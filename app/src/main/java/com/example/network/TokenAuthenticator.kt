package com.example.network

import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val cookieJar: PersistentCookieJar,
    private val baseUrl: () -> String,
) : Authenticator {
    private val refreshClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.endsWith("/auth/refresh") || responseCount(response) >= 2) return null
        return synchronized(lock) {
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = tokenManager.getToken()
            if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                return@synchronized response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }
            val refreshRequest = Request.Builder()
                .url(baseUrl() + "auth/refresh")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()
            refreshClient.newCall(refreshRequest).execute().use { refreshResponse ->
                if (!refreshResponse.isSuccessful) {
                    SessionManager.handleUnauthorized()
                    return@synchronized null
                }
                val raw = refreshResponse.body?.string() ?: run {
                    SessionManager.handleUnauthorized()
                    return@synchronized null
                }
                val body = runCatching { JSONObject(raw).optJSONObject("body") }.getOrNull() ?: run {
                    SessionManager.handleUnauthorized()
                    return@synchronized null
                }
                val token = body.optString("accessToken")
                if (token.isNullOrBlank()) {
                    SessionManager.handleUnauthorized()
                    return@synchronized null
                }
                tokenManager.saveSession(
                    token,
                    body.optLong("accessExpiresAt").takeIf { it > 0L },
                    body.optLong("refreshExpiresAt").takeIf { it > 0L },
                )
                response.request.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}

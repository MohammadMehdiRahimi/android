package com.example.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Request Interceptor in OkHttp - Equivalent to `axios.interceptors.request.use(...)` in Axios.
 * Automatically attaches Authorization header (Bearer Token), Accept, Content-Type, and App Metadata.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Standard HTTP headers
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept-Language", "fa") // Persian default locale

        val noAuth = originalRequest.header("No-Authentication") == "true"
        if (noAuth) {
            requestBuilder.removeHeader("No-Authentication")
        } else {
            // Inject Bearer token if user is logged in
            val token = tokenManager.getToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            // Inject Registration token if present
            val registrationToken = tokenManager.getRegistrationToken()
            if (!registrationToken.isNullOrBlank()) {
                requestBuilder.addHeader("X-Registration-Token", registrationToken)
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}

package com.example.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Response Interceptor in OkHttp - Equivalent to `axios.interceptors.response.use(...)` in Axios.
 * Handles HTTP status codes globally (401 Unauthorized, 403 Forbidden, 500 Server Errors, Refresh Tokens).
 */
class ResponseInterceptor(
    private val tokenManager: TokenManager,
    private val onUnauthorized: (() -> Unit)? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            // Re-throw network connectivity or timeout exceptions
            throw e
        }

        // Global response status checks
        when (response.code) {
            401 -> {
                // Token expired or invalid - clear token & trigger logout/refresh listener
                tokenManager.clearToken()
                onUnauthorized?.invoke()
            }
            403 -> {
                // Access forbidden
            }
            500, 502, 503 -> {
                // Server down or unexpected backend exception
            }
        }

        return response
    }
}

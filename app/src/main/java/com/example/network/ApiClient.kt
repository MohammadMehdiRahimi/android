package com.example.network

import android.content.Context
import com.example.API_BASE_URL
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * ApiClient - Central Network Manager combining Retrofit & OkHttp.
 * Direct Android equivalent of setting up an Axios instance (`axios.create(...)`) with Interceptors in web frontend.
 *
 * Features:
 * - Auth Interceptor (Attaches Authorization: Bearer <token> & headers)
 * - Response Interceptor (Global 401 handling, error processing)
 * - HttpLoggingInterceptor (Logs requests & responses in logcat like Axios logger)
 * - Moshi JSON Converter Factory
 * - Configurable Timeouts (30 seconds default)
 */
object ApiClient {
    /**
     * Single source of truth for every Shetab backend request.
     */
    private val baseUrl = normalizeBaseUrl(API_BASE_URL)
    
    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null
    private var tokenManager: TokenManager? = null
    private var persistentCookieJar: PersistentCookieJar? = null

    /**
     * Initialize the ApiClient with Context. Call once in Application class or MainActivity.
     */
    fun init(context: Context) {
        val appContext = context.applicationContext
        val tm = TokenManager(appContext)
        this.tokenManager = tm

        // 1. Http Logging Interceptor (Console Logger for debugging)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }

        // 2. Auth Request Interceptor (Axios Request Interceptor equivalent)
        val authInterceptor = AuthInterceptor(tm)
        val cookieJar = PersistentCookieJar(appContext)
        this.persistentCookieJar = cookieJar
        SessionManager.init(appContext, tm, cookieJar)
        val tokenAuthenticator = TokenAuthenticator(tm, cookieJar) { baseUrl }

        // 3. Response Interceptor (Axios Response Interceptor equivalent)
        val responseInterceptor = ResponseInterceptor(
            tokenManager = tm,
            onUnauthorized = {
                SessionManager.handleUnauthorized()
            }
        )

        // Build OkHttpClient
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .authenticator(tokenAuthenticator)
            .addInterceptor(MockDataInterceptor())
            .addInterceptor(authInterceptor)
            .addInterceptor(responseInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        this.okHttpClient = client

        // Build Moshi for Kotlin Data Class JSON conversion
        val moshi = Moshi.Builder()
            .add(DailyStudyTasksResponseJsonAdapter.Factory)
            .add(ManualStudyTaskResponseJsonAdapter.Factory)
            .add(StudyExecutionResponseJsonAdapter.Factory)
            .add(StudyTaskCatalogResponseJsonAdapter.Factory)
            .add(StudyExecutionEventJsonAdapter.Factory)
            .add(UpdateManualStudyTaskJsonAdapter.Factory)
            .add(MyGroupResponseJsonAdapter.Factory)
            .add(KotlinJsonAdapterFactory())
            .build()

        // Build Retrofit Instance
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Creates type-safe implementation of your API Service Interface.
     */
    fun <T> createService(serviceClass: Class<T>): T {
        val instance = retrofit ?: throw IllegalStateException(
            "ApiClient is not initialized. Call ApiClient.init(context) in Application or MainActivity."
        )
        return instance.create(serviceClass)
    }

    fun getTokenManager(): TokenManager? = tokenManager

    fun getBaseUrl(): String = baseUrl

    fun clearSession() {
        SessionManager.logout()
    }

    fun resolveUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        return baseUrl.trimEnd('/') + "/" + path.trimStart('/')
    }

    private fun normalizeBaseUrl(value: String): String {
        val normalized = value.trim()
        require(normalized.startsWith("http://") || normalized.startsWith("https://")) {
            "API_BASE_URL must start with http:// or https://"
        }
        return normalized.trimEnd('/') + "/"
    }

    /**
     * Shortcut getter for ApiService instance.
     */
    val apiService: ApiService by lazy {
        createService(ApiService::class.java)
    }
}

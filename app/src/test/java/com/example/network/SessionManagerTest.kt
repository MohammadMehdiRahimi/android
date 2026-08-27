package com.example.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SessionManagerTest {

    private lateinit var context: Context
    private lateinit var tokenManager: TokenManager
    private lateinit var cookieJar: PersistentCookieJar

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        tokenManager = TokenManager(context)
        cookieJar = PersistentCookieJar(context)
        SessionManager.init(context, tokenManager, cookieJar)
    }

    @Test
    fun `tokenManager clearAllData wipes all tokens and academic details`() {
        tokenManager.saveToken("sample_jwt_token")
        tokenManager.saveRefreshToken("sample_refresh_token")
        tokenManager.saveUserData("user_123", "09123456789", "STUDENT", "علی رضایی")
        tokenManager.saveUserAcademicInfo("ریاضی فیزیک", "دوازدهم")
        tokenManager.saveProfileData("علی رضایی", "رتبه ۱", "https://example.com/avatar.png", 500L)

        assertTrue(tokenManager.isLoggedIn())
        assertEquals("user_123", tokenManager.getUserId())
        assertEquals("ریاضی فیزیک", tokenManager.getUserMajor())
        assertEquals("دوازدهم", tokenManager.getUserGrade())

        tokenManager.clearAllData()

        assertFalse(tokenManager.isLoggedIn())
        assertNull(tokenManager.getToken())
        assertNull(tokenManager.getRefreshToken())
        assertNull(tokenManager.getUserId())
        assertNull(tokenManager.getUserPhone())
        assertNull(tokenManager.getUserFullName())
        assertNull(tokenManager.getUserMajor())
        assertNull(tokenManager.getUserGrade())
        assertNull(tokenManager.getUserTitle())
        assertNull(tokenManager.getProfileImageUrl())
        assertEquals(0L, tokenManager.getGlobalPoints())
    }

    @Test
    fun `SessionManager purgeAllDeviceData purges preferences and cookies`() {
        tokenManager.saveToken("token_to_purge")
        context.getSharedPreferences("shetab_onboarding_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_onboarded", true)
            .apply()

        SessionManager.purgeAllDeviceData()

        assertNull(tokenManager.getToken())
        val onboarded = context.getSharedPreferences("shetab_onboarding_prefs", Context.MODE_PRIVATE)
            .getBoolean("is_onboarded", false)
        assertFalse(onboarded)
    }

    @Test
    fun `SessionManager handleUnauthorized emits SessionExpired event`() = runTest {
        tokenManager.saveToken("token_to_expire")

        var receivedEvent: AuthEvent? = null
        backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            SessionManager.authEvents.collect {
                receivedEvent = it
            }
        }

        SessionManager.handleUnauthorized("نشست کاربری شما منقضی شده است.")

        assertTrue(receivedEvent is AuthEvent.SessionExpired)
        assertEquals("نشست کاربری شما منقضی شده است.", (receivedEvent as? AuthEvent.SessionExpired)?.message)
        assertNull(tokenManager.getToken())
    }

    @Test
    fun `ResponseInterceptor triggers onUnauthorized on HTTP 401 for protected endpoints`() {
        tokenManager.saveToken("valid_token")
        val unauthorizedTriggered = AtomicBoolean(false)

        val interceptor = ResponseInterceptor(
            tokenManager = tokenManager,
            onUnauthorized = {
                unauthorizedTriggered.set(true)
            }
        )

        val dummyRequest = Request.Builder()
            .url("https://api.weshetab.ir/users/me")
            .build()

        val mockChain = object : Interceptor.Chain {
            override fun request(): Request = dummyRequest
            override fun proceed(request: Request): Response {
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Unauthorized")
                    .body("{\"message\":\"Unauthorized\"}".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            override fun connection(): okhttp3.Connection? = null
            override fun call(): okhttp3.Call = throw NotImplementedError()
            override fun connectTimeoutMillis(): Int = 0
            override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
            override fun readTimeoutMillis(): Int = 0
            override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
            override fun writeTimeoutMillis(): Int = 0
            override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
        }

        val response = interceptor.intercept(mockChain)
        assertEquals(401, response.code)
        assertTrue(unauthorizedTriggered.get())
        assertNull(tokenManager.getToken())
    }
}

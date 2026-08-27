package com.example.network

import android.content.Context
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

sealed interface AuthEvent {
    data class SessionExpired(val message: String = "نشست کاربری شما منقضی شده است. لطفاً دوباره وارد شوید.") : AuthEvent
    data object LoggedOut : AuthEvent
}

/**
 * SessionManager handles global session lifecycle, reactive authorization events (401 unauthorized),
 * comprehensive device data purge (Tokens, Cookies, Local Room Database tables, Cache SharedPreferences),
 * and triggering immediate redirection to the login screen.
 */
object SessionManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var appContext: Context? = null
    private var tokenManager: TokenManager? = null
    private var cookieJar: PersistentCookieJar? = null

    private val isPurging = AtomicBoolean(false)
    private val lastPurgeTimestamp = AtomicLong(0L)

    private val _authEvents = MutableSharedFlow<AuthEvent>(replay = 0, extraBufferCapacity = 64)
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    fun init(context: Context, tokenManager: TokenManager, cookieJar: PersistentCookieJar) {
        this.appContext = context.applicationContext
        this.tokenManager = tokenManager
        this.cookieJar = cookieJar
    }

    /**
     * Handles 401 Unauthorized responses or expired refresh tokens.
     * Clears all local device data (Room DB, SharedPreferences, Cookies, Auth Tokens)
     * and emits an AuthEvent.SessionExpired to navigate the user to the login screen.
     */
    fun handleUnauthorized(message: String = "نشست کاربری شما منقضی شده است. لطفاً دوباره وارد شوید.") {
        val now = System.currentTimeMillis()
        // Prevent duplicate storms if multiple network calls fail simultaneously with 401
        if (now - lastPurgeTimestamp.get() < 2000L) {
            return
        }
        lastPurgeTimestamp.set(now)

        purgeAllDeviceData()
        _authEvents.tryEmit(AuthEvent.SessionExpired(message))
    }

    /**
     * Handles explicit user logout.
     * Clears all local device data and emits AuthEvent.LoggedOut.
     */
    fun logout() {
        val now = System.currentTimeMillis()
        lastPurgeTimestamp.set(now)
        purgeAllDeviceData()
        _authEvents.tryEmit(AuthEvent.LoggedOut)
    }

    /**
     * Thoroughly purges all device storage:
     * - TokenManager (JWT, refresh token, user profile, major, grade, expiry timestamps)
     * - PersistentCookieJar (Session cookies)
     * - AppDatabase (Room database tables: tasks, tickets, flashcards, etc.)
     * - Auxiliary SharedPreferences (onboarding, wizard, timer prefs, etc.)
     */
    fun purgeAllDeviceData() {
        // 1. Clear TokenManager
        tokenManager?.clearAllData()

        // 2. Clear Persistent HTTP Cookies
        cookieJar?.clear()

        // 3. Clear Auxiliary Shared Preferences
        appContext?.let { ctx ->
            val prefNames = listOf(
                "shetab_auth_prefs",
                "shetab_http_cookies",
                "shetab_onboarding_prefs",
                "shetab_wizard_prefs",
                "pomodoro_prefs",
                "study_plan_catalog_cache",
                "push_installation"
            )
            for (pref in prefNames) {
                runCatching {
                    ctx.getSharedPreferences(pref, Context.MODE_PRIVATE).edit().clear().apply()
                }
            }

            // 4. Asynchronously clear all Room Database tables
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    AppDatabase.getDatabase(ctx).clearAllTables()
                }
            }
        }
    }
}

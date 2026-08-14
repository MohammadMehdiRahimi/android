package com.example.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages JWT Auth Tokens and user details securely in SharedPreferences.
 * Works seamlessly with AuthInterceptor for dynamic Bearer token authorization.
 */
class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shetab_auth_prefs", Context.MODE_PRIVATE)
    private val _authVersion = MutableStateFlow(0L)
    val authVersion: StateFlow<Long> = _authVersion

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun saveSession(
        accessToken: String,
        accessExpiresAt: Long?,
        sessionExpiresAt: Long?,
    ) {
        prefs.edit()
            .putString("jwt_token", accessToken)
            .apply {
                if (accessExpiresAt != null) putLong("access_expires_at", accessExpiresAt)
                if (sessionExpiresAt != null) putLong("session_expires_at", sessionExpiresAt)
            }
            .apply()
    }

    fun saveUserData(
        id: String?,
        phone: String?,
        role: String?,
        fullName: String? = null,
    ) {
        val editor = prefs.edit()
        if (id != null) editor.putString("user_id", id)
        if (phone != null) editor.putString("user_phone", phone)
        if (role != null) editor.putString("user_role", role)
        if (!fullName.isNullOrBlank()) editor.putString("user_full_name", fullName.trim())
        editor.apply()
    }

    fun getUserId(): String? = prefs.getString("user_id", null)
    fun getUserPhone(): String? = prefs.getString("user_phone", null)
    fun getUserRole(): String? = prefs.getString("user_role", null)
    fun getUserFullName(): String? = prefs.getString("user_full_name", null)

    fun saveProfileData(
        fullName: String?,
        title: String?,
        profileImageUrl: String?,
        points: Long?,
    ) {
        prefs.edit().apply {
            if (!fullName.isNullOrBlank()) putString("user_full_name", fullName.trim())
            if (!title.isNullOrBlank()) putString("user_title", title.trim()) else remove("user_title")
            if (!profileImageUrl.isNullOrBlank()) putString("profile_image_url", profileImageUrl) else remove("profile_image_url")
            if (points != null) putLong("global_points", points)
        }.apply()
    }

    fun getUserTitle(): String? = prefs.getString("user_title", null)
    fun getProfileImageUrl(): String? = prefs.getString("profile_image_url", null)
    fun getGlobalPoints(): Long = prefs.getLong("global_points", 0L)
    fun getSessionExpiresAt(): Long? = prefs.getLong("session_expires_at", 0L).takeIf { it > 0L }

    fun updateSessionExpiry(value: Long?) {
        if (value != null && value > 0L) {
            prefs.edit().putLong("session_expires_at", value).apply()
        }
    }

    fun isLoggedIn(): Boolean {
        if (getToken().isNullOrBlank()) return false
        val expiry = getSessionExpiresAt() ?: return true
        if (expiry > System.currentTimeMillis()) return true
        clearToken()
        return false
    }

    fun clearToken() {
        prefs.edit()
            .remove("jwt_token")
            .remove("refresh_token")
            .remove("user_id")
            .remove("user_phone")
            .remove("user_role")
            .remove("user_full_name")
            .remove("user_title")
            .remove("profile_image_url")
            .remove("global_points")
            .remove("access_expires_at")
            .remove("session_expires_at")
            .apply()
        _authVersion.value = _authVersion.value + 1
    }

    fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }
}

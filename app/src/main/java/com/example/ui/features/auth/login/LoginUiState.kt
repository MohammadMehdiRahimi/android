package com.example.ui.features.auth.login

data class LoginUiState(
    val rawPhoneNumber: String = "",
    val displayPhoneNumber: String = "",
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val expiresIn: Int = 120
)

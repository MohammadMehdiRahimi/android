package com.example.ui.features.auth.otp

import com.example.ui.screens.toPersianDigits
import java.util.Locale

data class VerifyOtpUiState(
    val rawPhoneNumber: String = "",
    val formattedPhoneNumber: String = "",
    val otpCode: String = "",
    val remainingSeconds: Int = 120,
    val isTimerActive: Boolean = true,
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null,
    val isCodeComplete: Boolean = false,
    val isVerificationSuccess: Boolean = false,
    val isNewUser: Boolean = false,
    val registrationToken: String? = null
) {
    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            val raw = String.format(Locale.US, "%02d:%02d", minutes, seconds)
            return raw.toPersianDigits()
        }

    val resendButtonText: String
        get() {
            return if (isTimerActive && remainingSeconds > 0) {
                val min = remainingSeconds / 60
                val sec = remainingSeconds % 60
                if (min > 0) {
                    "ارسال مجدد کد ($min دقیقه بعد)".toPersianDigits()
                } else {
                    "ارسال مجدد کد ($sec ثانیه بعد)".toPersianDigits()
                }
            } else {
                "ارسال مجدد کد"
            }
        }
}

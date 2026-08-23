package com.example.ui.features.auth.otp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.network.OtpRequestDto
import com.example.network.OtpVerifyDto
import com.example.network.TokenManager
import com.example.network.safeApiCall
import com.example.ui.screens.convertPersianToEnglishDigits
import com.example.ui.screens.globalUserPhoneNumber
import com.example.ui.screens.toPersianDigits
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyOtpViewModel(
    initialPhone: String = ""
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyOtpUiState())
    val uiState: StateFlow<VerifyOtpUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val phone = initialPhone.ifBlank { globalUserPhoneNumber }
        setPhoneNumber(phone)
        startCountdownTimer(120)
    }

    fun setPhoneNumber(phone: String) {
        val cleanDigits = convertPersianToEnglishDigits(phone).filter { it.isDigit() }
        val normalized = when {
            cleanDigits.startsWith("9") && cleanDigits.length == 10 -> "0$cleanDigits"
            else -> cleanDigits
        }
        val formatted = formatPersianPhoneNumber(normalized)
        _uiState.update {
            it.copy(
                rawPhoneNumber = normalized,
                formattedPhoneNumber = formatted
            )
        }
    }

    private fun formatPersianPhoneNumber(phone: String): String {
        if (phone.length != 11) return phone.toPersianDigits()
        // Format as: 0912 345 6789
        val part1 = phone.substring(0, 4)
        val part2 = phone.substring(4, 7)
        val part3 = phone.substring(7, 11)
        return "$part1 $part2 $part3".toPersianDigits()
    }

    fun onOtpCodeChanged(newCode: String) {
        val englishDigits = convertPersianToEnglishDigits(newCode).filter { it.isDigit() }
        val limited = if (englishDigits.length > 6) englishDigits.substring(0, 6) else englishDigits
        val isComplete = limited.length == 6

        _uiState.update {
            it.copy(
                otpCode = limited,
                isCodeComplete = isComplete,
                errorMessage = null
            )
        }
    }

    fun startCountdownTimer(totalSeconds: Int = 120) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                remainingSeconds = totalSeconds,
                isTimerActive = totalSeconds > 0
            )
        }

        timerJob = viewModelScope.launch {
            var currentSeconds = totalSeconds
            while (currentSeconds > 0) {
                delay(1000L)
                currentSeconds--
                _uiState.update {
                    it.copy(
                        remainingSeconds = currentSeconds,
                        isTimerActive = currentSeconds > 0
                    )
                }
            }
        }
    }

    fun resendOtp(onSuccess: () -> Unit = {}) {
        val phone = _uiState.value.rawPhoneNumber
        if (phone.isBlank()) return
        if (_uiState.value.isTimerActive && _uiState.value.remainingSeconds > 0) return

        _uiState.update { it.copy(isResending = true, errorMessage = null) }

        viewModelScope.launch {
            val result = safeApiCall {
                ApiClient.apiService.requestOtp(OtpRequestDto(phone = phone))
            }
            when (result) {
                is NetworkResult.Success -> {
                    val expiresIn = result.data.body?.expiresIn ?: 120
                    _uiState.update {
                        it.copy(
                            isResending = false,
                            errorMessage = null
                        )
                    }
                    startCountdownTimer(expiresIn)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isResending = false,
                            errorMessage = result.message ?: "خطا در ارسال مجدد کد تأیید"
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _uiState.update {
                        it.copy(
                            isResending = false,
                            errorMessage = "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
                        )
                    }
                }
            }
        }
    }

    fun verifyCode(
        context: Context,
        onSuccess: (isNewUser: Boolean, registrationToken: String?) -> Unit
    ) {
        val currentState = _uiState.value
        val phone = currentState.rawPhoneNumber
        val code = currentState.otpCode

        if (code.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "لطفاً کد تأیید ۶ رقمی را به صورت کامل وارد کنید.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = safeApiCall {
                ApiClient.apiService.verifyOtp(
                    OtpVerifyDto(
                        phone = phone,
                        otp = code,
                        deviceType = "ANDROID"
                    )
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    val authData = result.data.body
                    if (authData?.isNew == true && !authData.registrationToken.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isVerificationSuccess = true,
                                isNewUser = true,
                                registrationToken = authData.registrationToken
                            )
                        }
                        onSuccess(true, authData.registrationToken)
                    } else if (authData != null && !authData.accessToken.isNullOrBlank()) {
                        val tokenManager = ApiClient.getTokenManager() ?: TokenManager(context)
                        tokenManager.saveSession(
                            authData.accessToken,
                            authData.accessExpiresAt,
                            authData.refreshExpiresAt
                        )
                        tokenManager.saveUserData(
                            id = authData.user?.id,
                            phone = authData.user?.phone ?: phone,
                            role = authData.user?.role,
                            fullName = authData.user?.fullName
                        )

                        val sharedPrefs = context.getSharedPreferences("shetab_onboarding_prefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit().apply {
                            putBoolean("is_logged_in", true)
                            authData.user?.fullName
                                ?.takeIf { it.isNotBlank() }
                                ?.let { putString("user_name", it.trim()) }
                        }.apply()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isVerificationSuccess = true,
                                isNewUser = false
                            )
                        }
                        onSuccess(false, null)
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "پاسخ نامعتبر از سرور"
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "کد وارد شده اشتباه یا منقضی شده است."
                        )
                    }
                }
                is NetworkResult.Exception -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "خطا در ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

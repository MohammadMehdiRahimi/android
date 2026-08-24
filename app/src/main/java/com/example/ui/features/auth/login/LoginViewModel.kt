package com.example.ui.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepositoryImpl
import com.example.domain.usecase.SendOtpUseCase
import com.example.network.ApiClient
import com.example.network.NetworkResult
import com.example.ui.screens.convertPersianToEnglishDigits
import com.example.ui.screens.globalUserPhoneNumber
import com.example.ui.screens.toPersianDigits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val sendOtpUseCase = SendOtpUseCase(
        AuthRepositoryImpl(
            apiService = ApiClient.apiService,
            tokenManager = ApiClient.getTokenManager()!!
        )
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        if (globalUserPhoneNumber.isNotBlank()) {
            onPhoneNumberChanged(globalUserPhoneNumber)
        }
    }

    fun onPhoneNumberChanged(newInput: String) {
        val englishDigits = convertPersianToEnglishDigits(newInput).filter { it.isDigit() }
        val limited = if (englishDigits.length > 11) englishDigits.substring(0, 11) else englishDigits
        val isValid = isValidIranianPhoneNumber(limited)

        _uiState.update {
            it.copy(
                rawPhoneNumber = limited,
                displayPhoneNumber = limited.toPersianDigits(),
                isValid = isValid,
                errorMessage = null
            )
        }
    }

    fun isValidIranianPhoneNumber(phone: String): Boolean {
        return (phone.length == 11 && phone.startsWith("09")) ||
                (phone.length == 10 && phone.startsWith("9"))
    }

    fun getNormalizedPhoneNumber(): String {
        val raw = _uiState.value.rawPhoneNumber
        return when {
            raw.startsWith("9") && raw.length == 10 -> "0$raw"
            else -> raw
        }
    }

    fun requestOtp(onSuccess: (phoneNumber: String) -> Unit = {}) {
        val phone = getNormalizedPhoneNumber()
        if (!isValidIranianPhoneNumber(phone)) {
            _uiState.update {
                it.copy(errorMessage = "لطفاً شماره موبایل معتبر (۱۱ رقمی) وارد کنید.")
            }
            return
        }

        globalUserPhoneNumber = phone
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            sendOtpUseCase.execute(phone).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOtpSent = true,
                                expiresIn = 120 // Default to 120s if not specified in response
                            )
                        }
                        onSuccess(phone)
                    }
                    is NetworkResult.Error -> {
                        val errorMsg = when (result.code) {
                            429 -> "تعداد درخواست‌ها بیش از حد مجاز است. لطفاً کمی صبر کنید."
                            400 -> "شماره موبایل وارد شده نامعتبر است."
                            else -> result.message ?: "خطا در ارسال کد تأیید"
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMsg
                            )
                        }
                    }
                    is NetworkResult.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "خطا در برقراری ارتباط با سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
                            )
                        }
                    }
                }
            }
        }
    }
}

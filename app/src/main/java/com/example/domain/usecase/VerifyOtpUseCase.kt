package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import com.example.network.OtpVerifyResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VerifyOtpUseCase(
    private val authRepository: AuthRepository
) {
    fun execute(phone: String, code: String): Flow<NetworkResult<OtpVerifyResponseDto>> {
        if (code.length != 6 || !code.all { it.isDigit() }) {
            return flow { emit(NetworkResult.Error(400, "کد تایید باید دقیقا ۶ رقم باشد", null)) }
        }
        val sanitizedPhone = sanitizePhoneNumber(phone)
        return authRepository.verifyOtp(sanitizedPhone, code)
    }

    private fun sanitizePhoneNumber(phone: String): String {
        var cleanPhone = phone.replace(Regex("[^\\d]"), "")
        if (cleanPhone.startsWith("09") && cleanPhone.length == 11) {
            cleanPhone = "98" + cleanPhone.substring(1)
        } else if (cleanPhone.startsWith("9") && cleanPhone.length == 10) {
            cleanPhone = "98" + cleanPhone
        } else if (cleanPhone.startsWith("+98")) {
            cleanPhone = cleanPhone.substring(1)
        } else if (cleanPhone.startsWith("0098")) {
            cleanPhone = cleanPhone.substring(2)
        }
        return cleanPhone
    }
}

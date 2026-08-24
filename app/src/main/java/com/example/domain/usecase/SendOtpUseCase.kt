package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.Flow

class SendOtpUseCase(
    private val authRepository: AuthRepository
) {
    fun execute(phone: String): Flow<NetworkResult<Unit>> {
        val sanitizedPhone = sanitizePhoneNumber(phone)
        return authRepository.sendOtp(sanitizedPhone)
    }

    fun sanitizePhoneNumber(phone: String): String {
        // Remove spaces, dashes, parentheses
        var cleanPhone = phone.replace(Regex("[^\\d]"), "")
        
        // Handle variations to match /^989\d{9}$/
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

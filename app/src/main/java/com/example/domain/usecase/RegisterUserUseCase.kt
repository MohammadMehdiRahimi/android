package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.AuthResponseDto
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) {
    fun execute(
        phone: String,
        registrationToken: String,
        fullName: String,
        grade: String,
        fieldOfStudy: String?,
        deviceType: String = "ANDROID"
    ): Flow<NetworkResult<AuthResponseDto>> {
        val cleanName = fullName.trim()
        if (cleanName.length < 2) {
            return flow { emit(NetworkResult.Error(400, "نام و نام خانوادگی باید حداقل ۲ حرف باشد.", null)) }
        }
        if (registrationToken.isBlank()) {
            return flow { emit(NetworkResult.Error(400, "توکن ثبت‌نام منقضی یا نامعتبر است. لطفاً مجدداً تلاش کنید.", null)) }
        }
        if (grade.isBlank()) {
            return flow { emit(NetworkResult.Error(400, "پایه تحصیلی الزامی است.", null)) }
        }

        val sanitizedPhone = sanitizePhoneNumber(phone)
        val sanitizedField = fieldOfStudy?.trim()?.takeIf { it.isNotEmpty() }

        return authRepository.register(
            phone = sanitizedPhone,
            registrationToken = registrationToken,
            fullName = cleanName,
            grade = grade,
            fieldOfStudy = sanitizedField,
            deviceType = deviceType
        )
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

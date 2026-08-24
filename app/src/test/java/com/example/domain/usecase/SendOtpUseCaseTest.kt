package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.network.OtpVerifyResponseDto
import com.example.network.AuthResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendOtpUseCaseTest {

    private val authRepository = object : AuthRepository {
        override fun sendOtp(phone: String): Flow<NetworkResult<Unit>> = flow {}
        override fun verifyOtp(phone: String, code: String): Flow<NetworkResult<OtpVerifyResponseDto>> = flow {}
        override fun register(
            phone: String,
            registrationToken: String,
            fullName: String,
            grade: String,
            fieldOfStudy: String?,
            deviceType: String
        ): Flow<NetworkResult<AuthResponseDto>> = flow {}
        override fun logout(): Flow<NetworkResult<Unit>> = flow {}
    }
    private val useCase = SendOtpUseCase(authRepository)

    @Test
    fun `sanitizePhoneNumber converts 09xxx to 989xxx format`() {
        val result = useCase.sanitizePhoneNumber("09123456789")
        assertEquals("989123456789", result)
    }

    @Test
    fun `sanitizePhoneNumber converts 9xxx to 989xxx format`() {
        val result = useCase.sanitizePhoneNumber("9123456789")
        assertEquals("989123456789", result)
    }

    @Test
    fun `sanitizePhoneNumber converts +989xxx to 989xxx format`() {
        val result = useCase.sanitizePhoneNumber("+989123456789")
        assertEquals("989123456789", result)
    }

    @Test
    fun `sanitizePhoneNumber converts 00989xxx to 989xxx format`() {
        val result = useCase.sanitizePhoneNumber("00989123456789")
        assertEquals("989123456789", result)
    }

    @Test
    fun `sanitizePhoneNumber removes spaces and dashes`() {
        val result = useCase.sanitizePhoneNumber("0912-345 67 89")
        assertEquals("989123456789", result)
    }
}

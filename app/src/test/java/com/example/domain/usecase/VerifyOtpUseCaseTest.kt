package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.example.network.OtpVerifyResponseDto
import com.example.network.AuthResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VerifyOtpUseCaseTest {

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
    private val useCase = VerifyOtpUseCase(authRepository)

    @Test
    fun `execute with code less than 6 digits returns error 400`() = runBlocking {
        val result = useCase.execute("09123456789", "12345").first()
        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }

    @Test
    fun `execute with code more than 6 digits returns error 400`() = runBlocking {
        val result = useCase.execute("09123456789", "1234567").first()
        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }

    @Test
    fun `execute with non-numeric code returns error 400`() = runBlocking {
        val result = useCase.execute("09123456789", "12345a").first()
        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }
}

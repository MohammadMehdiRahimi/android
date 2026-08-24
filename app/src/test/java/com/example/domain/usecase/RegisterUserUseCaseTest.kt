package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.AuthResponseDto
import com.example.network.NetworkResult
import com.example.network.OtpVerifyResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUserUseCaseTest {

    private var lastPhone: String? = null
    private var lastToken: String? = null
    private var lastName: String? = null
    private var lastGrade: String? = null
    private var lastField: String? = null
    private var lastDevice: String? = null

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
        ): Flow<NetworkResult<AuthResponseDto>> {
            lastPhone = phone
            lastToken = registrationToken
            lastName = fullName
            lastGrade = grade
            lastField = fieldOfStudy
            lastDevice = deviceType
            return flowOf(NetworkResult.Success(AuthResponseDto(statusCode = 200)))
        }
        override fun logout(): Flow<NetworkResult<Unit>> = flow {}
    }

    private val useCase = RegisterUserUseCase(authRepository)

    @Test
    fun `short full name returns 400 error`() = runBlocking {
        val result = useCase.execute(
            phone = "09123456789",
            registrationToken = "valid_token_12345678901234567890123456789012",
            fullName = "A",
            grade = "GRADE_10",
            fieldOfStudy = "MATHEMATICS"
        ).first()

        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }

    @Test
    fun `empty registration token returns 400 error`() = runBlocking {
        val result = useCase.execute(
            phone = "09123456789",
            registrationToken = "",
            fullName = "علی رضایی",
            grade = "GRADE_10",
            fieldOfStudy = "MATHEMATICS"
        ).first()

        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }

    @Test
    fun `empty grade returns 400 error`() = runBlocking {
        val result = useCase.execute(
            phone = "09123456789",
            registrationToken = "valid_token_12345678901234567890123456789012",
            fullName = "علی رضایی",
            grade = "",
            fieldOfStudy = "MATHEMATICS"
        ).first()

        assertTrue(result is NetworkResult.Error)
        assertEquals(400, (result as NetworkResult.Error).code)
    }

    @Test
    fun `valid registration forwards sanitized phone and key values`() = runBlocking {
        val result = useCase.execute(
            phone = "09123456789",
            registrationToken = "reg_token_abc_123456789012345678901234567890",
            fullName = "علی رضایی",
            grade = "GRADE_10",
            fieldOfStudy = "MATHEMATICS",
            deviceType = "ANDROID"
        ).first()

        assertTrue(result is NetworkResult.Success)
        assertEquals("989123456789", lastPhone)
        assertEquals("reg_token_abc_123456789012345678901234567890", lastToken)
        assertEquals("علی رضایی", lastName)
        assertEquals("GRADE_10", lastGrade)
        assertEquals("MATHEMATICS", lastField)
        assertEquals("ANDROID", lastDevice)
    }
}

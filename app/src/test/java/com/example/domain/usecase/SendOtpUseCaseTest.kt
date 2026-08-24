package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock

class SendOtpUseCaseTest {

    private val authRepository: AuthRepository = mock(AuthRepository::class.java)
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

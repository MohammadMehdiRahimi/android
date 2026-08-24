package com.example.domain.usecase

import com.example.data.repository.AuthRepository
import com.example.network.NetworkResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class VerifyOtpUseCaseTest {

    private val authRepository: AuthRepository = mock(AuthRepository::class.java)
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

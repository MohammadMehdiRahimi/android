package com.example.data.repository

import com.example.network.ApiService
import com.example.network.NetworkResult
import com.example.network.OtpRequestDto
import com.example.network.OtpVerifyDto
import com.example.network.OtpVerifyResponseDto
import com.example.network.safeApiCall
import com.example.network.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override fun sendOtp(phone: String): Flow<NetworkResult<Unit>> = flow {
        val result = safeApiCall { apiService.requestOtp(OtpRequestDto(phone = phone)) }
        when (result) {
            is NetworkResult.Success -> {
                emit(NetworkResult.Success(Unit))
            }
            is NetworkResult.Error -> {
                // Return mapped error (429, 400 will be handled in UseCase or UI, but we forward the generic NetworkResult.Error)
                emit(result)
            }
            is NetworkResult.Exception -> {
                emit(result)
            }
        }
    }

    override fun verifyOtp(phone: String, code: String): Flow<NetworkResult<OtpVerifyResponseDto>> = flow {
        val result = safeApiCall { apiService.verifyOtp(OtpVerifyDto(phone = phone, otp = code)) }
        when (result) {
            is NetworkResult.Success -> {
                val authData = result.data.body
                if (authData != null && !authData.accessToken.isNullOrBlank()) {
                    tokenManager.saveSession(
                        authData.accessToken,
                        authData.accessExpiresAt,
                        authData.refreshExpiresAt
                    )
                }
                emit(NetworkResult.Success(result.data))
            }
            is NetworkResult.Error -> {
                // Forward the error (e.g. 401, 429)
                emit(result)
            }
            is NetworkResult.Exception -> {
                emit(result)
            }
        }
    }
}

package com.example.data.repository

import com.example.network.ApiService
import com.example.network.AuthResponseDto
import com.example.network.NetworkResult
import com.example.network.OtpRequestDto
import com.example.network.OtpVerifyDto
import com.example.network.OtpVerifyResponseDto
import com.example.network.RegisterRequest
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

    override fun register(
        phone: String,
        registrationToken: String,
        fullName: String,
        grade: String,
        fieldOfStudy: String?,
        deviceType: String
    ): Flow<NetworkResult<AuthResponseDto>> = flow {
        val request = RegisterRequest(
            phone = phone,
            registrationToken = registrationToken,
            deviceType = deviceType,
            fullName = fullName.trim(),
            grade = grade,
            fieldOfStudy = fieldOfStudy?.ifBlank { null }
        )
        val result = safeApiCall { apiService.register(request) }
        when (result) {
            is NetworkResult.Success -> {
                val authData = result.data.body
                if (authData != null && !authData.accessToken.isNullOrBlank()) {
                    tokenManager.saveSession(
                        authData.accessToken,
                        authData.accessExpiresAt,
                        authData.refreshExpiresAt
                    )
                    tokenManager.saveUserData(
                        id = authData.user?.id,
                        phone = authData.user?.phone ?: phone,
                        role = authData.user?.role,
                        fullName = authData.user?.fullName ?: fullName
                    )
                    tokenManager.clearRegistrationToken()
                }
                emit(NetworkResult.Success(result.data))
            }
            is NetworkResult.Error -> emit(result)
            is NetworkResult.Exception -> emit(result)
        }
    }

    override fun logout(): Flow<NetworkResult<Unit>> = flow {
        val result = safeApiCall { apiService.logout() }
        when (result) {
            is NetworkResult.Success -> {
                // Ignore token clearing here, it should be done in ViewModel or API Client, but we can do it here too
                emit(NetworkResult.Success(Unit))
            }
            is NetworkResult.Error -> emit(result)
            is NetworkResult.Exception -> emit(result)
        }
    }
}

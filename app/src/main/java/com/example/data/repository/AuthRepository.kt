package com.example.data.repository

import com.example.network.AuthResponseDto
import com.example.network.NetworkResult
import com.example.network.OtpVerifyResponseDto
import com.example.network.RegisterRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sendOtp(phone: String): Flow<NetworkResult<Unit>>
    fun verifyOtp(phone: String, code: String): Flow<NetworkResult<OtpVerifyResponseDto>>
    fun register(
        phone: String,
        registrationToken: String,
        fullName: String,
        grade: String,
        fieldOfStudy: String?,
        deviceType: String = "ANDROID"
    ): Flow<NetworkResult<AuthResponseDto>>
    fun logout(): Flow<NetworkResult<Unit>>
}

package com.example.data.repository

import com.example.network.NetworkResult
import com.example.network.OtpVerifyResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sendOtp(phone: String): Flow<NetworkResult<Unit>>
    fun verifyOtp(phone: String, code: String): Flow<NetworkResult<OtpVerifyResponseDto>>
}

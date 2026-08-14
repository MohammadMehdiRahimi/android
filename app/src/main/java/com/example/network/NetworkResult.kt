package com.example.network

/**
 * A generic sealed class representing HTTP Network Results in Android.
 * Similar to Axios response state handling in web frontend.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String, val errorBody: String? = null) : NetworkResult<Nothing>()
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>()
}

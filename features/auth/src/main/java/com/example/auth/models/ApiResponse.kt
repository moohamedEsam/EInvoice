package com.example.auth.models

import com.example.common.models.Result

sealed interface ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>
    data class Error<T>(val error: String) : ApiResponse<T>
    data class Unknown<T>(val isSuccess: Boolean, val error: String, val data: T?) : ApiResponse<T>

    fun asResult(): Result<T> = when (this) {
        is Success -> Result.Success(data)
        is Error -> Result.Error(error)
        is Unknown -> if (isSuccess) Result.Success(data!!) else Result.Error(error)
    }
}
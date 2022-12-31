package com.example.auth.models

import com.example.common.models.Result
import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse<T>(val isSuccess: Boolean, val error: String?, val data: T?)

fun <T> ApiResponse<T>.asResult(): Result<T> = if (isSuccess)
    Result.Success(data!!)
else
    Result.Error(error)


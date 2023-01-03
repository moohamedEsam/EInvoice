package com.example.network.models

import com.example.common.models.Result
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse<T>(
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("error")
    val error: List<String>?,
    @SerialName("value")
    val data: T?
)

fun <T> ApiResponse<T>.asResult(): Result<T> = if (isSuccess)
    Result.Success(data!!)
else
    Result.Error(error?.joinToString(","))


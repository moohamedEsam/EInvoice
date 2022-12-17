package com.example.einvoice.data.models

interface ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>
    data class Error<T>(val error: String) : ApiResponse<T>
    data class Unknown<T>(val isSuccess: Boolean, val error: String, val data: T?) : ApiResponse<T>
}
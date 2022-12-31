package com.example.common.models

sealed interface Result<T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error<T>(val exception: String? = null) : Result<T>
    class Loading<T> : Result<T>
    class Empty<T> : Result<T>

    fun copy(data: T? = null, error: String? = null): Result<T> = when (this) {
        is Success -> Success(data ?: this.data)
        is Error -> Error(error ?: exception)
        is Loading -> Loading()
        is Empty -> Empty()
    }

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading()
        is Empty -> Empty()
    }
}
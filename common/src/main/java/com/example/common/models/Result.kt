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

    suspend fun ifFailure(block: suspend (String?) -> Unit): Result<T> {
        if (this is Error) block(exception)
        return this
    }

    suspend fun ifSuccess(block: suspend (T) -> Unit): Result<T> {
        if (this is Success) block(data)
        return this
    }

    suspend fun getSnackBarEvent(
        successMessage: String,
        successActionLabel: String? = null,
        successAction: suspend () -> Unit = {},
        errorActionLabel: String? = null,
        errorAction: suspend () -> Unit = {}
    ): SnackBarEvent =
        when (this) {
            is Success -> SnackBarEvent(
                message = successMessage,
                actionLabel = successActionLabel,
                action = successAction
            )
            is Error -> SnackBarEvent(
                message = exception ?: "Error",
                actionLabel = errorActionLabel,
                action = errorAction
            )
            else -> SnackBarEvent(message = successMessage)
        }
}
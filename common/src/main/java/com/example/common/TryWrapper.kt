package com.example.common

suspend fun <T> tryWrapper(block: suspend () -> Result<T>) =
    try {
        block()
    } catch (e: Exception) {
        Result.Error(e.localizedMessage)
    }

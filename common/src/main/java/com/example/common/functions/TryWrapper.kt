package com.example.common.functions

import com.example.common.models.Result

suspend fun <T> tryWrapper(block: suspend () -> Result<T>) =
    try {
        block()
    } catch (e: Exception) {
        Result.Error(e.localizedMessage)
    }

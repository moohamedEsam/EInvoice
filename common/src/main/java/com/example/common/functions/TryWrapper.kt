package com.example.common.functions

import android.util.Log
import com.example.common.models.Result

suspend fun <T> tryWrapper(block: suspend () -> Result<T>) =
    try {
        block()
    } catch (e: Exception) {
        Log.e("TryWrapper", "tryWrapper: ${e.message}")
        Log.e("TryWrapper", "tryWrapper: ${e.cause?.message}")
        Result.Error(e.localizedMessage)
    }

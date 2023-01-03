package com.example.common.functions

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.example.common.models.SnackBarEvent

suspend fun SnackbarHostState.handleSnackBarEvent(event: SnackBarEvent) {
    val result = showSnackbar(event.message, event.action, duration = SnackbarDuration.Short)
    if (result == SnackbarResult.ActionPerformed)
        event.actionCallback?.invoke()
}
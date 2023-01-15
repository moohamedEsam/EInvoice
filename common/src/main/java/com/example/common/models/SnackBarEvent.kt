package com.example.common.models

data class SnackBarEvent(
    val message: String,
    val actionLabel: String? = null,
    val action: (() -> Unit)? = null
)

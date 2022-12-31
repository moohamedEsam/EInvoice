package com.example.common.models

data class SnackBarEvent(
    val message: String,
    val action: String? = null,
    val actionCallback: (() -> Unit)? = null
)

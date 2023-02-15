package com.example.models.document

import androidx.compose.ui.graphics.Color


enum class DocumentStatus {
    Initial,
    Updated,
    SignError,
    Signed,
    InvalidSent,
    Submitted,
    Valid,
    Invalid,
    Rejected,
    Cancelled;

    fun getStatusColor(defaultColor: Color) = when (this) {
        SignError -> Color.Red
        InvalidSent -> Color.Red
        Submitted -> Color.Yellow
        Valid -> Color.Green
        Invalid -> Color.Red
        Rejected -> Color.LightGray
        Cancelled -> Color.LightGray
        else -> defaultColor
    }

    fun isSendable() = when (this) {
        Initial, Updated, SignError, Signed, InvalidSent -> true
        else -> false
    }

    fun isUpdatable() = isSendable() || this == Invalid

    fun isCancelable() = when (this) {
        Submitted, Valid -> true
        else -> false
    }
}
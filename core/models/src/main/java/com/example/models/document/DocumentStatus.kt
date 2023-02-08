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
}
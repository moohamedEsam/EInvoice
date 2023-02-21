package com.example.functions

import androidx.compose.ui.graphics.Color
import com.example.models.document.DocumentStatus

fun DocumentStatus.getStatusColor(defaultColor: Color) = when (this) {
    DocumentStatus.SignError -> Color.Red
    DocumentStatus.InvalidSent -> Color.Red
    DocumentStatus.Submitted -> Color.Yellow
    DocumentStatus.Valid -> Color.Green
    DocumentStatus.Invalid -> Color.Red
    DocumentStatus.Rejected -> Color.LightGray
    DocumentStatus.Cancelled -> Color.LightGray
    else -> defaultColor
}
package com.example.einvoicecomponents.textField

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.common.models.ValidationResult

@Composable
fun ValidationTrailingIcon(validation: ValidationResult) {
    if (validation is ValidationResult.Invalid) {
        Icon(
            imageVector = Icons.Default.RemoveCircle,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
    } else if (validation is ValidationResult.Valid) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Valid",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
package com.example.common.validators

import com.example.common.models.ValidationResult

fun notBlankValidator(value: String): ValidationResult {
    return when {
        value.isEmpty() -> ValidationResult.Empty
        value.isBlank() -> ValidationResult.Invalid("Cannot be empty")
        else -> ValidationResult.Valid
    }
}
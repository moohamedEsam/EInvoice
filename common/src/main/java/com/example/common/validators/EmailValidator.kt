package com.example.common.validators

import android.util.Patterns
import com.example.common.models.ValidationResult

fun String.validateEmail(): ValidationResult = when {
    isEmpty() -> ValidationResult.Empty
    isBlank() -> ValidationResult.Invalid("Email cannot be blank")
    !Patterns.EMAIL_ADDRESS.matcher(this).matches() -> ValidationResult.Invalid("Invalid email")
    else -> ValidationResult.Valid
}
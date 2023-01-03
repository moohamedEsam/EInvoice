package com.example.common.validators

import android.util.Patterns
import com.example.common.models.ValidationResult

fun validateEmail(email:String): ValidationResult = when {
    email.isEmpty() -> ValidationResult.Empty
    email.isBlank() -> ValidationResult.Invalid("Email cannot be blank")
    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult.Invalid("Invalid email")
    else -> ValidationResult.Valid
}
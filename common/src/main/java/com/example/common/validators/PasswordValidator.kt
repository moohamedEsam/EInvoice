package com.example.common.validators

import com.example.common.models.ValidationResult

const val PASSWORD_MIN_LENGTH = 6

fun validatePassword(password: String): ValidationResult = when {
    password.isEmpty() -> ValidationResult.Empty
    password.isBlank() -> ValidationResult.Invalid("Password cannot be blank")
    password.length < PASSWORD_MIN_LENGTH -> ValidationResult.Invalid("Password is too short")
    password.all { it.isDigit() } -> ValidationResult.Invalid("Password must contain at least one letter")
    else -> ValidationResult.Valid
}
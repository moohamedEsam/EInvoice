package com.example.common.validators

import com.example.common.models.ValidationResult

const val PASSWORD_MIN_LENGTH = 6

fun String.validatePassword(): ValidationResult = when {
    isEmpty() -> ValidationResult.Empty
    isBlank() -> ValidationResult.Invalid("Password cannot be blank")
    length < PASSWORD_MIN_LENGTH -> ValidationResult.Invalid("Password is too short")
    all { it.isDigit() } -> ValidationResult.Invalid("Password must contain at least one letter")
    else -> ValidationResult.Valid
}
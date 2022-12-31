package com.example.common.validators

import com.example.common.models.ValidationResult

const val USERNAME_MIN_LENGTH = 3
const val USERNAME_MAX_LENGTH = 20

fun String.validateUsername(): ValidationResult = when {
    isEmpty() -> ValidationResult.Empty
    length < USERNAME_MIN_LENGTH -> ValidationResult.Invalid("Username is too short")
    length > USERNAME_MAX_LENGTH -> ValidationResult.Invalid("Username is too long")
    isBlank() -> ValidationResult.Invalid("Username cannot be blank")
    else -> ValidationResult.Valid
}


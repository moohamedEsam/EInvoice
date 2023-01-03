package com.example.common.validators

import com.example.common.models.ValidationResult

const val USERNAME_MIN_LENGTH = 3
const val USERNAME_MAX_LENGTH = 20

fun validateUsername(username: String): ValidationResult = when {
    username.isEmpty() -> ValidationResult.Empty
    username.isBlank() -> ValidationResult.Invalid("Username cannot be blank")
    username.length < USERNAME_MIN_LENGTH -> ValidationResult.Invalid("Username is too short")
    username.length > USERNAME_MAX_LENGTH -> ValidationResult.Invalid("Username is too long")
    else -> ValidationResult.Valid
}


package com.example.common.validators

import com.example.common.models.ValidationResult

fun validatePhone(phone: String) = when {
    phone.isBlank() -> ValidationResult.Empty
    phone.any { !it.isDigit() } -> ValidationResult.Invalid("Phone number can only contain digits")
    phone.length < 10 -> ValidationResult.Invalid("Phone number must be at least 10 digits long")
    else -> ValidationResult.Valid
}
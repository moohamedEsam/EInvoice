package com.example.common.validators

import com.example.common.models.ValidationResult

fun percentValidator(value: String) = when (val result = numberValidator(value)) {
    is ValidationResult.Valid -> if (value.toDouble() > 100) ValidationResult.Invalid("Discount cannot be greater than 100%") else result
    else -> result
}
package com.example.common.models

sealed interface ValidationResult {
    object Valid : ValidationResult
    data class Invalid(val message: String) : ValidationResult
    object Empty : ValidationResult
}

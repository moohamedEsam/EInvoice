package com.example.common.validators

import android.util.Patterns
import com.example.common.models.ValidationResult

fun validateWebsite(value:String) = when {
    value.isBlank() -> ValidationResult.Empty
    Patterns.WEB_URL.matcher(value).matches() -> ValidationResult.Valid
    else -> ValidationResult.Invalid("Website is not valid")
}
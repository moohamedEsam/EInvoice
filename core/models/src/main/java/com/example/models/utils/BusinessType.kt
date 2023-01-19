package com.example.models.utils

@kotlinx.serialization.Serializable
enum class BusinessType {
    B, P, F;

    fun asString() = when (this) {
        B -> "Business"
        P -> "Personal"
        F -> "Foreign"
    }
}
package com.example.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Company(
    val name: String,
    val registrationNumber: String,
    val ceo: String,
    val phone: String,
    val website: String?,
    val id: String = UUID.randomUUID().toString()
)

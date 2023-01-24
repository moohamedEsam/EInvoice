package com.example.models.company

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Company(
    val name: String,
    val registrationNumber: String,
    val ceo: String,
    val phone: String,
    val website: String?,
    val settings: CompanySettings,
    val id: String = UUID.randomUUID().toString()
)

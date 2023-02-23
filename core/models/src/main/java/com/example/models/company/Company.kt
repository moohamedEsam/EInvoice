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
    val id: String = UUID.randomUUID().toString(),
    val isSynced: Boolean = false,
    val syncError: String? = null,
){
    companion object
}


fun Company.Companion.empty() = Company(
    name = "",
    registrationNumber = "",
    ceo = "",
    phone = "",
    website = null,
    settings = CompanySettings.empty()
)
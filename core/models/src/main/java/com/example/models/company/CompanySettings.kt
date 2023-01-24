package com.example.models.company

@kotlinx.serialization.Serializable
data class CompanySettings(
    val clientId: String,
    val clientSecret: String,
    val tokenPin: String,
    val taxActivityCode: String
) {
    companion object
}

fun CompanySettings.Companion.empty() =
    CompanySettings(
        clientId = "",
        clientSecret = "",
        tokenPin = "",
        taxActivityCode = ""
    )

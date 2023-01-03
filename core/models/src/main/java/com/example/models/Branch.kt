package com.example.models

import kotlinx.serialization.Serializable


@Serializable
data class Branch(
    val id: String,
    val name: String,
    val internalId: String,
    val companyId: String,
    val street: String,
    val city: String,
    val country: String,
    val governate: String,
    val postalCode: String,
    val regionCity: String,
    val buildingNumber: String,
    val floor: String,
    val room: String,
    val landmark: String,
    val additionalInformation: String
)

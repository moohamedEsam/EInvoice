package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Branch(
    val id: String,
    @SerialName("branchName")
    val name: String,
    @SerialName("branchId")
    val internalId: String,
    val companyId: String,
    val street: String,
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

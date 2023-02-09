package com.example.models.utils

import kotlinx.serialization.Serializable

@Serializable
data class Address(
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
){
    companion object
}

fun Address.Companion.empty() = Address(
    street = " ",
    country = " ",
    governate = " ",
    postalCode = " ",
    regionCity = " ",
    buildingNumber = " ",
    floor = " ",
    room = " ",
    landmark = " ",
    additionalInformation = " "
)

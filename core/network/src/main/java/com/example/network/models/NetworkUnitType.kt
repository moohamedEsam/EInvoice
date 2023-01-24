package com.example.network.models

import com.example.models.item.UnitType
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class NetworkUnitType(
    val code: String,
    @SerialName("desc_en")
    val name: String,
)

fun NetworkUnitType.asUnitType() = UnitType(
    code = code,
    name = name,
)
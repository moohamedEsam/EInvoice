package com.example.models.item

import kotlinx.serialization.SerialName
import java.util.UUID

@kotlinx.serialization.Serializable
data class UnitType(
    val code: String,
    @SerialName("desc_en")
    val name: String,
    val id: String = UUID.randomUUID().toString(),
)

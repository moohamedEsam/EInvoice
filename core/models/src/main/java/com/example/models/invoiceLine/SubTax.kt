package com.example.models.invoiceLine

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SubTax(
    val code: String,
    @SerialName("desc_en")
    val name: String,
)

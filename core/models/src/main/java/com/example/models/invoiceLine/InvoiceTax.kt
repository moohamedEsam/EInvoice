package com.example.models.invoiceLine

@kotlinx.serialization.Serializable
data class InvoiceTax(
    val rate: Float,
    val taxType: String,
    val taxSubType: String
)

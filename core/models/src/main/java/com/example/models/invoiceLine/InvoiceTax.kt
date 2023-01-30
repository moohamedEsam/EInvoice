package com.example.models.invoiceLine

@kotlinx.serialization.Serializable
data class InvoiceTax(
    val rate: Float,
    val taxTypeCode: String,
    val taxSubTypeCode: String
)

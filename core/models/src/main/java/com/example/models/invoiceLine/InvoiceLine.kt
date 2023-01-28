package com.example.models.invoiceLine

@kotlinx.serialization.Serializable
data class InvoiceLine(
    val id: String,
    val itemId: String,
    val quantity: Float,
    val unitValue: String,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String
)

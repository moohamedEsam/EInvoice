package com.example.models.invoiceLine

import com.example.models.item.Item

@kotlinx.serialization.Serializable
data class InvoiceLineView(
    val id: String,
    val item: Item,
    val quantity: Float,
    val unitValue: UnitValue,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String
)

fun InvoiceLineView.asInvoiceLine() = InvoiceLine(
    itemId = item.id,
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    documentId = documentId,
    id = id,
    taxes = taxes
)
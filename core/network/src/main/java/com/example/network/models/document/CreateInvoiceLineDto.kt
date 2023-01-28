package com.example.network.models.document

import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.InvoiceTax
import kotlinx.serialization.Serializable


@Serializable
data class CreateInvoiceLineDto(
    val itemId: String,
    val quantity: Float,
    val unitValue: String,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val valueDifference: Float = 0f,
    val itemsDiscount: Float = 0f,
)


fun InvoiceLineView.asCreateInvoiceLineDto() = CreateInvoiceLineDto(
    itemId = item.id,
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    taxes = taxes
)
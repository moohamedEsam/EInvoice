package com.example.network.models.document

import com.example.models.invoiceLine.InvoiceTax
import com.example.models.invoiceLine.UnitValue
import com.example.network.models.NetworkInvoiceLineView
import kotlinx.serialization.Serializable


@Serializable
data class CreateInvoiceLineDto(
    val itemId: String,
    val quantity: Float,
    val unitValue: NetworkUnitValue,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val valueDifference: Float = 0f,
    val itemsDiscount: Float = 0f,
)


fun NetworkInvoiceLineView.asCreateInvoiceLineDto() = CreateInvoiceLineDto(
    itemId = item.id,
    quantity = quantity,
    unitValue = unitValue.asNetworkUnitValue(),
    discountRate = discountRate,
    taxes = taxes
)
package com.example.network.models

import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.InvoiceTax
import com.example.models.invoiceLine.UnitValue

@kotlinx.serialization.Serializable
data class NetworkInvoiceLineView(
    val id: String,
    val item: NetworkItem,
    val quantity: Float,
    val unitValue: UnitValue,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String
)

fun NetworkInvoiceLineView.asInvoiceLineView() = InvoiceLineView(
    id = id,
    item = item.asItem(),
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    taxes = taxes,
    documentId = documentId
)

fun InvoiceLineView.asNetworkInvoiceLineView() = NetworkInvoiceLineView(
    id = id,
    item = item.asNetworkItem(),
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    taxes = taxes,
    documentId = documentId
)
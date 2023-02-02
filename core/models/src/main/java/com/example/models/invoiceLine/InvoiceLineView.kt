package com.example.models.invoiceLine

import com.example.models.item.Item
import com.example.models.item.empty

@kotlinx.serialization.Serializable
data class InvoiceLineView(
    val id: String,
    val item: Item,
    val quantity: Float,
    val unitValue: UnitValue,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String
){
    companion object
}

fun InvoiceLineView.asInvoiceLine() = InvoiceLine(
    itemId = item.id,
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    documentId = documentId,
    id = id,
    taxes = taxes
)

fun InvoiceLineView.Companion.empty() = InvoiceLineView(
    id = "",
    item = Item.empty(),
    quantity = 0f,
    unitValue = UnitValue.empty(),
    discountRate = 0f,
    taxes = emptyList(),
    documentId = ""
)

fun InvoiceLineView.getTotals(): InvoiceLineTotals {
    val total = quantity * unitValue.currencyEgp
    val discount = total * discountRate / 100
    val net = total - discount
    val tax = taxes.sumOf { it.rate * net / 100 }
    val totalWithTax = net + tax
    return InvoiceLineTotals(
        discount = discount,
        taxes = tax,
        total = total,
        net = net,
        totalWithTax = totalWithTax
    )
}
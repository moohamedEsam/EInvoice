package com.example.models.invoiceLine

data class InvoiceLineTotals(
    val discount: Double,
    val taxes: Double,
    val total: Double,
    val net: Double,
    val totalWithTax: Double
)

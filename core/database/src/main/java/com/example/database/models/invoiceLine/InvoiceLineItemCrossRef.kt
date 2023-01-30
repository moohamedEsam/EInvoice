package com.example.database.models.invoiceLine

import androidx.room.Entity

@Entity(
    primaryKeys = ["itemId", "invoiceLineId"],
    tableName = "InvoiceLineItemCrossRef"
)
data class InvoiceLineItemCrossRef(
    val itemId: String,
    val invoiceLineId: String
)

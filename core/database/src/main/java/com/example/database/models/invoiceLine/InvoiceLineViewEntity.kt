package com.example.database.models.invoiceLine

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.ItemEntity
import com.example.database.models.asItem
import com.example.models.invoiceLine.InvoiceLineView

data class InvoiceLineViewEntity(
    @Embedded val invoiceLine: InvoiceLineEntity,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "id"
    )
    val item: ItemEntity
)

fun InvoiceLineViewEntity.asInvoiceLineView() = InvoiceLineView(
    id = invoiceLine.id,
    quantity = invoiceLine.quantity,
    unitValue = invoiceLine.unitValue,
    discountRate = invoiceLine.discountRate,
    taxes = invoiceLine.taxes,
    documentId = invoiceLine.documentId,
    item = item.asItem()
)
package com.example.database.models.invoiceLine

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.ItemEntity
import com.example.database.models.asItem
import com.example.database.models.asItemEntity
import com.example.models.invoiceLine.InvoiceLineView


data class InvoiceLineViewEntity(
    @Embedded val invoiceLine: InvoiceLineEntity,

    @Relation(
        parentColumn = "itemId",
        entityColumn = "id",
        entity = ItemEntity::class
    )
    val item: ItemEntity
)

fun InvoiceLineView.asInvoiceLineViewEntity() = InvoiceLineViewEntity(
    invoiceLine = InvoiceLineEntity(
        id = id,
        quantity = quantity,
        unitValue = unitValue,
        discountRate = discountRate,
        taxes = taxes,
        documentId = documentId,
        itemId = item.id
    ),
    item = item.asItemEntity()
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

fun InvoiceLineViewEntity.asInvoiceLineEntity() = InvoiceLineEntity(
    id = invoiceLine.id,
    quantity = invoiceLine.quantity,
    unitValue = invoiceLine.unitValue,
    discountRate = invoiceLine.discountRate,
    taxes = invoiceLine.taxes,
    documentId = invoiceLine.documentId,
    itemId = item.id
)
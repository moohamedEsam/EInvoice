package com.example.database.models.invoiceLine

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.models.ItemEntity
import com.example.database.models.document.DocumentEntity
import com.example.models.invoiceLine.InvoiceLine
import com.example.models.invoiceLine.InvoiceTax
import com.example.models.invoiceLine.UnitValue
import java.util.UUID

@Entity(
    tableName = "InvoiceLine",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("itemId"),
        Index("documentId")
    ]
)
data class InvoiceLineEntity(
    val itemId: String,
    val quantity: Float,
    @Embedded val unitValue: UnitValue,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)

fun InvoiceLineEntity.asInvoiceLine() = InvoiceLine(
    itemId = itemId,
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    taxes = taxes,
    documentId = documentId,
    id = id
)

fun InvoiceLine.asInvoiceLineEntity() = InvoiceLineEntity(
    itemId = itemId,
    quantity = quantity,
    unitValue = unitValue,
    discountRate = discountRate,
    taxes = taxes,
    documentId = documentId,
    id = id
)
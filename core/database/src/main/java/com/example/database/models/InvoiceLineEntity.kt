package com.example.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.models.invoiceLine.InvoiceTax
import java.util.UUID

@Entity(
    tableName = "InvoiceLine",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.RESTRICT
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
    val unitValue: String,
    val discountRate: Float,
    val taxes: List<InvoiceTax>,
    val documentId: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)

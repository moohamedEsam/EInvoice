package com.example.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "Document",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["issuerId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("issuerId"),
        Index("receiverId"),
        Index("branchId")
    ]
)
data class DocumentEntity(
    val issuerId: String,
    val receiverId: String,
    val branchId: String,
    val internalId: String,
    val date: Date,
    val referencedDocument: String?,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
)

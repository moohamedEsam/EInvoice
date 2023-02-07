package com.example.database.models.document

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.models.branch.BranchEntity
import com.example.database.models.ClientEntity
import com.example.database.models.company.CompanyEntity
import com.example.models.document.Document
import com.example.models.document.DocumentStatus
import java.util.*

@Entity(
    tableName = "Document",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["issuerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
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
    val documentType: String,
    val status: DocumentStatus,
    val error: String? = null,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
)


fun DocumentEntity.asDocument() = Document(
    id = id,
    issuerId = issuerId,
    receiverId = receiverId,
    branchId = branchId,
    internalId = internalId,
    date = date,
    referencedDocument = referencedDocument,
    documentType = documentType,
    status = status,
    error = error,
)

fun Document.asDocumentEntity(
    isCreated: Boolean = false,
    isUpdated: Boolean = false,
    isDeleted: Boolean = false
) = DocumentEntity(
    issuerId = issuerId,
    receiverId = receiverId,
    branchId = branchId,
    internalId = internalId,
    date = date,
    referencedDocument = referencedDocument,
    id = id,
    isCreated = isCreated,
    isUpdated = isUpdated,
    isDeleted = isDeleted,
    documentType = documentType,
    status = status,
    error = error,
)
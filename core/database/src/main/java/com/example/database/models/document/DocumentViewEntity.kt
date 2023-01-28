package com.example.database.models.document

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.BranchEntity
import com.example.database.models.ClientEntity
import com.example.database.models.asBranch
import com.example.database.models.asClient
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.asCompany
import com.example.database.models.invoiceLine.InvoiceLineViewEntity
import com.example.database.models.invoiceLine.asInvoiceLineView
import com.example.models.document.DocumentView

data class DocumentViewEntity(
    @Embedded val documentEntity: DocumentEntity,

    @Relation(
        parentColumn = "branchId",
        entityColumn = "id"
    )
    val branch: BranchEntity,

    @Relation(
        parentColumn = "receiverId",
        entityColumn = "id"
    )
    val client: ClientEntity,

    @Relation(
        parentColumn = "issuerId",
        entityColumn = "id"
    )
    val companyEntity: CompanyEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "documentId"
    )
    val invoices: List<InvoiceLineViewEntity>,
)


fun DocumentViewEntity.asDocumentView() = DocumentView(
    id = documentEntity.id,
    date = documentEntity.date,
    client = client.asClient(),
    branch = branch.asBranch(),
    invoices = invoices.map { it.asInvoiceLineView() },
    company = companyEntity.asCompany(),
    internalId = documentEntity.internalId,
    documentType = documentEntity.documentType,
    referencedDocument = documentEntity.referencedDocument,
    status = documentEntity.status,
    error = documentEntity.error,
)
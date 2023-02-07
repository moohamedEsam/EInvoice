package com.example.database.models.document

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.*
import com.example.database.models.branch.BranchEntity
import com.example.database.models.branch.asBranch
import com.example.database.models.branch.asBranchEntity
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.asCompany
import com.example.database.models.company.asCompanyEntity
import com.example.database.models.invoiceLine.InvoiceLineEntity
import com.example.database.models.invoiceLine.InvoiceLineViewEntity
import com.example.database.models.invoiceLine.asInvoiceLineView
import com.example.database.models.invoiceLine.asInvoiceLineViewEntity
import com.example.models.document.DocumentView


data class DocumentViewEntity(
    @Embedded val documentEntity: DocumentEntity,

    @Relation(
        parentColumn = "branchId",
        entityColumn = "id",
        entity = BranchEntity::class
    )
    val branch: BranchEntity,

    @Relation(
        parentColumn = "receiverId",
        entityColumn = "id",
        entity = ClientEntity::class
    )
    val client: ClientEntity,

    @Relation(
        parentColumn = "issuerId",
        entityColumn = "id",
        entity = CompanyEntity::class
    )
    val companyEntity: CompanyEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "documentId",
        entity = InvoiceLineEntity::class
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

fun DocumentView.asDocumentViewEntity() = DocumentViewEntity(
    documentEntity = DocumentEntity(
        id = id,
        date = date,
        receiverId = client.id,
        branchId = branch.id,
        issuerId = company.id,
        internalId = internalId,
        documentType = documentType,
        referencedDocument = referencedDocument,
        status = status,
        error = error,
    ),
    branch = branch.asBranchEntity(),
    client = client.asClientEntity(),
    companyEntity = company.asCompanyEntity(),
    invoices = invoices.map { it.asInvoiceLineViewEntity() }
)
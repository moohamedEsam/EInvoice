package com.example.network.models.document

import com.example.customSerializers.DateSerializer
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.InvoiceLineView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DocumentViewDto(
    val id: String,
    val branch: Branch,
    val company: Company,
    val client: Client,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
    val documentType: String,
    val referencedDocument: String?,
    val invoices: List<InvoiceLineView>,
    val error: String? = null,
    val status: Int,
)

fun DocumentViewDto.asDocumentView() = DocumentView(
    id = id,
    branch = branch,
    company = company,
    client = client,
    internalId = internalId,
    date = date,
    documentType = documentType,
    referencedDocument = referencedDocument,
    invoices = invoices,
    error = error,
    status = DocumentStatus.values().first { it.ordinal == status },
)


fun DocumentView.asDocumentViewDto() = DocumentViewDto(
    id = id,
    branch = branch,
    company = company,
    client = client,
    internalId = internalId,
    date = date,
    documentType = documentType,
    referencedDocument = referencedDocument,
    invoices = invoices,
    error = error,
    status = status.ordinal,
)
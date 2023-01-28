package com.example.models.document

import com.example.customSerializers.DateSerializer
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.invoiceLine.InvoiceLineView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DocumentView(
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
    val status: DocumentStatus,
)

fun DocumentView.asDocument() = Document(
    id = id,
    issuerId = company.id,
    receiverId = client.id,
    branchId = branch.id,
    internalId = internalId,
    date = date,
    referencedDocument = referencedDocument,
    documentType = documentType,
    status = status,
)
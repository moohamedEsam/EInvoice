package com.example.network.models.document

import com.example.customSerializers.DateSerializer
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.InvoiceLineView
import com.example.network.models.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NetworkDocumentView(
    val id: String,
    val branch: Branch,
    val company: Company,
    val client: NetworkClient,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
    val documentType: String,
    val referencedDocument: String?,
    val invoices: List<NetworkInvoiceLineView>,
    val error: String? = null,
    val status: Int,
)

fun NetworkDocumentView.asDocumentView() = DocumentView(
    id = id,
    branch = branch,
    company = company,
    client = client.asClient(),
    internalId = internalId,
    date = date,
    documentType = documentType,
    referencedDocument = referencedDocument,
    invoices = invoices.map { it.asInvoiceLineView() },
    error = error,
    status = DocumentStatus.values().first { it.ordinal == status },
)


fun DocumentView.asNetworkDocumentView() = NetworkDocumentView(
    id = id,
    branch = branch,
    company = company,
    client = client.asNetworkClient(),
    internalId = internalId,
    date = date,
    documentType = documentType,
    referencedDocument = referencedDocument,
    invoices = invoices.map { it.asNetworkInvoiceLineView() },
    error = error,
    status = status.ordinal,
)
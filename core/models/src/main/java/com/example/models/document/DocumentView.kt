package com.example.models.document

import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.empty
import com.example.models.invoiceLine.InvoiceLineView
import java.util.Date

data class DocumentView(
    val id: String,
    val branch: Branch,
    val company: Company,
    val client: Client,
    val internalId: String,
    val date: Date,
    val documentType: String,
    val referencedDocument: String? = null,
    val invoices: List<InvoiceLineView>,
    val error: String? = null,
    val status: DocumentStatus = DocumentStatus.Initial,
    val isSynced: Boolean = false,
    val syncError: String? = null,
){
    companion object
}

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

fun DocumentView.Companion.empty() = DocumentView(
    id = "",
    branch = Branch.empty(),
    company = Company.empty(),
    client = Client.empty(),
    internalId = "",
    date = Date(),
    documentType = "",
    referencedDocument = null,
    invoices = emptyList(),
    error = null,
    status = DocumentStatus.Initial,
)
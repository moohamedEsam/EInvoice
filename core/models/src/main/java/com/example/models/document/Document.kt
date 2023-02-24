package com.example.models.document

import java.util.Date

data class Document(
    val id: String,
    val issuerId: String,
    val receiverId: String,
    val branchId: String,
    val internalId: String,
    val date: Date,
    val referencedDocument: String?,
    val documentType: String,
    val status: DocumentStatus,
    val error: String? = null,
    val isSynced: Boolean = false,
    val syncError: String? = null,
){
    companion object
}

fun Document.Companion.empty() = Document(
    id = "",
    issuerId = "",
    receiverId = "",
    branchId = "",
    internalId = "",
    date = Date(),
    referencedDocument = null,
    documentType = "",
    status = DocumentStatus.Initial
)
package com.example.models.document

import com.example.customSerializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Document(
    val id: String,
    val issuerId: String,
    val receiverId: String,
    val branchId: String,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
    val referencedDocument: String?,
    val documentType: String,
    val status: DocumentStatus,
    val error: String? = null,
)

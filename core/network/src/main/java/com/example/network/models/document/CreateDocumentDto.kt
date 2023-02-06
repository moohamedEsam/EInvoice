package com.example.network.models.document

import com.example.network.serializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateDocumentDto(
    val branchId: String,
    val issuerId: String,
    val receiverId: String,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
    val invoices: List<CreateInvoiceLineDto>
)

fun NetworkDocumentView.asCreateDocumentDto() = CreateDocumentDto(
    branchId = branch.id,
    issuerId = company.id,
    receiverId = client.id,
    internalId = internalId,
    date = date,
    invoices = invoices.map { it.asCreateInvoiceLineDto() }
)
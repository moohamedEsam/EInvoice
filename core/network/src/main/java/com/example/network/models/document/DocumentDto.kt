package com.example.network.models.document

import com.example.customSerializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

data class DocumentDto(
    val id: String,
    val branchName: String,
    val issuerName: String,
    val receiverName: String,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
)

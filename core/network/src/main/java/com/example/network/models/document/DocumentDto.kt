package com.example.network.models.document

import com.example.customSerializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class DocumentDto(
    val id: String,
    val branchName: String?,
    val companyName: String?,
    val clientName: String?,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
)

package com.example.network.models.document

import com.example.network.serializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NetworkDocument(
    val id: String,
    val branchName: String?,
    val companyName: String?,
    val clientName: String?,
    val internalId: String,
    @SerialName("dateTimeIssued")
    @Serializable(with = DateSerializer::class)
    val date: Date,
    val status:Int
){
    companion object
}

fun NetworkDocument.Companion.empty() = NetworkDocument(
    id = "",
    branchName = "",
    companyName = "",
    clientName = "",
    internalId = "",
    date = Date(),
    status = 0
)

package com.example.models.item

import com.example.models.utils.TaxStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val status: TaxStatus,
    val itemCode: String,
    val unitTypeCode: String,
    val branchId: String
)

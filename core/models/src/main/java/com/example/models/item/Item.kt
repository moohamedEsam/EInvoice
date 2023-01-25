package com.example.models.item

import com.example.models.utils.TaxStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

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
) {
    companion object
}

fun Item.Companion.empty() = Item(
    id = UUID.randomUUID().toString(),
    name = "",
    description = "",
    price = 0.0,
    status = TaxStatus.Taxable,
    itemCode = "",
    unitTypeCode = "",
    branchId = ""
)
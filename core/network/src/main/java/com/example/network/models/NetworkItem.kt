package com.example.network.models

import com.example.models.item.Item
import com.example.models.utils.TaxStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkItem(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("defaultPrice")
    val price: Double,
    @SerialName("itemType")
    val status: Int,
    val itemCode: String,
    val unitTypeCode: String,
    val branchId: String
)

fun NetworkItem.asItem() = Item(
    id = id,
    name = name,
    description = description,
    price = price,
    status = TaxStatus.values().first { it.ordinal == status },
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId
)

fun Item.asNetworkItem() = NetworkItem(
    id = id,
    name = name,
    description = description,
    price = price,
    status = status.ordinal,
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId
)
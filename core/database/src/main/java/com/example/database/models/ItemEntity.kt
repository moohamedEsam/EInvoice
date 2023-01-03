package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.Item
import com.example.models.utils.TaxStatus
import java.util.*

@Entity(tableName = "Item")
data class ItemEntity(
    val name: String,
    val description: String,
    val price: Double,
    val status: TaxStatus,
    val itemCode: String,
    val unitTypeCode: String,
    val branchId: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

fun ItemEntity.asItem() = Item(
    name = name,
    description = description,
    price = price,
    status = status,
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId,
    id = id
)

fun Item.asItemEntity() = ItemEntity(
    name = name,
    description = description,
    price = price,
    status = status,
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId,
    id = id
)
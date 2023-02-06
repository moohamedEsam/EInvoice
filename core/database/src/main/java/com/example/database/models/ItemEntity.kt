package com.example.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.models.item.Item
import com.example.models.utils.TaxStatus
import java.util.*

@Entity(
    tableName = "Item",
    foreignKeys = [
        ForeignKey(
            entity = BranchEntity::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("branchId")
    ]
)
data class ItemEntity(
    val name: String,
    val description: String,
    val price: Double,
    val status: TaxStatus,
    val itemCode: String,
    val unitTypeCode: String,
    val internalCode: String,
    val branchId: String,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
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
    id = id,
    internalCode = internalCode
)

fun Item.asItemEntity(
    isCreated: Boolean = false,
    isUpdated: Boolean = false,
    isDeleted: Boolean = false
) = ItemEntity(
    name = name,
    description = description,
    price = price,
    status = status,
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId,
    id = id,
    isCreated = isCreated,
    isUpdated = isUpdated,
    isDeleted = isDeleted,
    internalCode = internalCode
)
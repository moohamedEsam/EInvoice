package com.example.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.models.branch.BranchEntity
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
    override val isCreated: Boolean = false,
    override val isUpdated: Boolean = false,
    override val isDeleted: Boolean = false,
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    override val syncError: String? = null,
    override val isSynced: Boolean = false
) : DataEntity

fun ItemEntity.asItem() = Item(
    name = name,
    description = description,
    price = price,
    status = status,
    itemCode = itemCode,
    unitTypeCode = unitTypeCode,
    branchId = branchId,
    id = id,
    internalCode = internalCode,
    isSynced = isSynced,
    syncError = syncError
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
package com.example.database.models.branch

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.ItemEntity
import com.example.database.models.asItem
import com.example.models.branch.BranchView

data class BranchViewEntity(
    @Embedded val branch: BranchEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "branchId"
    )
    val items: List<ItemEntity>,
)

fun BranchViewEntity.asBranchView() = BranchView(
    branch = branch.asBranch(),
    items = items.map { it.asItem() }
)

fun BranchViewEntity.removeDeleted() = copy(
    items = items.filter { !it.isDeleted }
)
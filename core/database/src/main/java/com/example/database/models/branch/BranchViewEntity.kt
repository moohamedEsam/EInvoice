package com.example.database.models.branch

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.ItemEntity
import com.example.database.models.asItem
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.asCompany
import com.example.models.branch.BranchView
import com.example.models.company.Company

data class BranchViewEntity(
    @Embedded val branch: BranchEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "branchId"
    )
    val items: List<ItemEntity>,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: CompanyEntity,
)

fun BranchViewEntity.asBranchView() = BranchView(
    branch = branch.asBranch(),
    items = items.map { it.asItem() },
    company = company.asCompany(),
)

fun BranchViewEntity.removeDeleted() = copy(
    items = items.filter { !it.isDeleted }
)
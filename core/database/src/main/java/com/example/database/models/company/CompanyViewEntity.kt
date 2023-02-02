package com.example.database.models.company

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.BranchEntity
import com.example.database.models.ClientEntity
import com.example.database.models.asBranch
import com.example.database.models.asClient
import com.example.models.company.CompanyView

data class CompanyViewEntity(
    @Embedded val companyEntity: CompanyEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "companyId",
    )
    val branches: List<BranchEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "companyId",
    )
    val clients:List<ClientEntity>,
)

fun CompanyViewEntity.asCompanyView() = CompanyView(
    branches = branches.map { it.asBranch() },
    clients = clients.map { it.asClient() },
    company = companyEntity.asCompany(),
)
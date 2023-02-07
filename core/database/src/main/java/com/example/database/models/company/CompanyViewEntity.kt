package com.example.database.models.company

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.ClientEntity
import com.example.database.models.asClient
import com.example.database.models.branch.BranchEntity
import com.example.database.models.branch.asBranch
import com.example.database.models.document.DocumentEntity
import com.example.database.models.document.asDocument
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

    @Relation(
        parentColumn = "id",
        entityColumn = "issuerId",
    )
    val documents: List<DocumentEntity>,
)

fun CompanyViewEntity.asCompanyView() = CompanyView(
    branches = branches.map { it.asBranch() },
    clients = clients.map { it.asClient() },
    company = companyEntity.asCompany(),
    documents = documents.map { it.asDocument() },
)
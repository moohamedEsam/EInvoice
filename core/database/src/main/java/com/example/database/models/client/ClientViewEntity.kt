package com.example.database.models.client

import androidx.room.Embedded
import androidx.room.Relation
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.asCompany
import com.example.models.ClientView

data class ClientViewEntity(
    @Embedded val client: ClientEntity,
    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: CompanyEntity
)



fun ClientViewEntity.asClientView() = ClientView(
    client = client.asClient(),
    company = company.asCompany()
)
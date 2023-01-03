package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.Client
import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import java.util.UUID

@Entity(tableName = "Client")
data class ClientEntity(
    val registrationNumber: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address,
    val businessType: BusinessType,
    val taxStatus: TaxStatus,
    val companyId: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

fun ClientEntity.asClient() = Client(
    id = id,
    registrationNumber = registrationNumber,
    name = name,
    email = email,
    phone = phone,
    address = address,
    businessType = businessType,
    taxStatus = taxStatus,
    companyId = companyId
)

fun Client.asClientEntity() = ClientEntity(
    registrationNumber = registrationNumber,
    name = name,
    email = email,
    phone = phone,
    address = address,
    businessType = businessType,
    taxStatus = taxStatus,
    companyId = companyId,
    id = id
)
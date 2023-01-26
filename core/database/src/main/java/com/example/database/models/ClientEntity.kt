package com.example.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.models.Client
import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import java.util.UUID

@Entity(
    tableName = "Client",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("companyId")]
)
data class ClientEntity(
    val registrationNumber: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address?,
    val businessType: BusinessType,
    val status: TaxStatus,
    val companyId: String,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
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
    status = status,
    companyId = companyId
)

fun Client.asClientEntity(
    isCreated: Boolean = false,
    isUpdated: Boolean = false,
    isDeleted: Boolean = false
) = ClientEntity(
    registrationNumber = registrationNumber,
    name = name,
    email = email,
    phone = phone,
    address = address,
    businessType = businessType,
    status = status,
    companyId = companyId,
    id = id,
    isCreated = isCreated,
    isUpdated = isUpdated,
    isDeleted = isDeleted
)
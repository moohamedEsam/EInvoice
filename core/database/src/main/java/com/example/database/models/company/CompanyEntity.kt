package com.example.database.models.company

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.database.models.DataEntity
import com.example.models.company.Company
import com.example.models.company.CompanySettings
import java.util.UUID


@Entity(tableName = "Company")
data class CompanyEntity(
    val name: String,
    val registrationNumber: String,
    val ceo: String,
    val phone: String,
    val website: String?,
    @Embedded val settings: CompanySettings,
    override val isDeleted: Boolean = false,
    override val isUpdated: Boolean = false,
    override val isCreated: Boolean = false,
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    override val isSynced: Boolean = false,
    override val syncError: String? = null,
) : DataEntity

fun CompanyEntity.asCompany() = Company(
    name = name,
    registrationNumber = registrationNumber,
    ceo = ceo,
    phone = phone,
    website = website,
    id = id,
    settings = settings,
    isSynced = isSynced,
    syncError = syncError,
)

fun Company.asCompanyEntity(
    isCreated: Boolean = false,
    isUpdated: Boolean = false,
    isDeleted: Boolean = false
) = CompanyEntity(
    name = name,
    registrationNumber = registrationNumber,
    ceo = ceo,
    phone = phone,
    website = website,
    id = id,
    settings = settings,
    isCreated = isCreated,
    isUpdated = isUpdated,
    isDeleted = isDeleted,
    isSynced = isSynced,
    syncError = syncError,
)
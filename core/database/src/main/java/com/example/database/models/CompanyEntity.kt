package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val settings: CompanySettings,
    val isDeleted: Boolean = false,
    val isUpdated: Boolean = false,
    val isCreated: Boolean = false,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

fun CompanyEntity.asCompany() = Company(
    name = name,
    registrationNumber = registrationNumber,
    ceo = ceo,
    phone = phone,
    website = website,
    id = id,
    settings = settings
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
    isDeleted = isDeleted
)
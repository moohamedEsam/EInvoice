package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.Company
import java.util.UUID


@Entity(tableName = "Company")
data class CompanyEntity(
    val name: String,
    val registrationNumber: String,
    val ceo: String,
    val phone: String,
    val website: String?,
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
    id = id
)

fun Company.asCompanyEntity() = CompanyEntity(
    name = name,
    registrationNumber = registrationNumber,
    ceo = ceo,
    phone = phone,
    website = website,
    id = id
)
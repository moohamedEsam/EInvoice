package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.Branch
import java.util.UUID


@Entity(tableName = "Branch")
data class BranchEntity(
    val name: String,
    val internalId: String,
    val companyId: String,
    val street: String,
    val city: String,
    val country: String,
    val governate: String,
    val postalCode: String,
    val regionCity: String,
    val buildingNumber: String,
    val floor: String,
    val room: String,
    val landmark: String,
    val additionalInformation: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

fun BranchEntity.asBranch() = Branch(
    id = id,
    name = name,
    internalId = internalId,
    companyId = companyId,
    street = street,
    city = city,
    country = country,
    governate = governate,
    postalCode = postalCode,
    regionCity = regionCity,
    buildingNumber = buildingNumber,
    floor = floor,
    room = room,
    landmark = landmark,
    additionalInformation = additionalInformation
)

fun Branch.asBranchEntity() = BranchEntity(
    name = name,
    internalId = internalId,
    companyId = companyId,
    street = street,
    city = city,
    country = country,
    governate = governate,
    postalCode = postalCode,
    regionCity = regionCity,
    buildingNumber = buildingNumber,
    floor = floor,
    room = room,
    landmark = landmark,
    additionalInformation = additionalInformation,
    id = id
)
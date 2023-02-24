package com.example.database.models.branch

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.models.DataEntity
import com.example.database.models.company.CompanyEntity
import com.example.models.branch.Branch
import java.util.UUID


@Entity(
    tableName = "Branch",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("companyId")]
)
data class BranchEntity(
    val name: String,
    val internalId: String,
    val companyId: String,
    val street: String,
    val country: String,
    val governate: String,
    val postalCode: String,
    val regionCity: String,
    val buildingNumber: String,
    val floor: String,
    val room: String,
    val landmark: String,
    val additionalInformation: String,
    override val isCreated: Boolean = false,
    override val isUpdated: Boolean = false,
    override val isDeleted: Boolean = false,
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    override val isSynced: Boolean = false,
    override val syncError: String? = null
) : DataEntity

fun BranchEntity.asBranch() = Branch(
    id = id,
    name = name,
    internalId = internalId,
    companyId = companyId,
    street = street,
    country = country,
    governate = governate,
    postalCode = postalCode,
    regionCity = regionCity,
    buildingNumber = buildingNumber,
    floor = floor,
    room = room,
    landmark = landmark,
    additionalInformation = additionalInformation,
    isSynced = isSynced,
    syncError = syncError
)

fun Branch.asBranchEntity(
    isCreated: Boolean = false,
    isUpdated: Boolean = false,
    isDeleted: Boolean = false
) = BranchEntity(
    name = name,
    internalId = internalId,
    companyId = companyId,
    street = street,
    country = country,
    governate = governate,
    postalCode = postalCode,
    regionCity = regionCity,
    buildingNumber = buildingNumber,
    floor = floor,
    room = room,
    landmark = landmark,
    additionalInformation = additionalInformation,
    isCreated = isCreated,
    isUpdated = isUpdated,
    isDeleted = isDeleted,
    id = id
)
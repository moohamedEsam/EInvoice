package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.item.UnitType
import java.util.UUID

@Entity(tableName = "UnitType")
data class UnitTypeEntity(
    val code: String,
    val name: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
)


fun UnitTypeEntity.asUnitType() = UnitType(
    code = code,
    name = name,
    id = id,
)

fun UnitType.asUnitTypeEntity() = UnitTypeEntity(
    code = code,
    name = name,
    id = id,
)
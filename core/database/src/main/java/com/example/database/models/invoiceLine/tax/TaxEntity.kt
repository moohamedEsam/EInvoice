package com.example.database.models.invoiceLine.tax

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.models.invoiceLine.TaxView
import java.util.UUID

@Entity(tableName = "Tax")
data class TaxEntity(
    val name: String,
    val code: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
)

fun TaxView.asTaxEntity() = TaxEntity(
    name = name,
    code = code
)
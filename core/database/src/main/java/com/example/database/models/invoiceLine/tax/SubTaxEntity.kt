package com.example.database.models.invoiceLine.tax

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.models.invoiceLine.SubTax
import java.util.*

@Entity(
    tableName = "SubTax",
    foreignKeys = [
        ForeignKey(
            entity = TaxEntity::class,
            parentColumns = ["id"],
            childColumns = ["taxId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("taxId")
    ]
)
data class SubTaxEntity(
    val name:String,
    val code:String,
    val taxId:String,
    @PrimaryKey
    val id:String = UUID.randomUUID().toString(),
)


fun SubTaxEntity.asSubTax() = SubTax(
    name = name,
    code = code
)

fun SubTax.asSubTaxEntity(taxId:String) = SubTaxEntity(
    name = name,
    code = code,
    taxId = taxId
)
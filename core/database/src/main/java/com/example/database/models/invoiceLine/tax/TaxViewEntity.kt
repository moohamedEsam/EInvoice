package com.example.database.models.invoiceLine.tax

import androidx.room.Embedded
import androidx.room.Relation
import com.example.models.invoiceLine.TaxView

data class TaxViewEntity(
    @Embedded val tax: TaxEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "taxId"
    )
    val subTaxEntities: List<SubTaxEntity>
)

fun TaxViewEntity.asTaxView(): TaxView {
    return TaxView(
        code = tax.code,
        name = tax.name,
        subTaxes = subTaxEntities.map { it.asSubTax() }
    )
}
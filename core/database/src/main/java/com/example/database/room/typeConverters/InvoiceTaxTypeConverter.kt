package com.example.database.room.typeConverters

import androidx.room.TypeConverter
import com.example.models.invoiceLine.InvoiceTax
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InvoiceTaxTypeConverter {

    @TypeConverter
    fun toInvoiceTax(value: String): List<InvoiceTax> = Json.decodeFromString(value)

    @TypeConverter
    fun fromInvoiceTax(taxes: List<InvoiceTax>): String = Json.encodeToString(taxes)
}
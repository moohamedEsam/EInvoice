package com.example.database.room.typeConverters

import androidx.room.TypeConverter
import com.example.models.company.CompanySettings
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CompanySettingsConverter {

    @TypeConverter
    fun fromCompanySettings(companySettings: CompanySettings): String = Json.encodeToString(companySettings)

    @TypeConverter
    fun toCompanySettings(companySettings: String): CompanySettings = Json.decodeFromString(companySettings)
}
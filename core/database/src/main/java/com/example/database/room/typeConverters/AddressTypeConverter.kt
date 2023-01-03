package com.example.database.room.typeConverters

import androidx.room.TypeConverter
import com.example.models.utils.Address
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AddressTypeConverter {
    @TypeConverter
    fun fromAddress(address: Address) = Json.encodeToString(address)

    @TypeConverter
    fun toAddress(address: String): Address = Json.decodeFromString(address)
}
package com.example.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.models.*
import com.example.database.room.typeConverters.AddressTypeConverter
import com.example.database.room.typeConverters.CompanySettingsConverter
import com.example.database.room.typeConverters.DateTypeConverter
import com.example.database.room.typeConverters.InvoiceTaxTypeConverter

@Database(
    entities = [
        BranchEntity::class, CompanyEntity::class, ClientEntity::class,
        ItemEntity::class, UnitTypeEntity::class, InvoiceLineEntity::class,
        DocumentEntity::class,
    ],
    version = 1
)
@TypeConverters(
    AddressTypeConverter::class,
    CompanySettingsConverter::class,
    InvoiceTaxTypeConverter::class,
    DateTypeConverter::class
)
abstract class EInvoiceDatabase : RoomDatabase() {
    abstract fun getEInvoiceDao(): EInvoiceDao
}

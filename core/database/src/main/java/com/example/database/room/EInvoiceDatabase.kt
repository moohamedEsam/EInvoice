package com.example.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.models.*
import com.example.database.room.typeConverters.AddressTypeConverter

@Database(
    entities = [BranchEntity::class, CompanyEntity::class, ClientEntity::class, ItemEntity::class, UnitTypeEntity::class],
    version = 1
)
@TypeConverters(AddressTypeConverter::class)
abstract class EInvoiceDatabase : RoomDatabase() {
    abstract fun getEInvoiceDao(): EInvoiceDao
}

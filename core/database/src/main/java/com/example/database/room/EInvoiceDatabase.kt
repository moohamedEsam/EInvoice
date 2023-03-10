package com.example.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.test.core.app.ApplicationProvider
import com.example.database.models.*
import com.example.database.models.branch.BranchEntity
import com.example.database.models.client.ClientEntity
import com.example.database.models.company.CompanyEntity
import com.example.database.models.document.DocumentEntity
import com.example.database.models.invoiceLine.InvoiceLineEntity
import com.example.database.models.invoiceLine.tax.SubTaxEntity
import com.example.database.models.invoiceLine.tax.TaxEntity
import com.example.database.room.dao.*
import com.example.database.room.typeConverters.DateTypeConverter
import com.example.database.room.typeConverters.InvoiceTaxTypeConverter

@Database(
    entities = [
        BranchEntity::class, CompanyEntity::class, ClientEntity::class,
        ItemEntity::class, UnitTypeEntity::class, InvoiceLineEntity::class,
        DocumentEntity::class, TaxEntity::class, SubTaxEntity::class
    ],
    version = 1
)
@TypeConverters(
    InvoiceTaxTypeConverter::class,
    DateTypeConverter::class
)
abstract class EInvoiceDatabase : RoomDatabase() {
    abstract fun getCompanyDao(): CompanyDao

    abstract fun getBranchDao(): BranchDao

    abstract fun getClientDao(): ClientDao
    abstract fun getItemDao(): ItemDao

    abstract fun getDocumentDao(): DocumentDao

    companion object {
        fun createInMemoryDatabase(): EInvoiceDatabase {
            return androidx.room.Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                EInvoiceDatabase::class.java
            ).build()
        }
    }
}

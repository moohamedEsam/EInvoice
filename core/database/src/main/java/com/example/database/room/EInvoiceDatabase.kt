package com.example.database.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.database.models.BranchEntity
import com.example.database.models.ClientEntity
import com.example.database.models.CompanyEntity
import com.example.database.models.ItemEntity
import com.example.database.room.typeConverters.AddressTypeConverter

@Database(
    entities = [BranchEntity::class, CompanyEntity::class, ClientEntity::class, ItemEntity::class],
    version = 3,
    autoMigrations = [AutoMigration(
        from = 2,
        to = 3,
        spec = EInvoiceDatabase.AutoMigration_2_3::class
    )]
)
@TypeConverters(AddressTypeConverter::class)
abstract class EInvoiceDatabase : RoomDatabase() {
    abstract fun getEInvoiceDao(): EInvoiceDao

    @DeleteColumn(tableName = "Branch", columnName = "city")
    class AutoMigration_2_3 : AutoMigrationSpec

}

val Migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Branch ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE Branch ADD COLUMN isCreated INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE Branch ADD COLUMN isUpdated INTEGER NOT NULL DEFAULT 0")
    }
}


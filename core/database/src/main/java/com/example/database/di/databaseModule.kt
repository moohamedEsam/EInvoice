package com.example.database.di

import androidx.room.Room
import com.example.database.room.EInvoiceDatabase
import com.example.database.room.Migration_1_2
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

val databaseModule = module {
    single { provideEInvoiceDatabase() }
    single { get<EInvoiceDatabase>().getEInvoiceDao() }
}

private fun Scope.provideEInvoiceDatabase() = Room.databaseBuilder(
    androidContext(),
    EInvoiceDatabase::class.java,
    "einvoice_database"
).addMigrations(Migration_1_2).build()

package com.example.database.di

import androidx.room.Room
import com.example.database.room.EInvoiceDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

val databaseModule = module {
    single { provideEInvoiceDatabase() }
    single { get<EInvoiceDatabase>().getCompanyDao() }
    single { get<EInvoiceDatabase>().getBranchDao() }
    single { get<EInvoiceDatabase>().getClientDao() }
    single { get<EInvoiceDatabase>().getItemDao() }
    single { get<EInvoiceDatabase>().getDocumentDao() }
}

private fun Scope.provideEInvoiceDatabase() = Room.databaseBuilder(
    androidContext(),
    EInvoiceDatabase::class.java,
    "einvoice_database"
).build()

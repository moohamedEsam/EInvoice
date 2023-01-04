package com.example.einvoice

import android.app.Application
import com.example.auth.di.authModule
import com.example.company.di.companyModule
import com.example.data.di.dataModule
import com.example.database.di.databaseModule
import com.example.domain.di.domainModule
import com.example.einvoice.di.appModule
import com.example.network.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
            modules(
                listOf(
                    authModule,
                    appModule,
                    networkModule,
                    dataModule,
                    domainModule,
                    companyModule,
                    databaseModule
                )
            )
        }
    }
}
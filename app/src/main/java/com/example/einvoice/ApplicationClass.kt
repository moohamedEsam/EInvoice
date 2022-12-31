package com.example.einvoice

import android.app.Application
import com.example.auth.presentation.di.authModule
import com.example.einvoice.di.utilsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
            modules(
                listOf(
                    authModule, utilsModule
                )
            )
        }
    }
}
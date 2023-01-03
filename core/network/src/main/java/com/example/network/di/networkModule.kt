package com.example.network.di

import android.util.Log
import com.example.network.EInvoiceRemoteDataSource
import com.example.network.KtorEInvoiceRemoteDataSource
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient() }
    single<EInvoiceRemoteDataSource> { KtorEInvoiceRemoteDataSource(get()) }
}

fun provideHttpClient() = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.i("ktor", "log: $message")
            }
        }
    }

    install(Auth)
}
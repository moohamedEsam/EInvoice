package com.example.einvoice.di

import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

val utilsModule = module {
    single { provideImageLoader() }
    single { provideHttpClient() }
}

fun provideHttpClient() = HttpClient(CIO) {
    install(ContentNegotiation){
        json()
    }

    install(Logging){
        logger = object : Logger{
            override fun log(message: String) {
                Log.i("ktor", "log: $message")
            }
        }
    }

    install(Auth)
}

fun Scope.provideImageLoader() = ImageLoader.Builder(androidContext())
    .crossfade(true)
    .components {
        if (Build.VERSION.SDK_INT >= 28)
            add(ImageDecoderDecoder.Factory())
        else
            add(GifDecoder.Factory())
    }
    .build()

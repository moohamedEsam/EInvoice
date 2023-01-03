package com.example.einvoice.di

import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

val utilsModule = module {
    single { provideImageLoader() }
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
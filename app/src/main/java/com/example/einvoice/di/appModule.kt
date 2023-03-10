package com.example.einvoice.di

import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.MainActivityViewModel
import com.example.einvoice.presentation.shared.LayoutViewModel
import com.example.functions.BaseSnackBarManager
import com.example.functions.SnackBarManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module

val appModule = module {
    single { provideImageLoader() }
    viewModel { MainActivityViewModel(get()) }
    viewModel { LayoutViewModel(get(), get(), get()) }
    single<SnackBarManager> { BaseSnackBarManager() }
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

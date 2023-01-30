package com.example.document.di

import com.example.document.screens.all.DocumentsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val documentsModule = module {
    viewModel { DocumentsViewModel(get()) }
}
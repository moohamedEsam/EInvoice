package com.example.client.di

import com.example.client.screens.all.ClientsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clientModule = module {
    viewModel { ClientsViewModel(get()) }
}
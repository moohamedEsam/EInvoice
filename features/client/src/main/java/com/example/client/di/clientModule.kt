package com.example.client.di

import com.example.client.screens.all.ClientsViewModel
import com.example.client.screens.form.ClientFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clientModule = module {
    viewModel { ClientsViewModel(get()) }
    viewModel { params -> ClientFormViewModel(get(), get(), get(), get(), params.get()) }
}
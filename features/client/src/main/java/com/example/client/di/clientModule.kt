package com.example.client.di

import com.example.client.screens.all.ClientsViewModel
import com.example.client.screens.dashboard.ClientDashboardViewModel
import com.example.client.screens.form.ClientFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clientModule = module {
    viewModel { ClientsViewModel(get()) }
    viewModel { params ->
        ClientFormViewModel(
            createClientUseCase = get(),
            updateClientUseCase = get(),
            getCompaniesUseCase = get(),
            clientId = params.get(),
            getClientViewUseCase = get(),
            snackBarManager = get()
        )
    }
    viewModel { params ->
        ClientDashboardViewModel(
            getClientUseCase = get(),
            deleteClientUseCase = get(),
            getDocumentsByClientUseCase = get(),
            undoDeleteClientUseCase = get(),
            clientId = params.get(),
            snackBarManager = get()
        )
    }
}
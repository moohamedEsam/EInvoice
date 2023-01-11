package com.example.item.di

import com.example.item.screens.all.ItemsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val itemModule = module {
    viewModel {
        ItemsViewModel(
            getItemsUseCase = get(),
            getBranchesUseCase = get(),
            getUnitTypesUseCase = get(),
            createItemUseCase = get(),
            updateItemUseCase = get(),
            snackBarManager = get()
        )
    }
}
package com.example.company.di

import com.example.company.screen.all.CompaniesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val companyModule = module {
    viewModel { CompaniesViewModel(get(), get(), get(), get(), get()) }
}
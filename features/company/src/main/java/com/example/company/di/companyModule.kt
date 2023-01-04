package com.example.company.di

import com.example.company.screen.CompaniesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val companyModule = module {
    viewModel { CompaniesViewModel(get()) }
}
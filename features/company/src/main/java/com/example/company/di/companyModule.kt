package com.example.company.di

import com.example.company.screen.all.CompaniesViewModel
import com.example.company.screen.form.CompanyFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val companyModule = module {
    viewModel { CompaniesViewModel(get(), get(), get()) }
    viewModel { (companyId: String) -> CompanyFormViewModel(get(), get(), get(), companyId) }
}
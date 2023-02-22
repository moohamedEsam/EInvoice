package com.example.company.di

import com.example.company.screen.all.CompaniesViewModel
import com.example.company.screen.dashboard.CompanyDashboardViewModel
import com.example.company.screen.form.CompanyFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val companyModule = module {
    viewModel { CompaniesViewModel(companyPagingSource = get()) }

    viewModel { (companyId: String) ->
        CompanyFormViewModel(
            getCompanyUseCase = get(),
            createCompanyUseCase = get(),
            updateCompanyUseCase = get(),
            companyId = companyId,
            snackBarManager = get()
        )
    }
    viewModel { (companyId: String) ->
        CompanyDashboardViewModel(
            getCompanyUseCase = get(),
            getDocumentsUseCase = get(),
            deleteCompanyUseCase = get(),
            undoDeleteCompanyUseCase = get(),
            companyId = companyId,
            snackBarManager = get()
        )
    }

}
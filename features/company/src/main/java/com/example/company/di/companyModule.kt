package com.example.company.di

import com.example.company.screen.all.CompaniesViewModel
import com.example.company.screen.dashboard.CompanyDashboardViewModel
import com.example.company.screen.form.CompanyFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val companyModule = module {
    viewModel {
        CompaniesViewModel(
            getCompaniesUseCase = get(),
            deleteCompanyUseCase = get(),
            undoDeleteCompanyUseCase = get()
        )
    }
    viewModel { (companyId: String) ->
        CompanyFormViewModel(
            getCompanyUseCase = get(),
            createCompanyUseCase = get(),
            updateCompanyUseCase = get(),
            companyId = companyId
        )
    }
    viewModel { (companyId: String) ->
        CompanyDashboardViewModel(
            getCompanyUseCase = get(),
            getDocumentsUseCase = get(),
            companyId = companyId
        )
    }

}
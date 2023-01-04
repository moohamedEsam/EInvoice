package com.example.domain.di

import com.example.data.company.CompanyRepository
import com.example.domain.auth.isUserLoggedInUseCase
import com.example.domain.auth.loginUseCase
import com.example.domain.auth.logoutUseCase
import com.example.domain.auth.registerUseCase
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.DeleteCompanyUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UpdateCompanyUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {
    single { loginUseCase(get(), androidContext()) }
    single { logoutUseCase(get(), androidContext()) }
    single { registerUseCase(get(), androidContext()) }
    single { isUserLoggedInUseCase(androidContext()) }
    single { GetCompaniesUseCase(get<CompanyRepository>()::getCompanies) }
    single { GetCompanyUseCase(get<CompanyRepository>()::getCompany) }
    single { CreateCompanyUseCase(get<CompanyRepository>()::createCompany) }
    single { UpdateCompanyUseCase(get<CompanyRepository>()::updateCompany) }
    single { DeleteCompanyUseCase(get<CompanyRepository>()::deleteCompany) }
}
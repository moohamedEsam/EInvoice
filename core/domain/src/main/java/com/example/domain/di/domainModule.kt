package com.example.domain.di

import com.example.data.AuthRepository
import com.example.data.company.CompanyRepository
import com.example.domain.auth.LoginUseCase
import com.example.domain.auth.LogoutUseCase
import com.example.domain.auth.RegisterUseCase
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.DeleteCompanyUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UpdateCompanyUseCase
import org.koin.dsl.module

val domainModule = module {
    single { LoginUseCase(get<AuthRepository>()::login) }
    single { LogoutUseCase(get<AuthRepository>()::logout) }
    single { RegisterUseCase(get<AuthRepository>()::register) }
    single { GetCompaniesUseCase(get<CompanyRepository>()::getCompanies) }
    single { GetCompanyUseCase(get<CompanyRepository>()::getCompany) }
    single { CreateCompanyUseCase(get<CompanyRepository>()::createCompany) }
    single { UpdateCompanyUseCase(get<CompanyRepository>()::updateCompany) }
    single { DeleteCompanyUseCase(get<CompanyRepository>()::deleteCompany) }
}
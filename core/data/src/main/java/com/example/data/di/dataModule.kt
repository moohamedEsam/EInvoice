package com.example.data.di

import com.example.data.AuthRepository
import com.example.data.KtorAuthRepository
import com.example.data.company.CompanyRepository
import com.example.data.company.OfflineFirstCompanyRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { KtorAuthRepository(get()) }
    single<CompanyRepository> { OfflineFirstCompanyRepository(get(), get()) }
}
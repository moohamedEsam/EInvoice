package com.example.data.di

import com.example.data.AuthRepository
import com.example.data.KtorAuthRepository
import com.example.data.branch.BranchRepository
import com.example.data.branch.OfflineFirstBranchRepository
import com.example.data.client.ClientRepository
import com.example.data.client.OfflineFirstClientRepository
import com.example.data.company.CompanyRepository
import com.example.data.company.OfflineFirstCompanyRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { KtorAuthRepository(get()) }
    single<CompanyRepository> { OfflineFirstCompanyRepository(get(), get()) }
    single<BranchRepository> { OfflineFirstBranchRepository(get(), get()) }
    single<ClientRepository> { OfflineFirstClientRepository(get(), get()) }
}
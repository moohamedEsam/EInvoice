package com.example.domain.di

import com.example.data.branch.BranchRepository
import com.example.data.client.ClientRepository
import com.example.data.company.CompanyRepository
import com.example.domain.auth.isUserLoggedInUseCase
import com.example.domain.auth.loginUseCase
import com.example.domain.auth.logoutUseCase
import com.example.domain.auth.registerUseCase
import com.example.domain.branch.CreateBranchUseCase
import com.example.domain.branch.DeleteBranchUseCase
import com.example.domain.branch.GetBranchUseCase
import com.example.domain.branch.GetBranchesUseCase
import com.example.domain.branch.UpdateBranchUseCase
import com.example.domain.client.CreateClientUseCase
import com.example.domain.client.DeleteClientUseCase
import com.example.domain.client.GetClientUseCase
import com.example.domain.client.GetClientsUseCase
import com.example.domain.client.UpdateClientUseCase

import com.example.domain.company.*
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
    single { UndoDeleteCompanyUseCase(get<CompanyRepository>()::undoDeleteCompany) }

    //branch
    single { GetBranchesUseCase(get<BranchRepository>()::getBranches) }
    single { GetBranchUseCase(get<BranchRepository>()::getBranch) }
    single { CreateBranchUseCase(get<BranchRepository>()::createBranch) }
    single { UpdateBranchUseCase(get<BranchRepository>()::updateBranch) }
    single { DeleteBranchUseCase(get<BranchRepository>()::deleteBranch) }

    //client
    single { GetClientsUseCase(get<ClientRepository>()::getClients) }
    single { GetClientUseCase(get<ClientRepository>()::getClient) }
    single { CreateClientUseCase(get<ClientRepository>()::createClient) }
    single { UpdateClientUseCase(get<ClientRepository>()::updateClient) }
    single { DeleteClientUseCase(get<ClientRepository>()::deleteClient) }

}
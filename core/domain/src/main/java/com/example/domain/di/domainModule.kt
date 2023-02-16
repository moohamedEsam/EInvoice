package com.example.domain.di

import com.example.data.branch.BranchRepository
import com.example.data.client.ClientRepository
import com.example.data.company.CompanyRepository
import com.example.data.document.DocumentRepository
import com.example.data.item.ItemRepository
import com.example.domain.auth.isUserLoggedInUseCase
import com.example.domain.auth.loginUseCase
import com.example.domain.auth.logoutUseCase
import com.example.domain.auth.registerUseCase
import com.example.domain.branch.*
import com.example.domain.client.*

import com.example.domain.company.*
import com.example.domain.document.*
import com.example.domain.item.*
import com.example.domain.networkStatus.getAndroidNetworkObserver
import com.example.domain.sync.oneTimeSyncUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {
    single { oneTimeSyncUseCase(androidContext()) }
    single { getAndroidNetworkObserver(androidContext()) }

    single { loginUseCase(get(), androidContext(), get()) }
    single { logoutUseCase(get(), androidContext()) }
    single { registerUseCase(get(), androidContext()) }
    single { isUserLoggedInUseCase(androidContext()) }
    single { GetCompaniesUseCase(get<CompanyRepository>()::getCompanies) }
    single { GetCompaniesViewsUseCase(get<CompanyRepository>()::getCompaniesViews) }
    single { GetCompanyUseCase(get<CompanyRepository>()::getCompany) }
    single { CreateCompanyUseCase(get<CompanyRepository>()::createCompany) }
    single { UpdateCompanyUseCase(get<CompanyRepository>()::updateCompany) }
    single { DeleteCompanyUseCase(get<CompanyRepository>()::deleteCompany) }
    single { UndoDeleteCompanyUseCase(get<CompanyRepository>()::undoDeleteCompany) }

    //branch
    single { GetBranchesUseCase(get<BranchRepository>()::getBranches) }
    single { GetBranchesByCompanyUseCase(get<BranchRepository>()::getBranchesByCompanyId) }
    single { GetBranchUseCase(get<BranchRepository>()::getBranch) }
    single { GetBranchViewUseCase(get<BranchRepository>()::getBranchView) }
    single { CreateBranchUseCase(get<BranchRepository>()::createBranch) }
    single { UpdateBranchUseCase(get<BranchRepository>()::updateBranch) }
    single { DeleteBranchUseCase(get<BranchRepository>()::deleteBranch) }
    single { UndoDeleteBranchUseCase(get<BranchRepository>()::undoDeleteBranch) }

    //client
    single { GetClientsUseCase(get<ClientRepository>()::getClients) }
    single { GetClientUseCase(get<ClientRepository>()::getClient) }
    single { GetClientViewUseCase(get<ClientRepository>()::getClientView) }
    single { CreateClientUseCase(get<ClientRepository>()::createClient) }
    single { UpdateClientUseCase(get<ClientRepository>()::updateClient) }
    single { DeleteClientUseCase(get<ClientRepository>()::deleteClient) }
    single { UndoDeleteClientUseCase(get<ClientRepository>()::undoDeleteClient) }

    //item
    single { GetItemsUseCase(get<ItemRepository>()::getItems) }
    single { GetItemsByBranchUseCase(get<ItemRepository>()::getItemsByBranchId) }
    single { GetItemUseCase(get<ItemRepository>()::getItem) }
    single { CreateItemUseCase(get<ItemRepository>()::createItem) }
    single { UpdateItemUseCase(get<ItemRepository>()::updateItem) }
    single { DeleteItemUseCase(get<ItemRepository>()::deleteItem) }
    single { GetUnitTypesUseCase(get<ItemRepository>()::getUnitTypes) }
    single { GetTaxTypesUseCase(get<ItemRepository>()::getTaxTypes) }

    //document
    single { GetDocumentsUseCase(get<DocumentRepository>()::getDocuments) }
    single { getDocumentsInternalIdsByCompanyIdUseCase(get()) }
    single { getDocumentsByTypeUseCase(get()) }
    single { GetDocumentUseCase(get<DocumentRepository>()::getDocument) }
    single { CreateDocumentUseCase(get<DocumentRepository>()::createDocument) }
    single { UpdateDocumentUseCase(get<DocumentRepository>()::updateDocument) }
    single { DeleteDocumentUseCase(get<DocumentRepository>()::deleteDocument) }
    single { UndoDeleteDocumentUseCase(get<DocumentRepository>()::undoDeleteDocument) }
    single { SyncDocumentsStatusUseCase(get<DocumentRepository>()::syncDocumentsStatus) }
    single { CancelDocumentUseCase(get<DocumentRepository>()::cancelDocument) }
    single { SendDocumentUseCase(get<DocumentRepository>()::sendDocument) }
    single { CreateDerivedDocumentUseCase(get<DocumentRepository>()::createDerivedDocument) }

}
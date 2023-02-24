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
    factory { oneTimeSyncUseCase(androidContext()) }
    factory { getAndroidNetworkObserver(androidContext()) }

    factory { loginUseCase(get(), androidContext(), get()) }
    factory { logoutUseCase(get(), androidContext()) }
    factory { registerUseCase(get(), androidContext()) }
    factory { isUserLoggedInUseCase(androidContext()) }


    factory { GetCompaniesUseCase(get<CompanyRepository>()::getCompanies) }
    factory { GetCompanyPagingSource(get<CompanyRepository>()::getCompanyPagingSource) }
    factory { GetCompaniesViewsUseCase(get<CompanyRepository>()::getCompaniesViews) }
    factory { GetCompanyUseCase(get<CompanyRepository>()::getCompany) }
    factory { CreateCompanyUseCase(get<CompanyRepository>()::createCompany) }
    factory { UpdateCompanyUseCase(get<CompanyRepository>()::updateCompany) }
    factory { DeleteCompanyUseCase(get<CompanyRepository>()::deleteCompany) }
    factory { UndoDeleteCompanyUseCase(get<CompanyRepository>()::undoDeleteCompany) }

    //branch
    factory { GetBranchesUseCase(get<BranchRepository>()::getBranches) }
    factory { GetBranchesPagingSourceUseCase(get<BranchRepository>()::getBranchesPagingSource) }
    factory { GetBranchesByCompanyUseCase(get<BranchRepository>()::getBranchesByCompanyId) }
    factory { GetBranchViewUseCase(get<BranchRepository>()::getBranchView) }
    factory { CreateBranchUseCase(get<BranchRepository>()::createBranch) }
    factory { UpdateBranchUseCase(get<BranchRepository>()::updateBranch) }
    factory { DeleteBranchUseCase(get<BranchRepository>()::deleteBranch) }
    factory { UndoDeleteBranchUseCase(get<BranchRepository>()::undoDeleteBranch) }

    //client
    factory { GetClientsUseCase(get<ClientRepository>()::getClients) }
    factory { GetClientsPagingSourceUseCase(get<ClientRepository>()::getClientsPagingSource) }
    factory { GetClientUseCase(get<ClientRepository>()::getClient) }
    factory { GetClientViewUseCase(get<ClientRepository>()::getClientView) }
    factory { CreateClientUseCase(get<ClientRepository>()::createClient) }
    factory { UpdateClientUseCase(get<ClientRepository>()::updateClient) }
    factory { DeleteClientUseCase(get<ClientRepository>()::deleteClient) }
    factory { UndoDeleteClientUseCase(get<ClientRepository>()::undoDeleteClient) }

    //item
    factory { GetItemsUseCase(get<ItemRepository>()::getItems) }
    factory { GetItemsPagingSourceUseCase(get<ItemRepository>()::getItemPagingSource) }
    factory { GetItemsByBranchUseCase(get<ItemRepository>()::getItemsByBranchId) }
    factory { CreateItemUseCase(get<ItemRepository>()::createItem) }
    factory { UpdateItemUseCase(get<ItemRepository>()::updateItem) }
    factory { DeleteItemUseCase(get<ItemRepository>()::deleteItem) }
    factory { GetUnitTypesUseCase(get<ItemRepository>()::getUnitTypes) }
    factory { GetTaxTypesUseCase(get<ItemRepository>()::getTaxTypes) }

    //document
    factory { GetDocumentsUseCase(get<DocumentRepository>()::getDocuments) }
    factory { GetDocumentsPagingSourceUseCase(get<DocumentRepository>()::getDocumentsPagingSource) }
    factory { getDocumentsInternalIdsByCompanyIdUseCase(get()) }
    factory { getDocumentsByTypeUseCase(get()) }
    factory { GetDocumentUseCase(get<DocumentRepository>()::getDocument) }
    factory { CreateDocumentUseCase(get<DocumentRepository>()::createDocument) }
    factory { UpdateDocumentUseCase(get<DocumentRepository>()::updateDocument) }
    factory { DeleteDocumentUseCase(get<DocumentRepository>()::deleteDocument) }
    factory { UndoDeleteDocumentUseCase(get<DocumentRepository>()::undoDeleteDocument) }
    factory { SyncDocumentsStatusUseCase(get<DocumentRepository>()::syncDocumentsStatus) }
    factory { CancelDocumentUseCase(get<DocumentRepository>()::cancelDocument) }
    factory { SendDocumentUseCase(get<DocumentRepository>()::sendDocument) }
    factory { CreateDerivedDocumentUseCase(get<DocumentRepository>()::createDerivedDocument) }

}
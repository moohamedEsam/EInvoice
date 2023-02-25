package com.example.data.company

import androidx.paging.PagingSource
import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.company.*
import com.example.database.room.dao.CompanyDao
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map

private const val COMPANY_NOT_FOUND = "Company Not Found"

class OfflineFirstCompanyRepository(
    private val localDataSource: CompanyDao,
    private val remoteDataSource: EInvoiceRemoteDataSource
) : CompanyRepository {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        localDataSource.insertCompany(company.asCompanyEntity(isCreated = true))
        Result.Success(company)
    }

    override fun getCompany(id: String): Flow<CompanyView> = localDataSource.getCompanyViewById(id)
        .filterNotNull()
        .map(CompanyViewEntity::removeDeleted)
        .map(CompanyViewEntity::asCompanyView)

    override fun getCompaniesViews(): Flow<List<CompanyView>> = localDataSource.getCompaniesViews()
        .map { companies ->
            companies.map(CompanyViewEntity::removeDeleted).map(CompanyViewEntity::asCompanyView)
        }

    override fun getCompanies(): Flow<List<Company>> = localDataSource.getCompanies()
        .map { companies -> companies.map(CompanyEntity::asCompany) }

    override fun getCompanyPagingSource(): PagingSource<Int, CompanyView> =
        localDataSource.getPagedCompanies().map(CompanyViewEntity::asCompanyView)
            .asPagingSourceFactory().invoke()


    override suspend fun updateCompany(company: Company): Result<Company> = tryWrapper {
        val companyEntity = localDataSource.getCompanyById(company.id)
            ?: return@tryWrapper Result.Error(COMPANY_NOT_FOUND)
        if (companyEntity.isCreated)
            localDataSource.updateCompany(company.asCompanyEntity(isCreated = true))
        else
            localDataSource.updateCompany(company.asCompanyEntity(isUpdated = true))
        Result.Success(company)
    }

    override suspend fun deleteCompany(id: String): Result<Unit> = tryWrapper {
        val company = localDataSource.getCompanyById(id)
            ?: return@tryWrapper Result.Error(COMPANY_NOT_FOUND)
        if (company.isCreated)
            localDataSource.deleteCompany(company.id)
        else
            localDataSource.markCompanyAsDeleted(company.id)
        Result.Success(Unit)
    }

    override suspend fun undoDeleteCompany(id: String): Result<Unit> = tryWrapper {
        localDataSource.undoDeleteCompany(id)
        Result.Success(Unit)
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val companies = localDataSource.getAllCompanies()
        var idMappings: Map<String, String?> = emptyMap()
        val remotelySavedCompanies = mutableListOf<String>()
        val isSuccessfulSync = synchronizer.handleSync(
            remoteFetcher = remoteDataSource::getCompanies,
            remoteDeleter = { handleRemoteDelete(companies) },
            remoteCreator = { idMappings = handleRemoteCreate(companies) },
            remoteUpdater = {
                val updatedCompanies = companies.filter { it.isUpdated }
                updatedCompanies.forEach { company ->
                    updateCompanyRemotelyAndLocal(company)
                }
            },
            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    if (newId == null) return@forEach
                    updateRelationsKeys(oldId, newId)
                    localDataSource.deleteCompany(oldId)
                }
            },
            localCreator = { company ->
                remotelySavedCompanies.add(company.id)
                if (company.id !in companies.map { it.id })
                    localDataSource.insertCompany(company.asCompanyEntity().copy(isSynced = true))
                else
                    localDataSource.updateCompany(company.asCompanyEntity().copy(isSynced = true))
            }
        )
        return isSuccessfulSync
    }

    private suspend fun handleRemoteCreate(
        companies: List<CompanyEntity>
    ) =
        companies.filter { it.isCreated }
            .associateBy { companyEntity ->
                val result = remoteDataSource.createCompany(companyEntity.asCompany())
                handleCreateResult(result, companyEntity)
            }.map { (newId, company) ->
                company.id to newId
            }.toMap()

    private suspend fun handleCreateResult(
        result: Result<Company>,
        companyEntity: CompanyEntity
    ) =
        if (result is Result.Success)
            result.data.id
        else
            null.also { handleCreateError(result, companyEntity) }


    private suspend fun handleCreateError(
        result: Result<Company>,
        companyEntity: CompanyEntity
    ) {
        val error = (result as? Result.Error)?.exception
        localDataSource.updateCompany(
            companyEntity.copy(
                isSynced = false,
                syncError = error
            )
        )
    }


    private suspend fun handleRemoteDelete(
        companies: List<CompanyEntity>
    ) {
        companies.filter { it.isDeleted }
            .forEach { company ->
                deleteCompanyRemotelyAndLocal(company)
            }
    }

    private suspend fun updateRelationsKeys(oldId: String, newId: String) {
        localDataSource.updateBranchesCompanyId(oldId, newId)
        localDataSource.updateClientsCompanyId(oldId, newId)
        localDataSource.updateDocumentsIssuerId(oldId, newId)
    }

    private suspend fun updateCompanyRemotelyAndLocal(company: CompanyEntity) {
        val result = remoteDataSource.updateCompany(company.asCompany())
        if (result is Result.Success)
            localDataSource.updateCompany(company.copy(isUpdated = false))
    }

    private suspend fun deleteCompanyRemotelyAndLocal(company: CompanyEntity) {
        val result = remoteDataSource.deleteCompany(company.id)
        result.ifSuccess { localDataSource.deleteCompany(it.id) }
    }


}
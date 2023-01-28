package com.example.data.company

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.asCompany
import com.example.database.models.company.asCompanyEntity
import com.example.database.room.EInvoiceDao
import com.example.models.company.Company
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OfflineFirstCompanyRepository(
    private val localDataSource: EInvoiceDao,
    private val remoteDataSource: EInvoiceRemoteDataSource
) : CompanyRepository {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        localDataSource.insertCompany(company.asCompanyEntity(isCreated = true))
        Result.Success(company)
    }

    override fun getCompany(id: String): Flow<Company> = localDataSource.getCompanyById(id)
        .map(CompanyEntity::asCompany)

    override fun getCompanies(): Flow<List<Company>> = localDataSource.getCompanies()
        .map { companies -> companies.map(CompanyEntity::asCompany) }

    override suspend fun updateCompany(company: Company): Result<Company> = tryWrapper {
        val companyEntity = localDataSource.getCompanyById(company.id).first()
        if (companyEntity.isCreated)
            localDataSource.updateCompany(company.asCompanyEntity(isCreated = true))
        else
            localDataSource.updateCompany(company.asCompanyEntity(isUpdated = true))
        Result.Success(company)
    }

    override suspend fun deleteCompany(id: String): Result<Unit> = tryWrapper {
        val company = localDataSource.getCompanyById(id).first()
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
        val companies = localDataSource.getCompanies().first()
        val idMappings = HashMap<String, String>()
        val isSuccessfulSync = synchronizer.handleSync(
            remoteFetcher = remoteDataSource::getCompanies,
            remoteDeleter = {
                val deletedCompanies = companies.filter { it.isDeleted }
                deletedCompanies.forEach { company ->
                    val result = remoteDataSource.deleteCompany(company.id)
                    if (result is Result.Success)
                        localDataSource.deleteCompany(company.id)
                }
                Result.Success(Unit)
            },
            remoteCreator = {
                val createdCompanies = companies.filter { it.isCreated }
                createdCompanies.forEach { company ->
                    val result = remoteDataSource.createCompany(company.asCompany())
                    if (result is Result.Success) {
                        idMappings[company.id] = result.data.id
                    }
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedCompanies = companies.filter { it.isUpdated }
                updatedCompanies.forEach { company ->
                    val result = remoteDataSource.updateCompany(company.asCompany())
                    if (result is Result.Success)
                        localDataSource.updateCompany(company.copy(isUpdated = false))

                }
                Result.Success(Unit)
            },
            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    localDataSource.updateBranchesCompanyId(oldId, newId)
                    localDataSource.updateClientsCompanyId(oldId, newId)
                    localDataSource.updateDocumentsIssuerId(oldId, newId)
                    localDataSource.deleteCompany(oldId)
                }
            },
            localCreator = { company ->
                if (company.id !in companies.map { it.id })
                    localDataSource.insertCompany(company.asCompanyEntity())
                else
                    localDataSource.updateCompany(company.asCompanyEntity())
            }
        )
        return isSuccessfulSync
    }


}
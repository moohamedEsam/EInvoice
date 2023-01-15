package com.example.data.company

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.CompanyEntity
import com.example.database.models.asCompany
import com.example.database.models.asCompanyEntity
import com.example.database.room.EInvoiceDao
import com.example.models.Company
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.handleSync(
            remoteFetcher = remoteDataSource::getCompanies,
            remoteDeleter = {
                val companies = localDataSource.getDeletedCompanies()
                companies.forEach { company ->
                    remoteDataSource.deleteCompany(company.id)
                }
                Result.Success(Unit)
            },
            remoteCreator = {
                val companies = localDataSource.getCreatedCompanies()
                companies.forEach { company ->
                    remoteDataSource.createCompany(company.asCompany())
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val companies = localDataSource.getUpdatedCompanies()
                companies.forEach { company ->
                    remoteDataSource.updateCompany(company.asCompany())
                }
                Result.Success(Unit)
            },
            localCreator = { company ->
                localDataSource.insertCompany(company.asCompanyEntity())
            },
            beforeLocalCreate = {
                localDataSource.deleteAllCompanies()
            }
        )

}
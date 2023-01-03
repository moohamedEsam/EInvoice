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
import kotlinx.coroutines.flow.map

class OfflineFirstCompanyRepository(
    private val localDataSource: EInvoiceDao,
    private val remoteDataSource: EInvoiceRemoteDataSource
) : CompanyRepository {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        localDataSource.insertCompany(company.asCompanyEntity())
        Result.Success(company)
    }

    override fun getCompany(id: String): Flow<Company> = localDataSource.getCompanyById(id.toInt())
        .map(CompanyEntity::asCompany)

    override fun getCompanies(): Flow<List<Company>> = localDataSource.getCompanies()
        .map { companies -> companies.map(CompanyEntity::asCompany) }

    override suspend fun updateCompany(company: Company): Result<Company> = tryWrapper {
        localDataSource.updateCompany(company.asCompanyEntity())
        Result.Success(company)
    }

    override suspend fun deleteCompany(id: String): Result<Unit> = tryWrapper {
        localDataSource.deleteCompany(id.toInt())
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
            localCreator = { company ->
                localDataSource.insertCompany(company.asCompanyEntity())
            },
            localDeleter = { company ->
                localDataSource.deleteCompany(company.id.toInt())
            }
        )

}
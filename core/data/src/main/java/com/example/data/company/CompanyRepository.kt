package com.example.data.company

import com.example.models.company.Company
import com.example.common.models.Result
import com.example.data.sync.Syncable
import kotlinx.coroutines.flow.Flow

interface CompanyRepository : Syncable<Company> {
    suspend fun createCompany(company: Company): Result<Company>
    fun getCompany(id: String): Flow<Company>
    suspend fun updateCompany(company: Company): Result<Company>
    suspend fun deleteCompany(id: String): Result<Unit>

    suspend fun undoDeleteCompany(id: String): Result<Unit>
    fun getCompanies(): Flow<List<Company>>

}
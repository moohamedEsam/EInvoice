package com.example.data

import com.example.models.Company

interface EInvoiceCompanyRepository {
    suspend fun createCompany(company: Company) : Result<Company>
    suspend fun getCompany(id: String) : Result<Company>
    suspend fun updateCompany(company: Company) : Result<Company>
    suspend fun deleteCompany(id: String) : Result<Unit>
}
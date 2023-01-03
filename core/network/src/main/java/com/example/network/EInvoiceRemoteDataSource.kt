package com.example.network

import com.example.common.models.Result
import com.example.models.Branch
import com.example.models.Client
import com.example.models.Company

interface EInvoiceRemoteDataSource {
    // company related
    suspend fun createCompany(company: Company): Result<Company>
//    suspend fun getCompany(companyId: String): Result<Company>
//    suspend fun getCompanies(): Result<List<Company>>
//    suspend fun updateCompany(company: Company): Result<Company>
//    suspend fun deleteCompany(companyId: String): Result<Company>
//
//    // client related
//    suspend fun createClient(client: Client): Result<Client>
//    suspend fun getClient(clientId: String): Result<Client>
//    suspend fun getClients(): Result<List<Client>>
//    suspend fun updateClient(client: Client): Result<Client>
//    suspend fun deleteClient(clientId: String): Result<Client>
//
//    // branch related
//    suspend fun createBranch(branch: Branch): Result<Branch>
//    suspend fun getBranch(branchId: String): Result<Branch>
//    suspend fun getBranches(): Result<List<Branch>>
//    suspend fun updateBranch(branch: Branch): Result<Branch>
//    suspend fun deleteBranch(branchId: String): Result<Branch>

}
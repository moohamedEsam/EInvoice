package com.example.network

import com.example.common.models.Result
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.item.Item
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.models.branch.Branch
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.TaxView
import com.example.models.item.UnitType
import com.example.network.models.document.*

interface EInvoiceRemoteDataSource {

    //auth
    suspend fun login(credentials: Credentials): Result<Token>
    suspend fun register(register: Register): Result<Token>
    suspend fun logout(): Result<Unit>

    // company related
    suspend fun createCompany(company: Company): Result<Company>
    suspend fun getCompany(companyId: String): Result<Company>
    suspend fun getCompanies(): Result<List<Company>>
    suspend fun updateCompany(company: Company): Result<Company>
    suspend fun deleteCompany(companyId: String): Result<Company>

    // client related
    suspend fun createClient(client: Client): Result<Client>
    suspend fun getClient(clientId: String): Result<Client>
    suspend fun getClients(): Result<List<Client>>
    suspend fun updateClient(client: Client): Result<Client>
    suspend fun deleteClient(clientId: String): Result<Client>

    // branch related
    suspend fun createBranch(branch: Branch): Result<Branch>
    suspend fun getBranch(branchId: String): Result<Branch>
    suspend fun getBranches(): Result<List<Branch>>
    suspend fun updateBranch(branch: Branch): Result<Branch>
    suspend fun deleteBranch(branchId: String): Result<Branch>

    // item related
    suspend fun createItem(item: Item): Result<Item>
    suspend fun getItem(itemId: String): Result<Item>

    suspend fun getItems(): Result<List<Item>>

    suspend fun updateItem(item: Item): Result<Item>

    suspend fun deleteItem(itemId: String): Result<Item>

    suspend fun getUnitTypes(): Result<List<UnitType>>

    suspend fun getTaxTypes(): Result<List<TaxView>>

    // document related

    suspend fun createDocument(document: CreateDocumentDto): Result<NetworkDocument>

    suspend fun getDocument(documentId: String): Result<DocumentView>

    suspend fun getDocuments(): Result<List<NetworkDocument>>

    suspend fun updateDocument(document: UpdateDocumentDto): Result<NetworkDocument>

    suspend fun deleteDocument(documentId: String): Result<Unit>

    suspend fun syncDocumentsStatus(): Result<Unit>

    suspend fun cancelDocument(documentId: String): Result<Unit>

}
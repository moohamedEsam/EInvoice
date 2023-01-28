package com.example.network

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.network.models.document.DocumentViewDto
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.network.models.*
import com.example.network.models.document.CreateDocumentDto
import com.example.network.models.document.DocumentDto
import com.example.network.models.document.UpdateDocumentDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorEInvoiceRemoteDataSource(private val client: HttpClient) : EInvoiceRemoteDataSource {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        val response = client.post(Urls.COMPANY) {
            setBody(company)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

    override suspend fun login(credentials: Credentials): Result<Token> = tryWrapper {
        val response = client.post(Urls.LOGIN) {
            setBody(credentials)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Token>>()
        apiResponse.asResult()
    }

    override suspend fun register(register: Register): Result<Token> = tryWrapper {
        val response = client.post(Urls.REGISTER) {
            setBody(register)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Token>>()
        apiResponse.asResult()
    }

    override suspend fun logout(): Result<Unit> = tryWrapper {
        val authProvider = client.plugin(Auth).providers.firstOrNull() as? BearerAuthProvider
            ?: return@tryWrapper Result.Error("No auth provider found")
        authProvider.clearToken()
        Result.Success(Unit)
    }

    override suspend fun getCompany(companyId: String): Result<Company> = tryWrapper {
        val response = client.get(Urls.getCompany(companyId))
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

    override suspend fun getCompanies(): Result<List<Company>> = tryWrapper {
        val response = client.get(Urls.COMPANY)
        val apiResponse = response.body<ApiResponse<List<Company>>>()
        apiResponse.asResult()
    }

    override suspend fun updateCompany(company: Company): Result<Company> = tryWrapper {
        val response = client.put(Urls.getCompany(company.id)) {
            setBody(company)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

    override suspend fun deleteCompany(companyId: String): Result<Company> = tryWrapper {
        val response = client.delete(Urls.getCompany(companyId))
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

    override suspend fun createClient(client: Client): Result<Client> = tryWrapper {
        val response = this.client.post(Urls.CLIENT) {
            setBody(client.asNetworkClient())
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<NetworkClient>>()
        apiResponse.asResult().map { it.asClient() }
    }

    override suspend fun getClient(clientId: String): Result<Client> = tryWrapper {
        val response = client.get(Urls.getClient(clientId))
        val apiResponse = response.body<ApiResponse<NetworkClient>>()
        apiResponse.asResult().map { it.asClient() }
    }

    override suspend fun getClients(): Result<List<Client>> = tryWrapper {
        val response = client.get(Urls.CLIENT)
        val apiResponse = response.body<ApiResponse<List<NetworkClient>>>()
        apiResponse.asResult().map { clients -> clients.map { it.asClient() } }
    }

    override suspend fun updateClient(client: Client): Result<Client> = tryWrapper {
        val response = this.client.put(Urls.getClient(client.id)) {
            setBody(client)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<NetworkClient>>()
        apiResponse.asResult().map { it.asClient() }
    }

    override suspend fun deleteClient(clientId: String): Result<Client> = tryWrapper {
        val response = client.delete(Urls.getClient(clientId))
        val apiResponse = response.body<ApiResponse<NetworkClient>>()
        apiResponse.asResult().map { it.asClient() }
    }

    override suspend fun createItem(item: Item): Result<Item> = tryWrapper {
        val response = this.client.post(Urls.ITEM) {
            setBody(item.asNetworkItem())
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Item>>()
        apiResponse.asResult()
    }

    override suspend fun getItem(itemId: String): Result<Item> = tryWrapper {
        val response = client.get(Urls.getItem(itemId))
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getItems(): Result<List<Item>> = tryWrapper {
        val response = client.get(Urls.ITEM)
        val apiResponse = response.body<ApiResponse<List<NetworkItem>>>()
        apiResponse.asResult().map { items -> items.map { it.asItem() } }
    }

    override suspend fun updateItem(item: Item): Result<Item> = tryWrapper {
        val response = this.client.put(Urls.getItem(item.id)) {
            setBody(item.asNetworkItem())
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Item>>()
        apiResponse.asResult()
    }

    override suspend fun deleteItem(itemId: String): Result<Item> = tryWrapper {
        val response = client.delete(Urls.getItem(itemId))
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getUnitTypes(): Result<List<UnitType>> = tryWrapper {
        val response = client.get(Urls.UNIT_TYPES)
        val apiResponse = response.body<ApiResponse<List<NetworkUnitType>>>()
        apiResponse.asResult().map { unitTypes -> unitTypes.map { it.asUnitType() } }
    }

    override suspend fun createDocument(document: CreateDocumentDto): Result<DocumentDto> = tryWrapper {
        val response = client.post(Urls.DOCUMENT) {
            setBody(document)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<DocumentDto>>()
        apiResponse.asResult()
    }

    override suspend fun getDocument(documentId: String): Result<DocumentViewDto> = tryWrapper {
        val response = client.get(Urls.getDocument(documentId))
        val apiResponse = response.body<ApiResponse<DocumentViewDto>>()
        apiResponse.asResult()
    }

    override suspend fun getDocuments(): Result<List<DocumentDto>> = tryWrapper {
        val response = client.get(Urls.DOCUMENT)
        val apiResponse = response.body<ApiResponse<List<DocumentDto>>>()
        apiResponse.asResult()
    }

    override suspend fun updateDocument(document: UpdateDocumentDto): Result<DocumentDto> = tryWrapper {
        val response = client.put(Urls.getDocument(document.id)) {
            setBody(document)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<DocumentDto>>()
        apiResponse.asResult()
    }

    override suspend fun deleteDocument(documentId: String): Result<Unit> = tryWrapper{
        val response = client.delete(Urls.getDocument(documentId))
        val apiResponse = response.body<ApiResponse<Unit>>()
        apiResponse.asResult()
    }

    override suspend fun createBranch(branch: Branch): Result<Branch> = tryWrapper {
        val response = client.post(Urls.BRANCH) {
            setBody(branch)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }

    override suspend fun getBranch(branchId: String): Result<Branch> = tryWrapper {
        val response = client.get(Urls.getBranch(branchId))
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }

    override suspend fun getBranches(): Result<List<Branch>> = tryWrapper {
        val response = client.get(Urls.BRANCH)
        val apiResponse = response.body<ApiResponse<List<Branch>>>()
        apiResponse.asResult()
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> = tryWrapper {
        val response = client.put(Urls.getBranch(branch.id)) {
            setBody(branch)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }

    override suspend fun deleteBranch(branchId: String): Result<Branch> = tryWrapper {
        val response = client.delete(Urls.getBranch(branchId))
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }
}
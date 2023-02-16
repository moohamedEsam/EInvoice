package com.example.network

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.TaxView
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.network.models.*
import com.example.network.models.document.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorEInvoiceRemoteDataSource(private val client: HttpClient) : EInvoiceRemoteDataSource {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        val response = client.post(Urls.company()) {
            setBody(company)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

    override suspend fun login(credentials: Credentials): Result<Token> = tryWrapper {
        val response = client.post(Urls.login()) {
            setBody(credentials)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Token>>()
        apiResponse.asResult()
    }

    override suspend fun register(register: Register): Result<Token> = tryWrapper {
        val response = client.post(Urls.register()) {
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
        val response = client.get(Urls.company())
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
        val response = this.client.post(Urls.client()) {
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
        val response = client.get(Urls.client())
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
        val response = this.client.post(Urls.item()) {
            setBody(item.asNetworkItem())
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getItem(itemId: String): Result<Item> = tryWrapper {
        val response = client.get(Urls.getItem(itemId))
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getItems(): Result<List<Item>> = tryWrapper {
        val response = client.get(Urls.item())
        val apiResponse = response.body<ApiResponse<List<NetworkItem>>>()
        apiResponse.asResult().map { items -> items.map { it.asItem() } }
    }

    override suspend fun updateItem(item: Item): Result<Item> = tryWrapper {
        val response = this.client.put(Urls.getItem(item.id)) {
            setBody(item.asNetworkItem())
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getTaxTypes(): Result<List<TaxView>> = tryWrapper {
        val response = client.get(Urls.taxTypes())
        val apiResponse = response.body<ApiResponse<List<TaxView>>>()
        apiResponse.asResult()
    }


    override suspend fun deleteItem(itemId: String): Result<Item> = tryWrapper {
        val response = client.delete(Urls.getItem(itemId))
        val apiResponse = response.body<ApiResponse<NetworkItem>>()
        apiResponse.asResult().map { it.asItem() }
    }

    override suspend fun getUnitTypes(): Result<List<UnitType>> = tryWrapper {
        val response = client.get(Urls.unitTypes())
        val apiResponse = response.body<ApiResponse<List<NetworkUnitType>>>()
        apiResponse.asResult().map { unitTypes -> unitTypes.map { it.asUnitType() } }
    }

    override suspend fun createDocument(document: CreateDocumentDto): Result<NetworkDocument> =
        tryWrapper {
            val response = client.post(Urls.document()) {
                setBody(document)
                contentType(ContentType.Application.Json)
            }
            val apiResponse = response.body<ApiResponse<NetworkDocument>>()
            apiResponse.asResult()
        }

    override suspend fun getDocument(documentId: String): Result<DocumentView> = tryWrapper {
        val response = client.get(Urls.getDocument(documentId))
        val apiResponse = response.body<ApiResponse<NetworkDocumentView>>()
        apiResponse.asResult().map { it.asDocumentView() }
    }

    override suspend fun sendDocument(documentId: String): Result<Unit> = tryWrapper {
        val response = client.post(Urls.sendDocument(documentId))
        val apiResponse = response.body<ApiResponse<Unit>>()
        apiResponse.asResult()
    }

    override suspend fun getDocuments(): Result<List<NetworkDocument>> = tryWrapper {
        val response = client.get(Urls.document())
        val apiResponse = response.body<ApiResponse<PagedResponse<NetworkDocument>>>()
        apiResponse.asResult().map { it.data }
    }

    override suspend fun updateDocument(document: UpdateDocumentDto): Result<NetworkDocument> =
        tryWrapper {
            val response = client.put(Urls.getDocument(document.id)) {
                setBody(document)
                contentType(ContentType.Application.Json)
            }
            val apiResponse = response.body<ApiResponse<NetworkDocument>>()
            apiResponse.asResult()
        }

    override suspend fun deleteDocument(documentId: String): Result<Unit> = tryWrapper {
        val response = client.delete(Urls.getDocument(documentId))
        val apiResponse = response.body<ApiResponse<Unit>>()
        apiResponse.asResult()
    }

    override suspend fun syncDocumentsStatus(): Result<Unit> = tryWrapper {
        val response = client.post(Urls.syncDocumentStatus())
        val apiResponse = response.body<ApiResponse<Unit>>()
        apiResponse.asResult()
    }

    override suspend fun createBranch(branch: Branch): Result<Branch> = tryWrapper {
        val response = client.post(Urls.branch()) {
            setBody(branch)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }

    override suspend fun cancelDocument(documentId: String): Result<Unit> = tryWrapper {
        val response = client.post(Urls.cancelDocument(documentId))
        val apiResponse = response.body<ApiResponse<Unit>>()
        apiResponse.asResult()
    }

    override suspend fun getBranch(branchId: String): Result<Branch> = tryWrapper {
        val response = client.get(Urls.getBranch(branchId))
        val apiResponse = response.body<ApiResponse<Branch>>()
        apiResponse.asResult()
    }

    override suspend fun getBranches(): Result<List<Branch>> = tryWrapper {
        val response = client.get(Urls.branch())
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
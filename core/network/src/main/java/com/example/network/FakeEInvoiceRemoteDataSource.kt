package com.example.network

import com.example.common.models.Result
import com.example.models.Client
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.empty
import com.example.models.invoiceLine.TaxView
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.models.item.empty
import com.example.network.models.document.CreateDocumentDto
import com.example.network.models.document.NetworkDocument
import com.example.network.models.document.UpdateDocumentDto
import com.example.network.models.document.empty

class FakeEInvoiceRemoteDataSource : EInvoiceRemoteDataSource {
    override suspend fun login(credentials: Credentials): Result<Token> =
        Result.Success(Token("token"))

    override suspend fun register(register: Register): Result<Token> =
        Result.Success(Token("token"))

    override suspend fun logout(): Result<Unit> = Result.Success(Unit)

    override suspend fun createCompany(company: Company): Result<Company> =
        Result.Success(company)

    override suspend fun getCompany(companyId: String): Result<Company> =
        Result.Success(Company.empty().copy(id = companyId))

    override suspend fun getCompanies(): Result<List<Company>> =
        Result.Success(listOf(Company.empty()))

    override suspend fun updateCompany(company: Company): Result<Company> = Result.Success(company)

    override suspend fun deleteCompany(companyId: String): Result<Company> =
        Result.Success(Company.empty().copy(id = companyId))

    override suspend fun createClient(client: Client): Result<Client> = Result.Success(client)

    override suspend fun getClient(clientId: String): Result<Client> =
        Result.Success(Client.empty().copy(id = clientId))

    override suspend fun getClients(): Result<List<Client>> = Result.Success(listOf(Client.empty()))

    override suspend fun updateClient(client: Client): Result<Client> = Result.Success(client)

    override suspend fun deleteClient(clientId: String): Result<Client> =
        Result.Success(Client.empty().copy(id = clientId))

    override suspend fun createBranch(branch: Branch): Result<Branch> = Result.Success(branch)

    override suspend fun getBranch(branchId: String): Result<Branch> =
        Result.Success(Branch.empty().copy(id = branchId))

    override suspend fun getBranches(): Result<List<Branch>> =
        Result.Success(listOf(Branch.empty()))

    override suspend fun updateBranch(branch: Branch): Result<Branch> = Result.Success(branch)

    override suspend fun deleteBranch(branchId: String): Result<Branch> =
        Result.Success(Branch.empty().copy(id = branchId))

    override suspend fun createItem(item: Item): Result<Item> = Result.Success(item)

    override suspend fun getItem(itemId: String): Result<Item> =
        Result.Success(Item.empty().copy(id = itemId))

    override suspend fun getItems(): Result<List<Item>> = Result.Success(listOf(Item.empty()))

    override suspend fun updateItem(item: Item): Result<Item> = Result.Success(item)

    override suspend fun deleteItem(itemId: String): Result<Item> =
        Result.Success(Item.empty().copy(id = itemId))

    override suspend fun getUnitTypes(): Result<List<UnitType>> = Result.Success(listOf())

    override suspend fun getTaxTypes(): Result<List<TaxView>> = Result.Success(listOf())

    override suspend fun createDocument(document: CreateDocumentDto): Result<NetworkDocument> =
        Result.Success(NetworkDocument.empty())

    override suspend fun getDocument(documentId: String): Result<DocumentView> =
        Result.Success(DocumentView.empty().copy(id = documentId))

    override suspend fun getDocuments(): Result<List<NetworkDocument>> =
        Result.Success(listOf(NetworkDocument.empty()))

    override suspend fun updateDocument(document: UpdateDocumentDto): Result<NetworkDocument> =
        Result.Success(NetworkDocument.empty())

    override suspend fun deleteDocument(documentId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun syncDocumentsStatus(): Result<Unit> = Result.Success(Unit)

    override suspend fun cancelDocument(documentId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun sendDocument(documentId: String): Result<Unit> = Result.Success(Unit)
}
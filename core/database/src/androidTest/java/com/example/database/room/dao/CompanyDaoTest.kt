package com.example.database.room.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.database.models.branch.BranchEntity
import com.example.database.models.branch.asBranchEntity
import com.example.database.models.client.ClientEntity
import com.example.database.models.client.asClientEntity
import com.example.database.models.company.asCompanyEntity
import com.example.database.models.document.asDocumentEntity
import com.example.database.room.EInvoiceDatabase
import com.example.database.room.dao.CompanyDao
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.document.Document
import com.example.models.document.empty
import com.example.models.empty
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.UUID


@OptIn(ExperimentalCoroutinesApi::class)
class CompanyDaoTest {
    private lateinit var database: EInvoiceDatabase
    private lateinit var companyDao: CompanyDao
    private var company = Company.empty().asCompanyEntity()

    @Before
    fun setUp() {
        database = EInvoiceDatabase.createInMemoryDatabase()
        companyDao = database.getCompanyDao()
    }

    @Test
    fun getCompanies_returns_notDeleted() = runTest {
        company = company.copy(isDeleted = true)
        assertThat(company.isDeleted).isTrue()
        companyDao.insertCompany(company)
        companyDao.getCompanies().test {
            val companies = awaitItem()
            assertThat(companies.isEmpty()).isTrue()
        }
    }

    @Test
    fun getAllCompanies_returns_all() = runTest {
        companyDao.insertCompany(company)
        companyDao.insertCompany(company.copy(id = UUID.randomUUID().toString(), isDeleted = true))

        val companies = companyDao.getAllCompanies()
        assertThat(companies.size).isEqualTo(2)
        assertThat(companies.map { it.id }).contains(company.id)
    }

    @Test
    fun getCompanyById_returns_null_if_not_found() = runTest {
        var company = companyDao.getCompanyById("notFound")
        assertThat(company).isNull()
        company = Company.empty().asCompanyEntity()
        companyDao.insertCompany(company)
        company = companyDao.getCompanyById(company.id)
        assertThat(company).isNotNull()
    }

    @Test
    fun getCompanyViewById_returns_null_if_not_found() = runTest {
        companyDao.getCompanyViewById("notFound").test {
            val company = awaitItem()
            assertThat(company).isNull()
        }
    }

    @Test
    fun getCompanyViewById_returns_company_with_branches_clients_documents() = runTest {
        createCompanyView()
        companyDao.getCompanyViewById(company.id).test {
            val company = awaitItem()
            assertThat(company).isNotNull()
            assertThat(company?.branches?.size).isEqualTo(1)
            assertThat(company?.clients?.size).isEqualTo(1)
            assertThat(company?.documents?.size).isEqualTo(1)
        }
    }

    @Test
    fun deleteCompany_deletes_branches_clients_documents() = runTest {
        createCompanyView()
        companyDao.deleteCompany(company.id)
        companyDao.getCompanyViewById(company.id).test {
            val company = awaitItem()
            assertThat(company).isNull()
        }
        database.getBranchDao().getBranches().test {
            val branches = awaitItem()
            assertThat(branches.isEmpty()).isTrue()
        }
        database.getClientDao().getClients().test {
            val clients = awaitItem()
            assertThat(clients.isEmpty()).isTrue()
        }
        database.getDocumentDao().getDocuments().test {
            val documents = awaitItem()
            assertThat(documents.isEmpty()).isTrue()
        }
    }

    @Test
    fun updateBranchCompanyId_notFoundId_throwsException() = runTest {
        companyDao.insertCompany(company)
        val branch = createCompanyBranch(company.id)
        database.getBranchDao().insertBranch(branch)
        val newCompanyId = UUID.randomUUID().toString()
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                companyDao.updateBranchesCompanyId(company.id, newCompanyId)
            }
        }
    }

    private suspend fun createCompanyView() {
        companyDao.insertCompany(company)
        val branch = createCompanyBranch(company.id)
        val client = createCompanyClient(company.id)
        database.getBranchDao().insertBranch(branch)
        database.getClientDao().insertClient(client)
        database.getDocumentDao().insertDocument(createCompanyDocument(client, branch, company.id))
    }

    @After
    fun tearDown() {
        database.close()
    }

}

fun createCompanyDocument(
    client: ClientEntity,
    branch: BranchEntity,
    companyId: String
) = Document.empty()
    .copy(issuerId = companyId, receiverId = client.id, branchId = branch.id)
    .asDocumentEntity()

fun createCompanyClient(companyId: String) =
    Client.empty().copy(companyId = companyId).asClientEntity()

fun createCompanyBranch(companyId: String) =
    Branch.empty().copy(companyId = companyId).asBranchEntity()

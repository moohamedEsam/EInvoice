package com.example.data.company

import app.cash.turbine.test
import com.example.data.sync.Synchronizer
import com.example.database.models.branch.asBranchEntity
import com.example.database.models.company.asCompany
import com.example.database.models.company.asCompanyEntity
import com.example.database.room.EInvoiceDatabase
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.network.FakeEInvoiceRemoteDataSource
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runners.model.TestTimedOutException
import kotlin.test.assertFails

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstCompanyRepositoryTest {
    private lateinit var database: EInvoiceDatabase
    private lateinit var repository: CompanyRepository
    private val synchronizer: Synchronizer = Synchronizer()

    @Before
    fun setUp() {
        database = EInvoiceDatabase.createInMemoryDatabase()
        repository =
            OfflineFirstCompanyRepository(database.getCompanyDao(), FakeEInvoiceRemoteDataSource())
    }

    @Test
    fun getCompany_not_found_returns_empty_flow(): Unit =
        runBlocking { // runBlockingTest won't advance delay
            val job = launch {
                repository.getCompany("1").first()
                Assert.fail("Should not reach here")
            }
            // Wait for 1 second then cancel the job since it should not emit any value
            launch {
                delay(1000)
                job.cancel()
            }
        }

    @Test
    fun getCompany_found_returns_company() = runTest {
        val company = Company.empty()
        repository.createCompany(company)
        repository.getCompany(company.id).test {
            val companyView = awaitItem()
            assertThat(companyView.company.id).isEqualTo(company.id)
        }
    }

    @Test
    fun getCompanyView_should_remove_deleted_branches() = runTest {
        val company = Company.empty()
        repository.createCompany(company)
        val branch = createCompanyBranch(company.id).copy(isDeleted = true)
        database.getBranchDao().insertBranch(branch)
        repository.getCompany(company.id).test {
            val companyView = awaitItem()
            assertThat(companyView.branches).isEmpty()
        }
    }

    @Test
    fun updateCreatedCompany_should_stay_isCreated_flag() = runTest {
        val company = Company.empty()
        repository.createCompany(company)
        val updatedCompany = company.copy(name = "Updated")
        repository.updateCompany(updatedCompany)
        val companyEntity = database.getCompanyDao().getCompanyById(company.id)
        assertThat(companyEntity?.isCreated).isTrue()
        assertThat(companyEntity?.name).isEqualTo(updatedCompany.name)
        assertThat(companyEntity?.isUpdated).isFalse()
    }

    @Test
    fun updateCompany_should_update_isUpdated_flag() = runTest {
        val company = Company.empty().asCompanyEntity()
        database.getCompanyDao().insertCompany(company)
        val updatedCompany = company.copy(name = "Updated")
        repository.updateCompany(updatedCompany.asCompany())
        val companyEntity = database.getCompanyDao().getCompanyById(company.id)
        assertThat(companyEntity?.name).isEqualTo(updatedCompany.name)
        assertThat(companyEntity?.isUpdated).isTrue()
        assertThat(companyEntity?.isCreated).isFalse()
    }

    @After
    fun tearDown() {
        database.close()
    }
}

fun createCompanyBranch(companyId: String) =
    Branch.empty().copy(companyId = companyId).asBranchEntity()
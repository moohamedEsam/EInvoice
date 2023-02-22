package com.example.database.room.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.database.models.asItemEntity
import com.example.database.models.branch.asBranchEntity
import com.example.database.models.company.asCompanyEntity
import com.example.database.room.EInvoiceDatabase
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.item.Item
import com.example.models.item.empty
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
class BranchDaoTest {
    private lateinit var database: EInvoiceDatabase
    private lateinit var branchDao: BranchDao
    private val company = Company.empty().asCompanyEntity()
    private var branch = createCompanyBranch(companyId = company.id)

    @Before
    fun setUp() = runBlocking {
        database = EInvoiceDatabase.createInMemoryDatabase()
        database.getCompanyDao().insertCompany(company)
        branchDao = database.getBranchDao()
    }

    @Test
    fun getBranches_returns_notDeleted() = runTest {
        branch = branch.copy(isDeleted = true)
        assertThat(branch.isDeleted).isTrue()
        branchDao.insertBranch(branch)
        branchDao.getBranches().test {
            val branches = awaitItem()
            assertThat(branches.isEmpty()).isTrue()
        }
    }

    @Test
    fun getAllBranches_returns_all() = runTest {
        branchDao.insertBranch(branch)
        branchDao.insertBranch(branch.copy(id = UUID.randomUUID().toString(), isDeleted = true))
        val branches = branchDao.getAllBranches()
        assertThat(branches.size).isEqualTo(2)
        assertThat(branches.map { it.id }).contains(branch.id)
    }

    @Test
    fun getBranchById_returns_null_if_not_found() = runTest {
        var branch = branchDao.getBranchById("notFound")
        assertThat(branch).isNull()
        branch = createCompanyBranch(company.id)
        branchDao.insertBranch(branch)
        branch = branchDao.getBranchById(branch.id)
        assertThat(branch).isNotNull()
    }

    @Test
    fun getBranchViewById_returns_null_if_not_found() = runTest {
        branchDao.getBranchViewById("notFound").test {
            val branch = awaitItem()
            assertThat(branch).isNull()
        }
    }

    @Test
    fun getBranchViewById_returns_branch_with_branches_clients_documents() = runTest {
        createBranchView()
        branchDao.getBranchViewById(branch.id).test {
            val branch = awaitItem()
            assertThat(branch).isNotNull()
            assertThat(branch?.items?.size).isEqualTo(1)
        }
    }

    @Test
    fun deleteBranch_deletes_items() = runTest {
        createBranchView()
        branchDao.deleteBranch(branch.id)
        branchDao.getBranchViewById(branch.id).test {
            val branch = awaitItem()
            assertThat(branch).isNull()
        }
        database.getItemDao().getItemsByBranchId(branch.id).test {
            val items = awaitItem()
            assertThat(items.isEmpty()).isTrue()
        }
    }

    @Test
    fun updateBranchId_notFoundId_throwsException() = runTest {
        branchDao.insertBranch(branch)
        val item = createBranchItem(branch.id)
        database.getItemDao().insertItem(item)
        val newBranchId = UUID.randomUUID().toString()
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                branchDao.updateItemsBranchId(branch.id, newBranchId)
            }
        }
    }

    private suspend fun createBranchView() {
        branchDao.insertBranch(branch)
        val item = createBranchItem(branch.id)
        database.getItemDao().insertItem(item)
    }

    @After
    fun tearDown() {
        database.close()
    }

}

fun createBranchItem(branchId: String) = Item.empty().copy(branchId = branchId).asItemEntity()
package com.example.database.room.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.database.models.company.asCompanyEntity
import com.example.database.models.invoiceLine.asInvoiceLineEntity
import com.example.database.room.EInvoiceDatabase
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.invoiceLine.InvoiceLine
import com.example.models.invoiceLine.empty
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
class ItemDaoTest {
    private lateinit var database: EInvoiceDatabase
    private lateinit var itemDao: ItemDao
    private val company = Company.empty().asCompanyEntity()
    private val branch = createCompanyBranch(companyId = company.id)
    private var item = createBranchItem(branchId = branch.id)

    @Before
    fun setUp() = runBlocking {
        database = EInvoiceDatabase.createInMemoryDatabase()
        database.getCompanyDao().insertCompany(company)
        database.getBranchDao().insertBranch(branch)
        itemDao = database.getItemDao()
    }

    @Test
    fun deleteItem_deletes_all_its_invoices() = runTest {
        val client = createCompanyClient(companyId = company.id)
        val document = createCompanyDocument(client, branch, company.id)
        database.getClientDao().insertClient(client)
        val invoice = createDocumentInvoice(document.id, item.id)
        itemDao.insertItem(item)
        database.getDocumentDao().insertDocumentWithInvoices(document, listOf(invoice))
        database.getDocumentDao().getDocumentById(document.id).test {
            val invoices = awaitItem().invoices
            assertThat(invoices).isNotEmpty()
        }
        itemDao.deleteItem(item.id)
        database.getDocumentDao().getDocumentById(document.id).test {
            val invoices = awaitItem().invoices
            assertThat(invoices).isEmpty()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

}


fun createDocumentInvoice(documentId: String, itemId: String) =
    InvoiceLine.empty().copy(itemId = itemId, documentId = documentId).asInvoiceLineEntity()

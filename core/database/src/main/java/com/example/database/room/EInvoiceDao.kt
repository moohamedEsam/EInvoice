package com.example.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.database.models.*
import com.example.database.models.company.CompanyEntity
import com.example.database.models.document.DocumentEntity
import com.example.database.models.document.DocumentViewEntity
import com.example.database.models.invoiceLine.InvoiceLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EInvoiceDao {
    // Branch
    @Query("SELECT * FROM Branch")
    fun getBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE companyId = :companyId")
    fun getBranchesByCompanyId(companyId: String): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE id = :id")
    fun getBranchById(id: String): Flow<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 0 and isCreated = 1")
    suspend fun getCreatedBranches(): List<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 0 and isUpdated = 1")
    suspend fun getUpdatedBranches(): List<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 1")
    suspend fun getDeletedBranches(): List<BranchEntity>

    @Insert
    suspend fun insertBranch(branch: BranchEntity)

    @Query("DELETE FROM Branch where id = :id")
    suspend fun deleteBranch(id: String)

    @Update
    suspend fun updateBranch(branch: BranchEntity)

    @Query("update Branch set companyId =:newCompanyId where companyId = :oldCompanyId")
    suspend fun updateBranchesCompanyId(oldCompanyId: String, newCompanyId: String)

    // Company
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getCompanies(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM Company WHERE id = :id and isDeleted = 0")
    fun getCompanyById(id: String): Flow<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isUpdated = 1 and isDeleted = 0")
    suspend fun getUpdatedCompanies(): List<CompanyEntity>

    @Query("delete from branch")
    suspend fun deleteAllBranches()

    @Query("SELECT * FROM Company WHERE isCreated = 1 and isDeleted = 0")
    suspend fun getCreatedCompanies(): List<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isDeleted = 1")
    suspend fun getDeletedCompanies(): List<CompanyEntity>

    @Insert
    suspend fun insertCompany(company: CompanyEntity)

    @Query("DELETE FROM Company where id = :id")
    suspend fun deleteCompany(id: String)

    @Query("delete from company")
    suspend fun deleteAllCompanies()

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Query("update Company set isDeleted = 1 where id = :id")
    suspend fun markCompanyAsDeleted(id: String)

    @Query("update Company set isDeleted = 0 where id = :id")
    suspend fun undoDeleteCompany(id: String)


    // Client
    @Query("SELECT * FROM Client")
    fun getClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE companyId = :companyId")
    fun getClientsByCompanyId(companyId: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE id = :id")
    fun getClientById(id: String): Flow<ClientEntity>

    @Query("SELECT * FROM Client WHERE isDeleted = 0 and isCreated = 1")
    suspend fun getCreatedClients(): List<ClientEntity>

    @Query("SELECT * FROM Client WHERE isDeleted = 0 and isUpdated = 1")
    suspend fun getUpdatedClients(): List<ClientEntity>

    @Query("SELECT * FROM Client WHERE isDeleted = 1")
    suspend fun getDeletedClients(): List<ClientEntity>

    @Insert
    suspend fun insertClient(client: ClientEntity)

    @Query("DELETE FROM Client where id = :id")
    suspend fun deleteClient(id: String)

    @Query("delete from client")
    suspend fun deleteAllClients()

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Query("update Client set companyId =:newCompanyId where companyId = :oldCompanyId")
    suspend fun updateClientsCompanyId(oldCompanyId: String, newCompanyId: String)

    // Item
    @Query("SELECT * FROM Item")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE branchId = :branchId")
    fun getItemsByBranchId(branchId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE id = :id")
    fun getItemById(id: String): Flow<ItemEntity>

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Query("DELETE FROM Item where id = :id")
    suspend fun deleteItem(id: String)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query("update Item set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateItemsBranchId(oldBranchId: String, newBranchId: String)

    @Query("SELECT * FROM Item WHERE isDeleted = 0 and isCreated = 1")
    suspend fun getCreatedItems(): List<ItemEntity>

    @Query("SELECT * FROM Item WHERE isDeleted = 0 and isUpdated = 1")
    suspend fun getUpdatedItems(): List<ItemEntity>

    @Query("SELECT * FROM Item WHERE isDeleted = 1")
    suspend fun getDeletedItems(): List<ItemEntity>

    @Query("delete from item")
    suspend fun deleteAllItems()

    @Insert
    suspend fun insertUnitType(unitType: UnitTypeEntity)

    @Query("SELECT * FROM UnitType")
    fun getUnitTypes(): Flow<List<UnitTypeEntity>>

    @Query("delete FROM UnitType")
    suspend fun deleteAllUnitTypes()

    //invoice line
    @Query("SELECT * FROM InvoiceLine")
    fun getInvoiceLines(): Flow<List<InvoiceLineEntity>>

    @Query("SELECT * FROM InvoiceLine WHERE id = :id")
    fun getInvoiceLineById(id: String): Flow<InvoiceLineEntity>

    @Insert
    suspend fun insertInvoiceLine(invoiceLine: InvoiceLineEntity)

    @Insert
    suspend fun insertInvoiceLines(invoiceLines: List<InvoiceLineEntity>)

    @Update
    suspend fun updateInvoiceLine(invoiceLine: InvoiceLineEntity)

    @Query("update InvoiceLine set itemId =:newId where itemId = :oldId")
    suspend fun updateInvoiceLinesItemId(oldId: String, newId: String)

    @Query("update InvoiceLine set documentId =:newId where documentId = :oldId")
    suspend fun updateInvoiceLinesDocumentId(oldId: String, newId: String)

    @Query("DELETE FROM InvoiceLine where id = :id")
    suspend fun deleteInvoiceLine(id: String)

    @Query("delete from InvoiceLine")
    suspend fun deleteAllInvoiceLines()

    //document
    @Query("SELECT * FROM Document")
    fun getDocuments(): Flow<List<DocumentEntity>>

    @Transaction
    @Query("SELECT * FROM Document")
    fun getDocumentsView(): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE id = :id")
    fun getDocumentById(id: String): Flow<DocumentViewEntity>

    @Insert
    suspend fun insertDocument(document: DocumentEntity)

    @Insert
    suspend fun insertDocumentWithInvoices(
        document: DocumentEntity,
        invoiceLines: List<InvoiceLineEntity>
    )

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Update
    @Transaction
    suspend fun updateDocument(document: DocumentEntity, invoiceLines: List<InvoiceLineEntity>) {
        updateDocument(document)
        deleteAllInvoiceLines()
        insertInvoiceLines(invoiceLines)
    }

    @Query("update document set issuerId =:newIssuerId where issuerId = :oldIssuerId")
    suspend fun updateDocumentsIssuerId(oldIssuerId: String, newIssuerId: String)

    @Query("update document set receiverId =:newReceiverId where receiverId = :oldReceiverId")
    suspend fun updateDocumentsReceiverId(oldReceiverId: String, newReceiverId: String)

    @Query("update document set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateDocumentsBranchId(oldBranchId: String, newBranchId: String)

    @Query("DELETE FROM Document where id = :id")
    suspend fun deleteDocument(id: String)

    @Query("update Document set isDeleted = 1 where id = :id")
    suspend fun markDocumentAsDeleted(id: String)


    @Query("update Document set isDeleted = 0 where id = :id")
    suspend fun undoDeleteDocument(id: String)

    @Query("DELETE FROM InvoiceLine where documentId = :id")
    suspend fun deleteInvoicesByDocumentId(id: String)


    @Query("delete from Document")
    suspend fun deleteAllDocuments()

    @Transaction
    suspend fun deleteAll() {
        deleteAllInvoiceLines()
        deleteAllItems()
        deleteAllClients()
        deleteAllBranches()
        deleteAllCompanies()
        deleteAllDocuments()
    }
}
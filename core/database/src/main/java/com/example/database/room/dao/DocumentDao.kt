package com.example.database.room.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.database.models.document.DocumentEntity
import com.example.database.models.document.DocumentViewEntity
import com.example.database.models.invoiceLine.InvoiceLineEntity
import com.example.models.document.DocumentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert
    suspend fun insertInvoiceLines(invoiceLines: List<InvoiceLineEntity>)
    @Query("SELECT * FROM Document WHERE isDeleted = 0 order by date desc")
    fun getDocuments(): Flow<List<DocumentEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE issuerId = :id and isDeleted = 0 and date between :fromDate and :toDate order by date desc")
    fun getDocumentsByCompanyInDuration(
        id: String,
        fromDate: Long,
        toDate: Long
    ): Flow<List<DocumentViewEntity>>

    @Query("SELECT internalId FROM Document WHERE issuerId = :id and isDeleted = 0 and id != :excludedDocumentId")
    fun getDocumentsInternalIdsByCompanyId(
        id: String,
        excludedDocumentId: String
    ): Flow<List<String>>

    @Query("SELECT * FROM Document WHERE issuerId = :id and isDeleted = 0 order by date desc")
    fun getDocumentsByCompany(id: String): Flow<List<DocumentEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE branchId = :id and isDeleted = 0 and date between :fromDate and :toDate order by date desc")
    fun getDocumentsByBranch(
        id: String,
        fromDate: Long,
        toDate: Long
    ): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE receiverId = :id and isDeleted = 0 and date between :fromDate and :toDate order by date desc")
    fun getDocumentsByClient(
        id: String,
        fromDate: Long,
        toDate: Long
    ): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document where isDeleted = 0 order by date desc")
    fun getDocumentsView(): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document order by date desc")
    fun getAllDocuments(): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE isDeleted = 0 order by date desc")
    fun getPagedDocuments(): DataSource.Factory<Int, DocumentViewEntity>

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
        deleteInvoicesByDocumentId(document.id)
        insertInvoiceLines(invoiceLines)
    }

    @Query("DELETE FROM Document where id = :id")
    suspend fun deleteDocument(id: String)

    @Query("update Document set isDeleted = 1 where id = :id")
    suspend fun markDocumentAsDeleted(id: String)


    @Query("update Document set isDeleted = 0 where id = :id")
    suspend fun undoDeleteDocument(id: String)

    @Query("update Document set status = :status where id = :id")
    suspend fun updateDocumentStatus(id: String, status: DocumentStatus)

    @Query("DELETE FROM InvoiceLine where documentId = :id")
    suspend fun deleteInvoicesByDocumentId(id: String)


    @Query("delete from Document")
    suspend fun deleteAllDocuments()
}
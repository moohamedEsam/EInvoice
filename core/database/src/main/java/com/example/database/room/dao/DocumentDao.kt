package com.example.database.room.dao

import androidx.room.*
import com.example.database.models.document.DocumentEntity
import com.example.database.models.document.DocumentViewEntity
import com.example.database.models.invoiceLine.InvoiceLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
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

    @Query("update InvoiceLine set documentId =:newId where documentId = :oldId")
    suspend fun updateInvoiceLinesDocumentId(oldId: String, newId: String)

    @Query("DELETE FROM InvoiceLine where id = :id")
    suspend fun deleteInvoiceLine(id: String)

    @Query("delete from InvoiceLine where documentId = :documentId")
    suspend fun deleteInvoiceLinesByDocumentId(documentId: String)

    @Query("delete from InvoiceLine")
    suspend fun deleteAllInvoiceLines()

    //document
    @Query("SELECT * FROM Document")
    fun getDocuments(): Flow<List<DocumentEntity>>

    @Transaction
    @Query("SELECT * FROM Document WHERE issuerId = :id and isDeleted = 0 order by date desc")
    fun getDocumentsByCompany(id: String): Flow<List<DocumentViewEntity>>

    @Transaction
    @Query("SELECT * FROM Document where isDeleted = 0 order by date desc")
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
        deleteInvoicesByDocumentId(document.id)
        insertInvoiceLines(invoiceLines)
    }

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
}
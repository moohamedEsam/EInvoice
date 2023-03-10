package com.example.data.document

import androidx.paging.PagingSource
import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.document.Document
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.InvoiceLine
import kotlinx.coroutines.flow.Flow

interface DocumentRepository : Syncable<Document> {
    suspend fun createDocument(document: DocumentView): Result<DocumentView>

    fun getDocumentsInternalIdsByCompanyId(id: String, excludedDocumentId:String): Flow<List<String>>

    fun getDocument(id: String): Flow<DocumentView>

    suspend fun updateDocument(document: DocumentView): Result<DocumentView>

    suspend fun deleteDocument(id: String): Result<Unit>

    suspend fun undoDeleteDocument(id: String): Result<Unit>

    fun getDocuments(): Flow<List<DocumentView>>

    fun getDocumentsPagingSource(): PagingSource<Int, DocumentView>

    fun getDocumentsViewByCompanyInDuration(
        companyId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>>


    fun getDocumentsByBranchInDuration(
        branchId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>>

    fun getDocumentsByClientInDuration(
        clientId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>>

    suspend fun syncDocumentsStatus(): Result<Unit>

    suspend fun cancelDocument(id: String): Result<Unit>

    suspend fun sendDocument(id: String): Result<Unit>

    suspend fun createDerivedDocument(id:String, invoiceLines:List<InvoiceLine>) : Result<Document>

}
package com.example.data.document

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.document.DocumentViewEntity
import com.example.database.models.document.asDocumentEntity
import com.example.database.models.document.asDocumentView
import com.example.database.models.document.asDocumentViewEntity
import com.example.database.models.invoiceLine.asInvoiceLineEntity
import com.example.database.room.dao.DocumentDao
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.asDocument
import com.example.models.invoiceLine.asInvoiceLine
import com.example.network.EInvoiceRemoteDataSource
import com.example.network.models.document.asCreateDocumentDto
import com.example.network.models.document.asNetworkDocumentView
import com.example.network.models.document.asUpdateDocumentDto
import kotlinx.coroutines.flow.*

class OfflineFirstDocumentRepository(
    private val localDataSource: DocumentDao,
    private val remoteDataSource: EInvoiceRemoteDataSource
) : DocumentRepository {
    override suspend fun createDocument(document: DocumentView): Result<DocumentView> = tryWrapper {
        val documentEntity = document.asDocument().asDocumentEntity(isCreated = true)
        val invoiceLines = document.invoices.map { it.asInvoiceLine().asInvoiceLineEntity() }
        localDataSource.insertDocumentWithInvoices(documentEntity, invoiceLines)
        Result.Success(document)
    }

    override suspend fun cancelDocument(id: String): Result<Unit> = tryWrapper {
        val document = localDataSource.getDocumentById(id).firstOrNull()
            ?: return@tryWrapper Result.Error("Document not found")
        val cancelResult = remoteDataSource.cancelDocument(document.documentEntity.id)
        cancelResult.ifSuccess {
            localDataSource.updateDocumentStatus(document.documentEntity.id, DocumentStatus.Cancelled)
        }
        cancelResult
    }

    override fun getDocument(id: String): Flow<DocumentView> =
        localDataSource.getDocumentById(id).distinctUntilChanged().map { it.asDocumentView() }

    override suspend fun updateDocument(document: DocumentView): Result<DocumentView> = tryWrapper {
        val documentEntity = document.asDocument().asDocumentEntity(isUpdated = true)
        val invoiceLines = document.invoices.map { it.asInvoiceLine().asInvoiceLineEntity() }
        localDataSource.updateDocument(documentEntity, invoiceLines)
        Result.Success(document)
    }

    override suspend fun deleteDocument(id: String): Result<Unit> = tryWrapper {
        localDataSource.markDocumentAsDeleted(id)
        Result.Success(Unit)
    }

    override suspend fun syncDocumentsStatus(): Result<Unit> = tryWrapper {
        val syncResult = remoteDataSource.syncDocumentsStatus()
        if (syncResult is Result.Success) {
            val documentsResult = remoteDataSource.getDocuments()
            documentsResult.ifSuccess { networkDocuments ->
                networkDocuments.forEach { document ->
                    val status = DocumentStatus.values().first { it.ordinal == document.status }
                    localDataSource.updateDocumentStatus(document.id, status)
                }
            }
            return@tryWrapper documentsResult.map { }
        }
        syncResult
    }

    override suspend fun undoDeleteDocument(id: String): Result<Unit> = tryWrapper {
        localDataSource.undoDeleteDocument(id)
        Result.Success(Unit)
    }

    override fun getDocuments(): Flow<List<DocumentView>> =
        localDataSource.getDocumentsView().distinctUntilChanged()
            .map { documents -> documents.map { it.asDocumentView() } }

    override fun getDocumentsByCompany(
        companyId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByCompany(companyId, fromDateMillis, toDateMillis)
        .distinctUntilChanged()
        .map { documents -> documents.map { it.asDocumentView() } }

    override fun getDocumentsByBranch(
        branchId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByBranch(branchId, fromDateMillis, toDateMillis)
        .distinctUntilChanged()
        .map { documents -> documents.map { it.asDocumentView() } }

    override fun getDocumentsByClient(
        clientId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByClient(clientId, fromDateMillis, toDateMillis)
        .distinctUntilChanged()
        .map { documents -> documents.map { it.asDocumentView() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val documents = localDataSource.getDocumentsView().first()
        val idMappings = HashMap<String, String>()
        return synchronizer.handleSync(
            remoteFetcher = fetcher@{
                val ids = remoteDataSource.getDocuments()
                    .map { documents -> documents.map { it.id } }

                if (ids !is Result.Success) return@fetcher Result.Error("Failed to fetch documents")

                val remoteDocuments = ids.data.mapNotNull { id ->
                    getDocumentEntityFromRemoteSource(id)
                }

                Result.Success(remoteDocuments)
            },
            remoteDeleter = {
                val deletedDocuments = documents.filter { it.documentEntity.isDeleted }
                    .map { it.asDocumentView().asDocument() }
                deletedDocuments.forEach { document ->
                    val result = remoteDataSource.deleteDocument(document.id)
                    if (result is Result.Success)
                        localDataSource.deleteDocument(document.id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedDocuments = documents.filter { it.documentEntity.isUpdated }
                updatedDocuments.forEach { document ->
                    val result = remoteDataSource.updateDocument(
                        document.asDocumentView().asNetworkDocumentView().asUpdateDocumentDto()
                    )
                    if (result is Result.Success) {
                        localDataSource.updateDocument(
                            document.asDocumentView().asDocument()
                                .asDocumentEntity(isUpdated = false)
                        )
                    }
                }
                Result.Success(Unit)
            },
            remoteCreator = {
                val createdDocuments = documents.filter { it.documentEntity.isCreated }
                createdDocuments.forEach { document ->
                    val result = remoteDataSource.createDocument(
                        document.asDocumentView().asNetworkDocumentView().asCreateDocumentDto()
                    )
                    if (result is Result.Success) {
                        idMappings[document.documentEntity.id] = result.data.id
                    }
                }
                Result.Success(Unit)
            },
            localCreator = { documentView ->
                if (documentView.documentEntity.id !in documents.map { it.documentEntity.id })
                    localDataSource.insertDocumentWithInvoices(
                        documentView.documentEntity,
                        documentView.invoices.map { it.asInvoiceLineEntity() }
                    )
                else
                    localDataSource.updateDocument(documentView.documentEntity)
            },
            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    localDataSource.updateInvoiceLinesDocumentId(oldId, newId)
                    localDataSource.deleteDocument(oldId)
                }
            },
        )
    }

    private suspend fun getDocumentEntityFromRemoteSource(id: String): DocumentViewEntity? {
        val documentResult =
            remoteDataSource.getDocument(id).map(DocumentView::asDocumentViewEntity)

        return if (documentResult is Result.Success)
            documentResult.data
        else
            null
    }
}
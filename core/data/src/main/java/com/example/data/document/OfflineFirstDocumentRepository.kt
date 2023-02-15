package com.example.data.document

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.document.*
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

    override fun getDocumentsInternalIdsByCompanyId(id: String, excludedDocumentId:String): Flow<List<String>> =
        localDataSource.getDocumentsInternalIdsByCompanyId(id, excludedDocumentId)

    override suspend fun cancelDocument(id: String): Result<Unit> = tryWrapper {
        val document = localDataSource.getDocumentById(id).firstOrNull()
            ?: return@tryWrapper Result.Error("Document not found")
        val cancelResult = remoteDataSource.cancelDocument(document.documentEntity.id)
        cancelResult.ifSuccess {
            localDataSource.updateDocumentStatus(
                document.documentEntity.id,
                DocumentStatus.Cancelled
            )
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

    override fun getDocumentsViewByCompanyInDuration(
        companyId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByCompanyInDuration(companyId, fromDateMillis, toDateMillis)
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
        val documents = localDataSource.getAllDocuments().first()
        val idMappings = HashMap<String, String>()
        return synchronizer.handleSync(
            remoteFetcher = fetcher@{
                val ids = remoteDataSource.getDocuments()
                    .map { documents -> documents.map { it.id } }

                if (ids !is Result.Success) return@fetcher Result.Error("Failed to fetch documents")

                val remoteDocuments = ids.data.mapNotNull { id ->
                    getDocumentViewFromRemoteSource(id)?.asDocumentViewEntity()
                }

                Result.Success(remoteDocuments)
            },
            remoteDeleter = {
                val deletedDocumentsIds = documents.filter { it.documentEntity.isDeleted }
                    .map { it.documentEntity.id }
                deletedDocumentsIds.forEach { id ->
                    val result = remoteDataSource.deleteDocument(id)
                    if (result is Result.Success)
                        localDataSource.deleteDocument(id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedDocuments = documents.filter { it.documentEntity.isUpdated }
                updatedDocuments.forEach { document ->
                    val result = remoteDataSource.updateDocument(document.convertToUpdateDocument())
                    result.ifSuccess {
                        localDataSource.updateDocument(
                            document.asDocumentEntity().copy(isUpdated = false)
                        )
                    }
                }
                Result.Success(Unit)
            },
            remoteCreator = {
                val createdDocuments = documents.filter { it.documentEntity.isCreated }
                createdDocuments.forEach { document ->
                    val result = remoteDataSource.createDocument(document.convertToCreateDocument())
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
                idMappings.forEach { (oldId, _) ->
                    localDataSource.deleteDocument(oldId)
                }
            },
        )
    }

    private fun DocumentViewEntity.convertToUpdateDocument() =
        asDocumentView().asNetworkDocumentView().asUpdateDocumentDto()

    private fun DocumentViewEntity.convertToCreateDocument() =
        asDocumentView().asNetworkDocumentView().asCreateDocumentDto()

    private suspend fun getDocumentViewFromRemoteSource(id: String): DocumentView? {
        val documentResult =
            remoteDataSource.getDocument(id)

        return if (documentResult is Result.Success)
            documentResult.data
        else
            null
    }
}
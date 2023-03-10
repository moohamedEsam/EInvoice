package com.example.data.document

import androidx.paging.PagingSource
import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.document.*
import com.example.database.models.invoiceLine.asInvoiceLineEntity
import com.example.database.room.dao.DocumentDao
import com.example.models.document.Document
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.asDocument
import com.example.models.invoiceLine.InvoiceLine
import com.example.models.invoiceLine.asInvoiceLine
import com.example.network.EInvoiceRemoteDataSource
import com.example.network.models.document.NetworkDocument
import com.example.network.models.document.asCreateDocumentDto
import com.example.network.models.document.asNetworkDocumentView
import com.example.network.models.document.asUpdateDocumentDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.UUID

private const val DOCUMENT_NOT_FOUND = "Document not found"

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

    override fun getDocumentsInternalIdsByCompanyId(
        id: String,
        excludedDocumentId: String
    ): Flow<List<String>> =
        localDataSource.getDocumentsInternalIdsByCompanyId(id, excludedDocumentId)

    override suspend fun cancelDocument(id: String): Result<Unit> = tryWrapper {
        val document = localDataSource.getDocumentById(id).firstOrNull()
            ?: return@tryWrapper Result.Error(DOCUMENT_NOT_FOUND)
        val cancelResult = remoteDataSource.cancelDocument(document.documentEntity.id)
        cancelResult.ifSuccess {
            localDataSource.updateDocumentStatus(
                document.documentEntity.id,
                DocumentStatus.Cancelled
            )
        }
        cancelResult
    }

    override suspend fun sendDocument(id: String): Result<Unit> = tryWrapper {
        val document = localDataSource.getDocumentById(id).firstOrNull()
            ?: return@tryWrapper Result.Error(DOCUMENT_NOT_FOUND)
        val sendResult = remoteDataSource.sendDocument(document.documentEntity.id)
        val status =
            if (sendResult is Result.Success) DocumentStatus.Submitted else DocumentStatus.InvalidSent
        val error = (sendResult as? Result.Error)?.exception
        localDataSource.updateDocument(document.documentEntity.copy(status = status, error = error))
        sendResult
    }

    override fun getDocumentsPagingSource(): PagingSource<Int, DocumentView> =
        localDataSource.getPagedDocuments().map { it.asDocumentView() }.asPagingSourceFactory()
            .invoke()

    override suspend fun createDerivedDocument(
        id: String,
        invoiceLines: List<InvoiceLine>
    ): Result<Document> = tryWrapper {
        val document = localDataSource.getDocumentById(id).firstOrNull()
            ?: return@tryWrapper Result.Error(DOCUMENT_NOT_FOUND)

        val creditDocument =
            document.documentEntity.copy(id = UUID.randomUUID().toString(), isCreated = true)
        val creditInvoiceLines = invoiceLines.map { it.asInvoiceLineEntity() }
        localDataSource.insertDocumentWithInvoices(creditDocument, creditInvoiceLines)
        Result.Success(creditDocument.asDocument())
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

    override fun getDocumentsByBranchInDuration(
        branchId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByBranch(branchId, fromDateMillis, toDateMillis)
        .distinctUntilChanged()
        .map { documents -> documents.map { it.asDocumentView() } }

    override fun getDocumentsByClientInDuration(
        clientId: String,
        fromDateMillis: Long,
        toDateMillis: Long
    ): Flow<List<DocumentView>> = localDataSource
        .getDocumentsByClient(clientId, fromDateMillis, toDateMillis)
        .distinctUntilChanged()
        .map { documents -> documents.map { it.asDocumentView() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val documents = localDataSource.getAllDocuments().first()
        var idMappings = emptyMap<String, String?>()
        val remotelyCreatedDocuments = mutableListOf<String>()
        val result = synchronizer.handleSync(
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
                documents.filter { it.documentEntity.isDeleted }
                    .map { it.documentEntity.id }
                    .forEach { id ->
                        val result = remoteDataSource.deleteDocument(id)
                        result.ifSuccess {
                            localDataSource.deleteDocument(id)
                        }
                    }
            },
            remoteUpdater = {
                documents.filter { it.documentEntity.isUpdated }
                    .forEach { document ->
                        val result =
                            remoteDataSource.updateDocument(document.convertToUpdateDocument())
                        result.ifSuccess {
                            localDataSource.updateDocument(
                                document.asDocumentEntity().copy(isUpdated = false)
                            )
                        }
                    }
            },
            remoteCreator = {
                idMappings = createAndGetIdMappings(documents)
            },
            localCreator = { documentView ->
                remotelyCreatedDocuments.add(documentView.documentEntity.id)
                if (documentView.documentEntity.id !in documents.map { it.documentEntity.id })
                    localDataSource.insertDocumentWithInvoices(
                        documentView.documentEntity.copy(isSynced = true),
                        documentView.invoices.map { it.asInvoiceLineEntity() }
                    )
                else {
                    localDataSource.updateDocument(documentView.documentEntity.copy(isSynced = true))
                    localDataSource.deleteInvoicesByDocumentId(documentView.documentEntity.id)
                    localDataSource.insertInvoiceLines(documentView.invoices.map { it.asInvoiceLineEntity() })
                }
            },
            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    if(newId == null) return@forEach
                    localDataSource.deleteDocument(oldId)
                }
            },
        )
        return result
    }

    private suspend fun createAndGetIdMappings(documents: List<DocumentViewEntity>) =
        documents.filter { it.documentEntity.isCreated }
            .associateBy {
                val result = remoteDataSource.createDocument(it.convertToCreateDocument())
                handleCreateResult(result, it)
            }.map { (newId, document) ->
                document.documentEntity.id to newId
            }.toMap()


    private suspend fun handleCreateResult(
        result: Result<NetworkDocument>,
        documentViewEntity: DocumentViewEntity
    ) =
        if (result is Result.Success)
            result.data.id
        else
            null.also { handleCreateError(result, documentViewEntity) }


    private suspend fun handleCreateError(
        result: Result<NetworkDocument>,
        documentViewEntity: DocumentViewEntity
    ) {
        val error = (result as? Result.Error)?.exception
        localDataSource.updateDocument(
            documentViewEntity.documentEntity.copy(
                isSynced = false,
                syncError = error
            )
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


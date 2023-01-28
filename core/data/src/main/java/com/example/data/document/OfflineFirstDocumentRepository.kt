package com.example.data.document

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.document.DocumentEntity
import com.example.database.models.document.asDocument
import com.example.database.models.document.asDocumentEntity
import com.example.database.models.document.asDocumentView
import com.example.database.models.invoiceLine.asInvoiceLineEntity

import com.example.database.room.EInvoiceDao
import com.example.models.document.Document
import com.example.models.document.DocumentView
import com.example.models.document.asDocument
import com.example.models.invoiceLine.asInvoiceLine
import com.example.network.EInvoiceRemoteDataSource
import com.example.network.models.document.asCreateDocumentDto
import com.example.network.models.document.asDocumentView
import com.example.network.models.document.asDocumentViewDto
import com.example.network.models.document.asUpdateDocumentDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OfflineFirstDocumentRepository(
    private val localDataSource: EInvoiceDao,
    private val remoteDataSource: EInvoiceRemoteDataSource
) : DocumentRepository {
    override suspend fun createDocument(document: DocumentView): Result<Document> = tryWrapper {
        val documentEntity = document.asDocument().asDocumentEntity(isCreated = true)
        val invoiceLines = document.invoices.map { it.asInvoiceLine().asInvoiceLineEntity() }
        localDataSource.insertDocumentWithInvoices(documentEntity, invoiceLines)
        Result.Success(documentEntity.asDocument())
    }

    override fun getDocument(id: String): Flow<DocumentView> =
        localDataSource.getDocumentById(id).map { it.asDocumentView() }

    override suspend fun updateDocument(document: DocumentView): Result<Document> = tryWrapper {
        val documentEntity = document.asDocument().asDocumentEntity(isUpdated = true)
        val invoiceLines = document.invoices.map { it.asInvoiceLine().asInvoiceLineEntity() }
        localDataSource.updateDocument(documentEntity, invoiceLines)
        Result.Success(documentEntity.asDocument())
    }

    override suspend fun deleteDocument(id: String): Result<Unit> = tryWrapper {
        localDataSource.markDocumentAsDeleted(id)
        Result.Success(Unit)
    }

    override suspend fun undoDeleteDocument(id: String): Result<Unit> = tryWrapper {
        localDataSource.undoDeleteDocument(id)
        Result.Success(Unit)
    }

    override fun getDocuments(): Flow<List<Document>> =
        localDataSource.getDocuments().map { documents -> documents.map { it.asDocument() } }

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
                        document.asDocumentView().asDocumentViewDto().asUpdateDocumentDto()
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
                        document.asDocumentView().asDocumentViewDto().asCreateDocumentDto()
                    )
                    if (result is Result.Success) {
                        idMappings[document.documentEntity.id] = result.data.id
                    }
                }
                Result.Success(Unit)
            },
            localCreator = { documentEntity ->
                if (documentEntity.id !in documents.map { it.documentEntity.id })
                    localDataSource.insertDocument(documentEntity)
                else
                    localDataSource.updateDocument(documentEntity)
            },
            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    localDataSource.updateInvoiceLinesDocumentId(oldId, newId)
                    localDataSource.deleteDocument(oldId)
                }
            },
        )
    }

    private suspend fun getDocumentEntityFromRemoteSource(id: String): DocumentEntity? {
        val documentResult = remoteDataSource.getDocument(id)
            .map { it.asDocumentView().asDocument().asDocumentEntity() }

        return if (documentResult is Result.Success)
            documentResult.data
        else
            null
    }
}
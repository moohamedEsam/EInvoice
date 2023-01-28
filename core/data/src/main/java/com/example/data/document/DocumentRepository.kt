package com.example.data.document

import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.document.Document
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow

interface DocumentRepository : Syncable<Document> {
    suspend fun createDocument(document: DocumentView): Result<Document>

    fun getDocument(id: String): Flow<DocumentView>

    suspend fun updateDocument(document: DocumentView): Result<Document>

    suspend fun deleteDocument(id: String): Result<Unit>

    suspend fun undoDeleteDocument(id: String): Result<Unit>

    fun getDocuments(): Flow<List<Document>>
}
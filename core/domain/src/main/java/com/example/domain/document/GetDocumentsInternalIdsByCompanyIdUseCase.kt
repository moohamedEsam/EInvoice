package com.example.domain.document

import com.example.data.document.DocumentRepository
import kotlinx.coroutines.flow.Flow

fun interface GetDocumentsInternalIdsByCompanyIdUseCase :
        (GetDocumentsInternalIdsByCompanyIdUseCase.Params) -> Flow<List<String>> {
    data class Params(val companyId: String, val excludedDocumentId: String)
}


fun getDocumentsInternalIdsByCompanyIdUseCase(documentRepository: DocumentRepository) =
    GetDocumentsInternalIdsByCompanyIdUseCase { params ->
        documentRepository.getDocumentsInternalIdsByCompanyId(
            params.companyId,
            params.excludedDocumentId
        )
    }
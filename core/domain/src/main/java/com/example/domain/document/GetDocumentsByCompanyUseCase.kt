package com.example.domain.document

import com.example.data.document.DocumentRepository
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow
import java.util.*

fun interface GetDocumentsByCompanyUseCase :
        (GetDocumentsByCompanyUseCase.Params) -> Flow<List<DocumentView>> {
    data class Params(val companyId: String, val fromDate: Date, val toDate: Date)
}

fun getDocumentsByCompanyUseCase(documentsRepository: DocumentRepository) =
    GetDocumentsByCompanyUseCase { params ->
        documentsRepository.getDocumentsByCompany(
            params.companyId,
            params.fromDate.time,
            params.toDate.time
        )
    }
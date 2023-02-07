package com.example.domain.document

import com.example.data.document.DocumentRepository
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow
import java.util.*

fun interface GetDocumentsByBranchUseCase :
        (GetDocumentsByBranchUseCase.Params) -> Flow<List<DocumentView>> {
    data class Params(val branchId: String, val fromDate: Date, val toDate: Date)
}

fun getDocumentsByBranchUseCase(documentsRepository: DocumentRepository) =
    GetDocumentsByBranchUseCase { params ->
        documentsRepository.getDocumentsByBranch(
            params.branchId,
            params.fromDate.time,
            params.toDate.time
        )
    }
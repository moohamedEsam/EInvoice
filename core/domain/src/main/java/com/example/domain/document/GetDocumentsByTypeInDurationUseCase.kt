package com.example.domain.document

import com.example.data.document.DocumentRepository
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow
import java.util.*

data class DaysRange(val start: Date, val end: Date)
fun interface GetDocumentsByTypeInDurationUseCase : (GetDocumentsByTypeInDurationUseCase.Params) -> Flow<List<DocumentView>> {
    data class Params(val type: Types, val id: String, val daysRange: DaysRange)

    enum class Types {
        Company, Client, Branch;

        fun getDocumentFunction(documentsRepository: DocumentRepository) = when (this) {
            Company -> documentsRepository::getDocumentsViewByCompanyInDuration
            Client -> documentsRepository::getDocumentsByClientInDuration
            Branch -> documentsRepository::getDocumentsByBranchInDuration
        }

    }
}

fun getDocumentsByTypeUseCase(documentsRepository: DocumentRepository) =
    GetDocumentsByTypeInDurationUseCase { params ->
        val documentsFunction = params.type.getDocumentFunction(documentsRepository)
        documentsFunction(params.id, params.daysRange.start.time, params.daysRange.end.time)
    }
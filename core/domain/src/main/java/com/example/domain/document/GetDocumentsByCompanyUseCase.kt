package com.example.domain.document

import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.Flow

fun interface GetDocumentsByCompanyUseCase : (String) -> Flow<List<DocumentView>>
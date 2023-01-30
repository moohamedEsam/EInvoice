package com.example.domain.document

import com.example.common.models.Result
import com.example.models.document.DocumentView

fun interface CreateDocumentUseCase : suspend (DocumentView) -> Result<DocumentView>
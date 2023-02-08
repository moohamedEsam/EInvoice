package com.example.domain.document

import com.example.common.models.Result

fun interface UndoDeleteDocumentUseCase : suspend (String) -> Result<Unit>
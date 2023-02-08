package com.example.domain.document

import com.example.common.models.Result

fun interface DeleteDocumentUseCase : suspend (String) -> Result<Unit>
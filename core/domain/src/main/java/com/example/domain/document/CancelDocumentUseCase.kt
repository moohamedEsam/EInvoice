package com.example.domain.document

import com.example.common.models.Result

fun interface CancelDocumentUseCase : suspend (String) -> Result<Unit>
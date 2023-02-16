package com.example.domain.document

import com.example.common.models.Result

fun interface SendDocumentUseCase : suspend (String) -> Result<Unit>
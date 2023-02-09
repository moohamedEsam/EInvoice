package com.example.domain.document

import com.example.common.models.Result

fun interface SyncDocumentsStatusUseCase : suspend () -> Result<Unit>
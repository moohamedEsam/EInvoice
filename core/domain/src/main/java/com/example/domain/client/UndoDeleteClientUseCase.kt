package com.example.domain.client

import com.example.common.models.Result

fun interface UndoDeleteClientUseCase : suspend (String) -> Result<Unit>
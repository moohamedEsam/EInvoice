package com.example.domain.company

import com.example.common.models.Result

fun interface UndoDeleteCompanyUseCase : suspend (String) -> Result<Unit>
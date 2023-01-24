package com.example.domain.company

import com.example.common.models.Result

fun interface DeleteCompanyUseCase : suspend (String) -> Result<Unit>
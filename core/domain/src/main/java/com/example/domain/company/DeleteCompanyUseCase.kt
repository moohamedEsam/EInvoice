package com.example.domain.company

import com.example.common.models.Result
import com.example.models.Company

fun interface DeleteCompanyUseCase : suspend (String) -> Result<Unit>
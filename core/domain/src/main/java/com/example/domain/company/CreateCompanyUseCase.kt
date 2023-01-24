package com.example.domain.company

import com.example.common.models.Result
import com.example.models.company.Company

fun interface CreateCompanyUseCase : suspend (Company) -> Result<Company>
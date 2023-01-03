package com.example.domain.company

import com.example.models.Company
import kotlinx.coroutines.flow.Flow

fun interface GetCompanyUseCase : suspend (String) -> Flow<Company>
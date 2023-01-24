package com.example.domain.company

import com.example.models.company.Company
import kotlinx.coroutines.flow.Flow

fun interface GetCompaniesUseCase : () -> Flow<List<Company>>
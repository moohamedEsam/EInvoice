package com.example.domain.company

import com.example.models.Company
import kotlinx.coroutines.flow.Flow

fun interface GetCompaniesUseCase : suspend () -> Flow<List<Company>>
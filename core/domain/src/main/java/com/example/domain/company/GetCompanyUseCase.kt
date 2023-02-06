package com.example.domain.company

import com.example.models.company.CompanyView
import kotlinx.coroutines.flow.Flow

fun interface GetCompanyUseCase : (String) -> Flow<CompanyView>
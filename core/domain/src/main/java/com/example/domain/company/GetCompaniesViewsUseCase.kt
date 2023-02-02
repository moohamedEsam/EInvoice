package com.example.domain.company

import com.example.models.company.Company
import com.example.models.company.CompanyView
import kotlinx.coroutines.flow.Flow

fun interface GetCompaniesViewsUseCase : () -> Flow<List<CompanyView>>
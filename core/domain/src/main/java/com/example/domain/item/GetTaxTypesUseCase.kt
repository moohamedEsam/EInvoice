package com.example.domain.item

import com.example.models.invoiceLine.TaxView
import kotlinx.coroutines.flow.Flow

fun interface GetTaxTypesUseCase : () -> Flow<List<TaxView>>
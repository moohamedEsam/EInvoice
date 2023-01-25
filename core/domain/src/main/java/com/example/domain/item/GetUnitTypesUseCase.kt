package com.example.domain.item

import com.example.models.item.UnitType
import kotlinx.coroutines.flow.Flow

fun interface GetUnitTypesUseCase : () -> Flow<List<UnitType>>
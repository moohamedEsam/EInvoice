package com.example.domain.item

import com.example.models.item.Item
import kotlinx.coroutines.flow.Flow

fun interface GetItemsUseCase :  () -> Flow<List<Item>>
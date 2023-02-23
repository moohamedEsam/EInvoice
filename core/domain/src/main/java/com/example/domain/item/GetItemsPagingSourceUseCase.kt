package com.example.domain.item

import androidx.paging.PagingSource
import com.example.models.item.Item
import kotlinx.coroutines.flow.Flow

fun interface GetItemsPagingSourceUseCase :  () -> PagingSource<Int, Item>
package com.example.domain.client

import androidx.paging.PagingSource
import com.example.models.Client
import kotlinx.coroutines.flow.Flow

fun interface GetClientsPagingSourceUseCase :  () -> PagingSource<Int, Client>
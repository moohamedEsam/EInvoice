package com.example.domain.branch

import androidx.paging.PagingSource
import com.example.models.branch.Branch
import kotlinx.coroutines.flow.Flow

fun interface GetBranchesPagingSourceUseCase :  () -> PagingSource<Int, Branch>
package com.example.domain.branch

import com.example.models.Branch
import kotlinx.coroutines.flow.Flow

fun interface GetBranchUseCase :  (String) -> Flow<Branch>
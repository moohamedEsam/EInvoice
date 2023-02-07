package com.example.domain.branch

import com.example.models.branch.Branch
import kotlinx.coroutines.flow.Flow

fun interface GetBranchUseCase :  (String) -> Flow<Branch>
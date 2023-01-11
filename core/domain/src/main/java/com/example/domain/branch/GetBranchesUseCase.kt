package com.example.domain.branch

import com.example.common.models.Result
import com.example.models.Branch
import kotlinx.coroutines.flow.Flow

fun interface GetBranchesUseCase :  () -> Flow<List<Branch>>
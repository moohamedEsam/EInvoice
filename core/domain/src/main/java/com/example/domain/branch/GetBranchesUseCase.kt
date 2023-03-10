package com.example.domain.branch

import com.example.models.branch.Branch
import kotlinx.coroutines.flow.Flow

fun interface GetBranchesUseCase :  () -> Flow<List<Branch>>
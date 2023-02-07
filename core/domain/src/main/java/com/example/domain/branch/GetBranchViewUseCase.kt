package com.example.domain.branch

import com.example.models.branch.Branch
import com.example.models.branch.BranchView
import kotlinx.coroutines.flow.Flow

fun interface GetBranchViewUseCase :  (String) -> Flow<BranchView>
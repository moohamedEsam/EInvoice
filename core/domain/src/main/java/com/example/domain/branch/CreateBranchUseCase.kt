package com.example.domain.branch

import com.example.common.models.Result
import com.example.models.branch.Branch

fun interface CreateBranchUseCase : suspend (Branch) -> Result<Branch>
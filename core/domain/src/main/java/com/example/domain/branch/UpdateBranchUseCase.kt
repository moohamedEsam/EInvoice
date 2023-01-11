package com.example.domain.branch

import com.example.common.models.Result
import com.example.models.Branch

fun interface UpdateBranchUseCase : suspend (Branch) -> Result<Branch>
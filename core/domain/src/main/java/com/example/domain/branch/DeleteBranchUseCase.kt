package com.example.domain.branch

import com.example.common.models.Result
import com.example.models.Branch

fun interface DeleteBranchUseCase : suspend (String) -> Result<Unit>
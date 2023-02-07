package com.example.domain.branch

import com.example.common.models.Result
import com.example.models.branch.Branch

fun interface UndoDeleteBranchUseCase : suspend (String) -> Result<Unit>
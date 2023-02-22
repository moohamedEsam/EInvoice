package com.example.data.branch

import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.branch.Branch
import com.example.models.branch.BranchView
import kotlinx.coroutines.flow.Flow

interface BranchRepository : Syncable<Branch> {
    fun getBranches(): Flow<List<Branch>>


    fun getBranchView(id: String): Flow<BranchView>

    suspend fun createBranch(branch: Branch): Result<Branch>

    suspend fun updateBranch(branch: Branch): Result<Branch>

    suspend fun deleteBranch(id: String): Result<Unit>

    suspend fun undoDeleteBranch(id: String): Result<Unit>

    fun getBranchesByCompanyId(companyId: String): Flow<List<Branch>>
}
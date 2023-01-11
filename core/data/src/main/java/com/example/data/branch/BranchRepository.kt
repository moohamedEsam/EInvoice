package com.example.data.branch

import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.Branch
import kotlinx.coroutines.flow.Flow

interface BranchRepository : Syncable<Branch> {
    fun getBranches(): Flow<List<Branch>>

    fun getBranch(id: String): Flow<Branch>

    suspend fun createBranch(branch: Branch): com.example.common.models.Result<Branch>

    suspend fun updateBranch(branch: Branch): Result<Branch>

    suspend fun deleteBranch(id: String): com.example.common.models.Result<Unit>

    fun getBranchesByCompanyId(companyId: String): Flow<List<Branch>>
}
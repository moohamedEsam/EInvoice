package com.example.data.branch

import com.example.common.functions.tryWrapper
import com.example.data.sync.Synchronizer
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.common.models.Result
import com.example.data.sync.handleSync
import com.example.database.models.branch.*
import com.example.database.room.dao.BranchDao
import com.example.models.branch.Branch
import com.example.models.branch.BranchView
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

private const val BRANCH_NOT_FOUND = "Branch not found"


class OfflineFirstBranchRepository(
    private val localSource: BranchDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : BranchRepository {
    override fun getBranches(): Flow<List<Branch>> =
        localSource.getBranches().map { branches -> branches.map { it.asBranch() } }

    override fun getBranch(id: String): Flow<Branch> =
        localSource.getBranchById(id).filterNotNull().map(BranchEntity::asBranch)

    override fun getBranchView(id: String): Flow<BranchView> =
        localSource.getBranchViewById(id).filterNotNull().map(BranchViewEntity::asBranchView)

    override suspend fun createBranch(branch: Branch): Result<Branch> = tryWrapper {
        localSource.insertBranch(branch.asBranchEntity(isCreated = true))
        Result.Success(branch)
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> = tryWrapper {
        val branchEntity = localSource.getBranchById(branch.id).first() ?: return@tryWrapper Result.Error(
            BRANCH_NOT_FOUND
        )
        if (branchEntity.isCreated)
            localSource.updateBranch(branch.asBranchEntity(isCreated = true))
        else
            localSource.updateBranch(branch.asBranchEntity(isUpdated = true))
        Result.Success(branch)
    }

    override suspend fun deleteBranch(id: String): Result<Unit> = tryWrapper {
        val branchEntity = localSource.getBranchById(id).first() ?: return@tryWrapper Result.Error(BRANCH_NOT_FOUND)
        if (branchEntity.isCreated)
            localSource.deleteBranch(id)
        else
            localSource.updateBranch(branchEntity.copy(isDeleted = true))
        Result.Success(Unit)
    }

    override fun getBranchesByCompanyId(companyId: String): Flow<List<Branch>> =
        localSource.getBranchesByCompanyId(companyId)
            .map { branches -> branches.map { it.asBranch() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val branches = localSource.getBranches().first()
        val idMappings = HashMap<String, String>()
        val syncResult = synchronizer.handleSync(
            remoteCreator = {
                val createdBranches = branches.filter { it.isCreated }
                createdBranches.forEach { branch ->
                    val result = remoteSource.createBranch(branch.asBranch())
                    if (result is Result.Success) {
                        idMappings[branch.id] = result.data.id
                    }
                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val deletedBranches = branches.filter { it.isDeleted }
                deletedBranches.forEach { branch ->
                    val result = remoteSource.deleteBranch(branch.id)
                    if (result is Result.Success) localSource.deleteBranch(branch.id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedBranches = branches.filter { it.isUpdated }
                updatedBranches.forEach { branch ->
                    val result = remoteSource.updateBranch(branch.asBranch())
                    if (result is Result.Success) {
                        localSource.updateBranch(branch.copy(isUpdated = false))
                    }
                }
                Result.Success(Unit)
            },

            localCreator = { branch ->
                val ids = branches.map { it.id }
                if (branch.id in ids)
                    localSource.updateBranch(branch)
                else
                    localSource.insertBranch(branch)

            },
            afterLocalCreate = {
                idMappings.forEach { (old, new) ->
                    localSource.updateItemsBranchId(old, new)
                    localSource.updateDocumentsBranchId(old, new)
                    localSource.deleteBranch(old)
                }
            },
            remoteFetcher = {
                remoteSource.getBranches()
                    .map { branches -> branches.map { it.asBranchEntity() } }
            },
        )
        return syncResult
    }

    override suspend fun undoDeleteBranch(id: String): Result<Unit> = tryWrapper {
        localSource.markBranchAsNotDeleted(id)
        Result.Success(Unit)
    }

}
package com.example.data.branch

import androidx.paging.PagingSource
import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.branch.*
import com.example.database.room.dao.BranchDao
import com.example.models.branch.Branch
import com.example.models.branch.BranchView
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

private const val BRANCH_NOT_FOUND = "Branch not found"


class OfflineFirstBranchRepository(
    private val localSource: BranchDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : BranchRepository {
    override fun getBranches(): Flow<List<Branch>> =
        localSource.getBranches().map { branches -> branches.map { it.asBranch() } }

    override fun getBranchesPagingSource(): PagingSource<Int, Branch> =
        localSource.getPagedBranches().map { it.asBranch() }.asPagingSourceFactory().invoke()


    override fun getBranchView(id: String): Flow<BranchView> =
        localSource.getBranchViewById(id).filterNotNull().map(BranchViewEntity::removeDeleted)
            .map(BranchViewEntity::asBranchView)

    override suspend fun createBranch(branch: Branch): Result<Branch> = tryWrapper {
        localSource.insertBranch(branch.asBranchEntity(isCreated = true))
        Result.Success(branch)
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> = tryWrapper {
        val branchEntity = localSource.getBranchById(branch.id) ?: return@tryWrapper Result.Error(
            BRANCH_NOT_FOUND
        )
        if (branchEntity.isCreated)
            localSource.updateBranch(branch.asBranchEntity(isCreated = true))
        else
            localSource.updateBranch(branch.asBranchEntity(isUpdated = true))
        Result.Success(branch)
    }

    override suspend fun deleteBranch(id: String): Result<Unit> = tryWrapper {
        val branchEntity =
            localSource.getBranchById(id) ?: return@tryWrapper Result.Error(BRANCH_NOT_FOUND)
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
        val branches = localSource.getAllBranches()
        var idMappings = emptyMap<String, String?>()
        val remotelyCreatedBranches = mutableListOf<String>()
        val syncResult = synchronizer.handleSync(
            remoteCreator = { idMappings = createAndGetIdMappings(branches) },
            remoteDeleter = { deleteBranchesRemotelyAndLocal(branches) },
            remoteUpdater = { updateBranchesRemotelyAndLocal(branches) },

            localCreator = { branch ->
                remotelyCreatedBranches.add(branch.id)
                val ids = branches.map { it.id }
                if (branch.id in ids)
                    localSource.updateBranch(branch)
                else
                    localSource.insertBranch(branch)

            },
            afterLocalCreate = {
                idMappings.forEach { (old, new) ->
                    if (new == null) return@forEach
                    updateRelationKeys(old, new)
                    localSource.deleteBranch(old)
                }
            },
            remoteFetcher = {
                remoteSource.getBranches()
                    .map { branches -> branches.map { it.asBranchEntity().copy(isSynced = true) } }
            },
        )
        val remotelyDeletedBranches = branches.filterNot { it.id in remotelyCreatedBranches }
        remotelyDeletedBranches.forEach { branch ->
            localSource.deleteBranch(branch.id)
        }
        return syncResult
    }

    private suspend fun updateRelationKeys(old: String, new: String) {
        localSource.updateItemsBranchId(old, new)
        localSource.updateDocumentsBranchId(old, new)
    }

    private suspend fun updateBranchesRemotelyAndLocal(branches: List<BranchEntity>) {
        branches.filter { it.isUpdated }
            .forEach { branch ->
                val result = remoteSource.updateBranch(branch.asBranch())
                if (result is Result.Success) {
                    localSource.updateBranch(result.data.asBranchEntity())
                }
            }
    }

    private suspend fun deleteBranchesRemotelyAndLocal(branches: List<BranchEntity>) {
        branches.filter { it.isDeleted }
            .forEach { branch ->
                val result = remoteSource.deleteBranch(branch.id)
                if (result is Result.Success) localSource.deleteBranch(branch.id)
            }
    }

    private suspend fun createAndGetIdMappings(branches: List<BranchEntity>) =
        branches.filter { it.isCreated }
            .associateBy { branchEntity ->
                val result = remoteSource.createBranch(branchEntity.asBranch())
                handleCreateResult(result, branchEntity)
            }.map { (newId, branch) ->
                branch.id to newId
            }.toMap()

    private suspend fun handleCreateResult(
        result: Result<Branch>,
        branchEntity: BranchEntity
    ) = if (result is Result.Success)
        result.data.id
    else
        null.also { handleCreateError(result, branchEntity) }

    private suspend fun handleCreateError(
        result: Result<Branch>,
        it: BranchEntity
    ) {
        val error = (result as? Result.Error)?.exception
        localSource.updateBranch(it.copy(isSynced = false, syncError = error))
    }


    override suspend fun undoDeleteBranch(id: String): Result<Unit> = tryWrapper {
        localSource.markBranchAsNotDeleted(id)
        Result.Success(Unit)
    }

}

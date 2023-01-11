package com.example.data.branch

import com.example.common.functions.tryWrapper
import com.example.data.sync.Synchronizer
import com.example.database.models.asBranch
import com.example.database.models.asBranchEntity
import com.example.database.room.EInvoiceDao
import com.example.models.Branch
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.common.models.Result
import com.example.data.sync.handleSync
import kotlinx.coroutines.flow.first

class OfflineFirstBranchRepository(
    private val localSource: EInvoiceDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : BranchRepository {
    override fun getBranches(): Flow<List<Branch>> =
        localSource.getBranches().map { branches -> branches.map { it.asBranch() } }

    override fun getBranch(id: String): Flow<Branch> =
        localSource.getBranchById(id).map { it.asBranch() }

    override suspend fun createBranch(branch: Branch): Result<Branch> = tryWrapper {
        localSource.insertBranch(branch.asBranchEntity(isCreated = true))
        Result.Success(branch)
    }

    override suspend fun updateBranch(branch: Branch): Result<Branch> = tryWrapper {
        val branchEntity = localSource.getBranchById(branch.id).first()
        if (branchEntity.isCreated)
            localSource.updateBranch(branch.asBranchEntity(isCreated = true))
        else
            localSource.updateBranch(branch.asBranchEntity(isUpdated = true))
        Result.Success(branch)
    }

    override suspend fun deleteBranch(id: String): Result<Unit> = tryWrapper {
        val branchEntity = localSource.getBranchById(id).first()
        if (branchEntity.isCreated)
            localSource.deleteBranch(id)
        else
            localSource.updateBranch(branchEntity.copy(isDeleted = true))
        Result.Success(Unit)
    }

    override fun getBranchesByCompanyId(companyId: String): Flow<List<Branch>> =
        localSource.getBranchesByCompanyId(companyId)
            .map { branches -> branches.map { it.asBranch() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.handleSync(
            remoteCreator = {
                val branches = localSource.getCreatedBranches()
                branches.forEach { branch ->
                    remoteSource.createBranch(branch.asBranch())
                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val branches = localSource.getDeletedBranches()
                branches.forEach { branch ->
                    remoteSource.deleteBranch(branch.id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val branches = localSource.getUpdatedBranches()
                branches.forEach { branch ->
                    remoteSource.updateBranch(branch.asBranch())
                }
                Result.Success(Unit)
            },

            localCreator = localSource::insertBranch,
            beforeLocalCreate = { localSource.deleteAllBranches() },
            remoteFetcher = {
                remoteSource.getBranches().map { branches -> branches.map { it.asBranchEntity() } }
            },
        )

}
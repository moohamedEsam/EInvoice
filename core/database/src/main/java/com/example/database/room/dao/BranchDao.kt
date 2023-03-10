package com.example.database.room.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.database.models.branch.BranchEntity
import com.example.database.models.branch.BranchViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Query("SELECT * FROM Branch where isDeleted = 0")
    fun getBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch where isDeleted = 0")
    fun getPagedBranches(): DataSource.Factory<Int, BranchEntity>

    @Query("SELECT * FROM Branch")
    suspend fun getAllBranches(): List<BranchEntity>

    @Query("SELECT * FROM Branch WHERE companyId = :companyId and isDeleted = 0")
    fun getBranchesByCompanyId(companyId: String): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE id = :id")
    suspend fun getBranchById(id: String): BranchEntity?

    @Transaction
    @Query("select * from branch where id = :id")
    fun getBranchViewById(id: String): Flow<BranchViewEntity?>

    @Query("update Item set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateItemsBranchId(oldBranchId: String, newBranchId: String)

    @Query("update document set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateDocumentsBranchId(oldBranchId: String, newBranchId: String)

    @Insert
    suspend fun insertBranch(branch: BranchEntity)

    @Query("update branch set isDeleted = 1 where id = :id")
    suspend fun markBranchAsDeleted(id: String)

    @Query("update branch set isDeleted = 0 where id = :id")
    suspend fun markBranchAsNotDeleted(id: String)

    @Query("DELETE FROM Branch where id = :id")
    suspend fun deleteBranch(id: String)

    @Query("delete from branch")
    suspend fun deleteAllBranches()

    @Update
    suspend fun updateBranch(branch: BranchEntity)
}
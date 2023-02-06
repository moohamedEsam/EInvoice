package com.example.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.BranchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {
    @Query("SELECT * FROM Branch")
    fun getBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE companyId = :companyId")
    fun getBranchesByCompanyId(companyId: String): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE id = :id")
    fun getBranchById(id: String): Flow<BranchEntity>

    @Query("update Item set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateItemsBranchId(oldBranchId: String, newBranchId: String)

    @Query("update document set branchId =:newBranchId where branchId = :oldBranchId")
    suspend fun updateDocumentsBranchId(oldBranchId: String, newBranchId: String)

    @Insert
    suspend fun insertBranch(branch: BranchEntity)

    @Query("DELETE FROM Branch where id = :id")
    suspend fun deleteBranch(id: String)

    @Query("delete from branch")
    suspend fun deleteAllBranches()

    @Update
    suspend fun updateBranch(branch: BranchEntity)
}
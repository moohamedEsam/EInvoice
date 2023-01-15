package com.example.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.BranchEntity
import com.example.database.models.ClientEntity
import com.example.database.models.CompanyEntity
import com.example.database.models.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EInvoiceDao {
    // Branch
    @Query("SELECT * FROM Branch")
    fun getBranches(): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE companyId = :companyId")
    fun getBranchesByCompanyId(companyId: String): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE id = :id")
    fun getBranchById(id: String): Flow<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 0 and isCreated = 1")
    suspend fun getCreatedBranches(): List<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 0 and isUpdated = 1")
    suspend fun getUpdatedBranches(): List<BranchEntity>

    @Query("SELECT * FROM Branch WHERE isDeleted = 1")
    suspend fun getDeletedBranches(): List<BranchEntity>

    @Insert
    suspend fun insertBranch(branch: BranchEntity)

    @Query("DELETE FROM Branch where id = :id")
    suspend fun deleteBranch(id: String)

    @Update
    suspend fun updateBranch(branch: BranchEntity)

    // Company
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getCompanies(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM Company WHERE id = :id and isDeleted = 0")
    fun getCompanyById(id: String): Flow<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isUpdated = 1 and isDeleted = 0")
    suspend fun getUpdatedCompanies(): List<CompanyEntity>

    @Query("delete from branch")
    suspend fun deleteAllBranches()

    @Query("SELECT * FROM Company WHERE isCreated = 1 and isDeleted = 0")
    suspend fun getCreatedCompanies(): List<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isDeleted = 1")
    suspend fun getDeletedCompanies(): List<CompanyEntity>

    @Insert
    suspend fun insertCompany(company: CompanyEntity)

    @Query("DELETE FROM Company where id = :id")
    suspend fun deleteCompany(id: String)

    @Query("delete from company")
    suspend fun deleteAllCompanies()

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Query("update Company set isDeleted = 1 where id = :id")
    suspend fun markCompanyAsDeleted(id: String)

    @Query("update Company set isDeleted = 0 where id = :id")
    suspend fun undoDeleteCompany(id: String)


    // Client
    @Query("SELECT * FROM Client")
    fun getClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE companyId = :companyId")
    fun getClientsByCompanyId(companyId: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE id = :id")
    fun getClientById(id: String): Flow<ClientEntity>

    @Insert
    suspend fun insertClient(client: ClientEntity)

    @Query("DELETE FROM Client where id = :id")
    suspend fun deleteClient(id: String)

    @Update
    suspend fun updateClient(client: ClientEntity)

    // Item
    @Query("SELECT * FROM Item")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE branchId = :branchId")
    fun getItemsByBranchId(branchId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE id = :id")
    fun getItemById(id: String): Flow<ItemEntity>

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Query("DELETE FROM Item where id = :id")
    suspend fun deleteItem(id: String)

    @Update
    suspend fun updateItem(item: ItemEntity)


}
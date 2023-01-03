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
    fun getBranchesByCompanyId(companyId: Int): Flow<List<BranchEntity>>

    @Query("SELECT * FROM Branch WHERE id = :id")
    fun getBranchById(id: Int): Flow<BranchEntity>

    @Insert
    suspend fun insertBranch(branch: BranchEntity)

    @Query("DELETE FROM Branch where id = :id")
    suspend fun deleteBranch(id: Int)

    @Update
    suspend fun updateBranch(branch: BranchEntity)

    // Company
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getCompanies(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM Company WHERE id = :id and isDeleted = 0")
    fun getCompanyById(id: Int): Flow<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isUpdated = 1")
    suspend fun getUpdatedCompanies(): List<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isCreated = 1")
    suspend fun getCreatedCompanies(): List<CompanyEntity>

    @Query("SELECT * FROM Company WHERE isDeleted = 1")
    suspend fun getDeletedCompanies(): List<CompanyEntity>

    @Insert
    suspend fun insertCompany(company: CompanyEntity)

    @Query("DELETE FROM Company where id = :id")
    suspend fun deleteCompany(id: Int)

    @Update
    suspend fun updateCompany(company: CompanyEntity)


    // Client
    @Query("SELECT * FROM Client")
    fun getClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE companyId = :companyId")
    fun getClientsByCompanyId(companyId: Int): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE id = :id")
    fun getClientById(id: Int): Flow<ClientEntity>

    @Insert
    suspend fun insertClient(client: ClientEntity)

    @Query("DELETE FROM Client where id = :id")
    suspend fun deleteClient(id: Int)

    @Update
    suspend fun updateClient(client: ClientEntity)

    // Item
    @Query("SELECT * FROM Item")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE branchId = :branchId")
    fun getItemsByBranchId(branchId: Int): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE id = :id")
    fun getItemById(id: Int): Flow<ItemEntity>

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Query("DELETE FROM Item where id = :id")
    suspend fun deleteItem(id: Int)

    @Update
    suspend fun updateItem(item: ItemEntity)


}
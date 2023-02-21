package com.example.database.room.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.database.models.company.CompanyEntity
import com.example.database.models.company.CompanyViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getCompanies(): Flow<List<CompanyEntity>>

    @Transaction
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getPagedCompanies(): DataSource.Factory<Int, CompanyViewEntity>

    @Query("SELECT * FROM Company")
    fun getAllCompanies(): Flow<List<CompanyEntity>>

    @Transaction
    @Query("SELECT * FROM Company where isDeleted = 0")
    fun getCompaniesViews(): Flow<List<CompanyViewEntity>>


    @Query("SELECT * FROM Company WHERE id = :id and isDeleted = 0")
    fun getCompanyById(id: String): Flow<CompanyEntity>

    @Transaction
    @Query("SELECT * FROM Company WHERE id = :id and isDeleted = 0")
    fun getCompanyViewById(id: String): Flow<CompanyViewEntity?>


    @Insert
    suspend fun insertCompany(company: CompanyEntity)

    @Query("DELETE FROM Company where id = :id")
    suspend fun deleteCompany(id: String)

    @Query("update Branch set companyId =:newCompanyId where companyId = :oldCompanyId")
    suspend fun updateBranchesCompanyId(oldCompanyId: String, newCompanyId: String)

    @Query("update Client set companyId =:newCompanyId where companyId = :oldCompanyId")
    suspend fun updateClientsCompanyId(oldCompanyId: String, newCompanyId: String)

    @Query("update document set issuerId =:newIssuerId where issuerId = :oldIssuerId")
    suspend fun updateDocumentsIssuerId(oldIssuerId: String, newIssuerId: String)

    @Query("delete from company")
    suspend fun deleteAllCompanies()

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Query("update Company set isDeleted = 1 where id = :id")
    suspend fun markCompanyAsDeleted(id: String)

    @Query("update Company set isDeleted = 0 where id = :id")
    suspend fun undoDeleteCompany(id: String)

    @Query("delete from UnitType")
    suspend fun deleteAllUnitTypes()

    @Query("delete from Tax")
    suspend fun deleteAllTaxes()


    @Transaction
    suspend fun clearData() {
        deleteAllCompanies()
        deleteAllTaxes()
        deleteAllUnitTypes()
    }

}
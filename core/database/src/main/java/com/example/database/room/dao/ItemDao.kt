package com.example.database.room.dao

import androidx.room.*
import com.example.database.models.ItemEntity
import com.example.database.models.UnitTypeEntity
import com.example.database.models.invoiceLine.tax.SubTaxEntity
import com.example.database.models.invoiceLine.tax.TaxEntity
import com.example.database.models.invoiceLine.tax.TaxViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item where isDeleted = 0")
    fun getItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item")
    suspend fun getAllItems() : List<ItemEntity>

    @Query("SELECT * FROM Item WHERE branchId = :branchId and isDeleted = 0")
    fun getItemsByBranchId(branchId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Item WHERE id = :id and isDeleted = 0")
    suspend fun getItemById(id: String): ItemEntity?

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Query("DELETE FROM Item where id = :id")
    suspend fun deleteItem(id: String)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Query("update InvoiceLine set itemId =:newId where itemId = :oldId")
    suspend fun updateInvoiceLinesItemId(oldId: String, newId: String)

    @Query("delete from item")
    suspend fun deleteAllItems()

    @Insert
    suspend fun insertUnitType(unitType: UnitTypeEntity)

    @Query("SELECT * FROM UnitType")
    fun getUnitTypes(): Flow<List<UnitTypeEntity>>

    @Insert
    suspend fun insertTax(tax: TaxEntity, subTaxes: List<SubTaxEntity>)


    @Query("SELECT * FROM Tax")
    @Transaction
    fun getTaxTypes(): Flow<List<TaxViewEntity>>

    @Query("delete FROM UnitType")
    suspend fun deleteAllUnitTypes()

    @Query("delete FROM Tax")
    suspend fun deleteAllTaxTypes()

}
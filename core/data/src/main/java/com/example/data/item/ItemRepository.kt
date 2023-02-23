package com.example.data.item

import androidx.paging.PagingSource
import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.invoiceLine.TaxView
import com.example.models.item.Item
import com.example.models.item.UnitType
import kotlinx.coroutines.flow.Flow

interface ItemRepository : Syncable<Item> {
    fun getItems(): Flow<List<Item>>

    fun getItemPagingSource(): PagingSource<Int, Item>

    fun getUnitTypes(): Flow<List<UnitType>>

    fun getTaxTypes(): Flow<List<TaxView>>

    suspend fun createItem(item: Item): Result<Item>

    suspend fun updateItem(item: Item): Result<Item>

    suspend fun deleteItem(id: String): Result<Unit>

    fun getItemsByBranchId(branchId: String): Flow<List<Item>>

}
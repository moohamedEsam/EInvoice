package com.example.data.item

import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.item.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository : Syncable<Item> {
    fun getItems(): Flow<List<Item>>

    fun getItem(id: String): Flow<Item>

    suspend fun createItem(item: Item): Result<Item>

    suspend fun updateItem(item: Item): Result<Item>

    suspend fun deleteItem(id: String): Result<Unit>

    fun getItemsByBranchId(branchId: String): Flow<List<Item>>

}
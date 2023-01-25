package com.example.data.item

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.asItem
import com.example.database.models.asItemEntity
import com.example.database.models.asUnitType
import com.example.database.models.asUnitTypeEntity
import com.example.database.room.EInvoiceDao
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineFirstItemRepository(
    private val localSource: EInvoiceDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : ItemRepository {
    override fun getItems(): Flow<List<Item>> =
        localSource.getItems().map { items -> items.map { it.asItem() } }

    override fun getItem(id: String): Flow<Item> =
        localSource.getItemById(id).map { it.asItem() }

    override suspend fun createItem(item: Item): Result<Item> = tryWrapper {
        localSource.insertItem(item.asItemEntity(isCreated = true))
        Result.Success(item)
    }

    override suspend fun updateItem(item: Item): Result<Item> = tryWrapper {
        val itemEntity = localSource.getItemById(item.id).first()
        if (itemEntity.isCreated)
            localSource.updateItem(item.asItemEntity(isCreated = true))
        else
            localSource.updateItem(item.asItemEntity(isUpdated = true))
        Result.Success(item)
    }

    override suspend fun deleteItem(id: String): Result<Unit> = tryWrapper {
        val itemEntity = localSource.getItemById(id).first()
        if (itemEntity.isCreated)
            localSource.deleteItem(id)
        else
            localSource.updateItem(itemEntity.copy(isDeleted = true))
        Result.Success(Unit)
    }

    override fun getItemsByBranchId(branchId: String): Flow<List<Item>> =
        localSource.getItemsByBranchId(branchId)
            .map { items -> items.map { it.asItem() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return withContext(synchronizer.dispatcher) {
            awaitAll(
                async { syncItems(synchronizer) },
                async { syncUnitTypes(synchronizer) }
            ).all { it }
        }
    }

    private suspend fun syncUnitTypes(synchronizer: Synchronizer) =
        synchronizer.handleSync(
            remoteFetcher = remoteSource::getUnitTypes,
            beforeLocalCreate = localSource::deleteAllUnitTypes,
            localCreator = {
                localSource.insertUnitType(it.asUnitTypeEntity())
                Result.Success(Unit)
            },
            remoteCreator = { Result.Success(Unit) },
            remoteUpdater = { Result.Success(Unit) },
            remoteDeleter = { Result.Success(Unit) }
        )

    private suspend fun syncItems(synchronizer: Synchronizer) =
        synchronizer.handleSync(
            remoteCreator = {
                val items = localSource.getCreatedItems()
                items.forEach { item ->
                    remoteSource.createItem(item.asItem())
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val items = localSource.getUpdatedItems()
                items.forEach { item ->
                    remoteSource.updateItem(item.asItem())
                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val items = localSource.getDeletedItems()
                items.forEach { item ->
                    remoteSource.deleteItem(item.id)
                }
                Result.Success(Unit)
            },
            localCreator = {
                localSource.insertItem(it.asItemEntity())
                Result.Success(Unit)
            },

            beforeLocalCreate = localSource::deleteAllItems,
            remoteFetcher = remoteSource::getItems
        )

    override fun getUnitTypes(): Flow<List<UnitType>> =
        localSource.getUnitTypes().map { unitTypes -> unitTypes.map { it.asUnitType() } }
}
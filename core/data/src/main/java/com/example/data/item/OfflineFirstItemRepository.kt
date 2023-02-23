package com.example.data.item

import androidx.paging.PagingSource
import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.data.sync.Synchronizer
import com.example.data.sync.handleSync
import com.example.database.models.*
import com.example.database.models.invoiceLine.tax.asSubTaxEntity
import com.example.database.models.invoiceLine.tax.asTaxEntity
import com.example.database.models.invoiceLine.tax.asTaxView
import com.example.database.room.dao.ItemDao
import com.example.models.invoiceLine.TaxView
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val ITEM_NOT_FOUND = "Item Not Found"

class OfflineFirstItemRepository(
    private val localSource: ItemDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : ItemRepository {
    override fun getItems(): Flow<List<Item>> =
        localSource.getItems().map { items -> items.map { it.asItem() } }

    override suspend fun createItem(item: Item): Result<Item> = tryWrapper {
        localSource.insertItem(item.asItemEntity(isCreated = true))
        Result.Success(item)
    }

    override suspend fun updateItem(item: Item): Result<Item> = tryWrapper {
        val itemEntity =
            localSource.getItemById(item.id) ?: return@tryWrapper Result.Error(ITEM_NOT_FOUND)
        if (itemEntity.isCreated)
            localSource.updateItem(item.asItemEntity(isCreated = true))
        else
            localSource.updateItem(item.asItemEntity(isUpdated = true))
        Result.Success(item)
    }

    override fun getItemPagingSource(): PagingSource<Int, Item> =
        localSource.getPagedItems().map(ItemEntity::asItem).asPagingSourceFactory().invoke()

    override suspend fun deleteItem(id: String): Result<Unit> = tryWrapper {
        val itemEntity =
            localSource.getItemById(id) ?: return@tryWrapper Result.Error(ITEM_NOT_FOUND)
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
                async { syncUnitTypes(synchronizer) },
                async { syncTaxTypes(synchronizer) }
            ).all { it }
        }
    }

    private suspend fun syncUnitTypes(synchronizer: Synchronizer) =
        synchronizer.handleSync(
            remoteFetcher = remoteSource::getUnitTypes,
            afterLocalCreate = { },
            localCreator = {
                localSource.insertUnitType(it.asUnitTypeEntity())
                Result.Success(Unit)
            },
            remoteCreator = { Result.Success(Unit) },
            remoteUpdater = { Result.Success(Unit) },
            remoteDeleter = { Result.Success(Unit) }
        )

    private suspend fun syncTaxTypes(synchronizer: Synchronizer) =
        synchronizer.handleSync(
            remoteFetcher = remoteSource::getTaxTypes,
            afterLocalCreate = { },
            localCreator = {
                val taxEntity = it.asTaxEntity()
                localSource.insertTax(
                    taxEntity,
                    it.subTaxes.map { subTax -> subTax.asSubTaxEntity(taxId = taxEntity.id) })
                Result.Success(Unit)
            },
            remoteCreator = { Result.Success(Unit) },
            remoteUpdater = { Result.Success(Unit) },
            remoteDeleter = { Result.Success(Unit) }
        )

    private suspend fun syncItems(synchronizer: Synchronizer): Boolean {
        val items = localSource.getAllItems()
        val idMappings = HashMap<String, String>()
        val remotelyCreatedItems = mutableListOf<String>()
        val result = synchronizer.handleSync(
            remoteCreator = {
                val createdItems = items.filter { it.isCreated }
                createdItems.forEach { item ->
                    val result = remoteSource.createItem(item.asItem())
                    if (result is Result.Success) {
                        idMappings[item.id] = result.data.id
                    }
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedItems = items.filter { it.isUpdated }
                updatedItems.forEach { item ->
                    val result = remoteSource.updateItem(item.asItem())
                    if (result is Result.Success) {
                        localSource.updateItem(item.copy(isUpdated = false))
                    }
                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val deletedItems = items.filter { it.isDeleted }
                deletedItems.forEach { item ->
                    val result = remoteSource.deleteItem(item.id)
                    if (result is Result.Success) {
                        localSource.deleteItem(item.id)
                    }
                }
                Result.Success(Unit)
            },
            localCreator = { item ->
                remotelyCreatedItems.add(item.id)
                val ids = items.map { it.id }
                if (item.id in ids)
                    localSource.updateItem(item.asItemEntity())
                else
                    localSource.insertItem(item.asItemEntity())
                Result.Success(Unit)
            },

            afterLocalCreate = {
                idMappings.forEach { (oldId, newId) ->
                    localSource.updateInvoiceLinesItemId(oldId, newId)
                    localSource.deleteItem(oldId)
                }
            },
            remoteFetcher = remoteSource::getItems
        )
        val remotelyDeletedItems = items.filterNot { it.id in remotelyCreatedItems }
        remotelyDeletedItems.forEach { item ->
            localSource.deleteItem(item.id)
        }
        return result
    }

    override fun getUnitTypes(): Flow<List<UnitType>> =
        localSource.getUnitTypes().map { unitTypes -> unitTypes.map { it.asUnitType() } }

    override fun getTaxTypes(): Flow<List<TaxView>> =
        localSource.getTaxTypes().map { taxTypes -> taxTypes.map { it.asTaxView() } }
}
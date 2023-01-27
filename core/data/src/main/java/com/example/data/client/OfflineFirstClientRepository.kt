package com.example.data.client

import com.example.common.functions.tryWrapper
import com.example.data.sync.Synchronizer
import com.example.database.models.asClient
import com.example.database.models.asClientEntity
import com.example.database.room.EInvoiceDao
import com.example.models.Client
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.common.models.Result
import com.example.data.sync.handleSync
import kotlinx.coroutines.flow.first

class OfflineFirstClientRepository(
    private val localSource: EInvoiceDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : ClientRepository {
    override fun getClients(): Flow<List<Client>> =
        localSource.getClients().map { clients -> clients.map { it.asClient() } }

    override fun getClient(id: String): Flow<Client> =
        localSource.getClientById(id).map { it.asClient() }

    override suspend fun createClient(client: Client): Result<Client> = tryWrapper {
        localSource.insertClient(client.asClientEntity(isCreated = true))
        Result.Success(client)
    }

    override suspend fun updateClient(client: Client): Result<Client> = tryWrapper {
        val clientEntity = localSource.getClientById(client.id).first()
        if (clientEntity.isCreated)
            localSource.updateClient(client.asClientEntity(isCreated = true))
        else
            localSource.updateClient(client.asClientEntity(isUpdated = true))
        Result.Success(client)
    }

    override suspend fun deleteClient(id: String): Result<Unit> = tryWrapper {
        val clientEntity = localSource.getClientById(id).first()
        if (clientEntity.isCreated)
            localSource.deleteClient(id)
        else
            localSource.updateClient(clientEntity.copy(isDeleted = true))
        Result.Success(Unit)
    }

    override fun getClientsByCompanyId(companyId: String): Flow<List<Client>> =
        localSource.getClientsByCompanyId(companyId)
            .map { clients -> clients.map { it.asClient() } }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        val clients = localSource.getClients().first()
        val idMappings = HashMap<String, String>()
        return synchronizer.handleSync(
            remoteCreator = {
                val createdClients = clients.filter { it.isCreated }
                createdClients.forEach { client ->
                    val result = remoteSource.createClient(client.asClient())
                    if (result is Result.Success)
                        idMappings[client.id] = result.data.id

                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val deletedClients = clients.filter { it.isDeleted }
                deletedClients.forEach { client ->
                    val result = remoteSource.deleteClient(client.id)
                    if (result is Result.Success)
                        localSource.deleteClient(client.id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val updatedClients = clients.filter { it.isUpdated }
                updatedClients.forEach { client ->
                    val result = remoteSource.updateClient(client.asClient())
                    if (result is Result.Success)
                        localSource.updateClient(client.copy(isUpdated = false))
                }
                Result.Success(Unit)
            },

            localCreator = { client ->
                val ids = clients.map { it.id }
                if (client.id in ids) return@handleSync
                localSource.insertClient(client)
            },
            afterLocalCreate = {
                idMappings.forEach { (old, new) ->
                    localSource.updateDocumentsReceiverId(old, new)
                    localSource.deleteClient(old)
                }
            },
            remoteFetcher = {
                remoteSource.getClients().map { clients -> clients.map { it.asClientEntity() } }
            },
        )
    }

}
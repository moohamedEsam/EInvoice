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

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.handleSync(
            remoteCreator = {
                val clients = localSource.getCreatedClients()
                clients.forEach { client ->
                    remoteSource.createClient(client.asClient())
                }
                Result.Success(Unit)
            },
            remoteDeleter = {
                val clients = localSource.getDeletedClients()
                clients.forEach { client ->
                    remoteSource.deleteClient(client.id)
                }
                Result.Success(Unit)
            },
            remoteUpdater = {
                val clients = localSource.getUpdatedClients()
                clients.forEach { client ->
                    remoteSource.updateClient(client.asClient())
                }
                Result.Success(Unit)
            },

            localCreator = localSource::insertClient,
            beforeLocalCreate = { localSource.deleteAllClients() },
            remoteFetcher = {
                remoteSource.getClients().map { clients -> clients.map { it.asClientEntity() } }
            },
        )

}
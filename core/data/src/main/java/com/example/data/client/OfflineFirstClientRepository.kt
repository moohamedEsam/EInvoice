package com.example.data.client

import androidx.paging.PagingSource
import com.example.common.functions.tryWrapper
import com.example.data.sync.Synchronizer
import com.example.models.Client
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.common.models.Result
import com.example.data.sync.handleSync
import com.example.database.models.client.*
import com.example.database.room.dao.ClientDao
import com.example.models.ClientView
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

private const val CLIENT_NOT_FOUND = "Client not found"

class OfflineFirstClientRepository(
    private val localSource: ClientDao,
    private val remoteSource: EInvoiceRemoteDataSource
) : ClientRepository {
    override fun getClients(): Flow<List<Client>> =
        localSource.getClients().map { clients -> clients.map { it.asClient() } }

    override fun getClientView(id: String): Flow<ClientView> =
        localSource.getClientViewById(id).filterNotNull().map(ClientViewEntity::asClientView)

    override suspend fun undoDeleteClient(id: String): Result<Unit> = tryWrapper {
        localSource.undoDeleteClient(id)
        Result.Success(Unit)
    }

    override fun getClient(id: String): Flow<Client> =
        localSource.getClientById(id).filterNotNull().map(ClientEntity::asClient)

    override fun getClientsPagingSource(): PagingSource<Int, Client> =
        localSource.getPagedClients().map { it.asClient() }.asPagingSourceFactory().invoke()

    override suspend fun createClient(client: Client): Result<Client> = tryWrapper {
        localSource.insertClient(client.asClientEntity(isCreated = true))
        Result.Success(client)
    }

    override suspend fun updateClient(client: Client): Result<Client> = tryWrapper {
        val clientEntity =
            localSource.getClientById(client.id).first() ?: return@tryWrapper Result.Error(
                CLIENT_NOT_FOUND
            )
        if (clientEntity.isCreated)
            localSource.updateClient(client.asClientEntity(isCreated = true))
        else
            localSource.updateClient(client.asClientEntity(isUpdated = true))
        Result.Success(client)
    }

    override suspend fun deleteClient(id: String): Result<Unit> = tryWrapper {
        val clientEntity = localSource.getClientById(id).first() ?: return@tryWrapper Result.Error(
            CLIENT_NOT_FOUND
        )
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
        val clients = localSource.getAllClients()
        var idMappings = emptyMap<String, String?>()
        val remotelyCreatedClients = mutableListOf<String>()
        val result = synchronizer.handleSync(
            remoteCreator = { idMappings = createAndGetIdMappings(clients) },
            remoteDeleter = { deleteRemoteAndLocal(clients) },
            remoteUpdater = { updateRemoteAndLocal(clients) },
            localCreator = { client ->
                remotelyCreatedClients.add(client.id)
                val ids = clients.map { it.id }
                if (client.id in ids)
                    localSource.updateClient(client)
                else
                    localSource.insertClient(client)
            },
            afterLocalCreate = {
                idMappings.forEach { (old, new) ->
                    if(new == null) return@forEach
                    localSource.updateDocumentsReceiverId(old, new)
                    localSource.deleteClient(old)
                }
            },
            remoteFetcher = {
                remoteSource.getClients().map { clients -> clients.map { it.asClientEntity().copy(isSynced = true) } }
            },
        )
        return result
    }

    private suspend fun updateRemoteAndLocal(clients: List<ClientEntity>) {
        clients.filter { it.isUpdated }
            .forEach { client ->
                val result = remoteSource.updateClient(client.asClient())
                if (result is Result.Success)
                    localSource.updateClient(client.copy(isUpdated = false))
            }
    }

    private suspend fun deleteRemoteAndLocal(clients: List<ClientEntity>) {
        clients.filter { it.isDeleted }
            .forEach { client ->
                val result = remoteSource.deleteClient(client.id)
                if (result is Result.Success)
                    localSource.deleteClient(client.id)
            }
    }

    private suspend fun createAndGetIdMappings(clients: List<ClientEntity>) =
        clients.filter { it.isCreated }
            .associateBy {
                val result = remoteSource.createClient(it.asClient())
                handleCreateResult(result, it)
            }.map { (newId, client) ->
                client.id to newId
            }.toMap()

    private suspend fun handleCreateResult(
        result: Result<Client>,
        clientEntity: ClientEntity
    ) = if (result is Result.Success)
        result.data.id
    else
        null.also { handleCreateError(result, clientEntity) }



    private suspend fun handleCreateError(
        result: Result<Client>,
        it: ClientEntity
    ) {
        val error = (result as? Result.Error)?.exception
        localSource.updateClient(it.copy(isSynced = false, syncError = error))
    }

}
package com.example.data.client

import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository : Syncable<Client> {
    fun getClients(): Flow<List<Client>>

    fun getClient(id: String): Flow<Client>

    suspend fun createClient(client: Client): Result<Client>

    suspend fun updateClient(client: Client): Result<Client>

    suspend fun deleteClient(id: String): Result<Unit>

    fun getClientsByCompanyId(companyId: String): Flow<List<Client>>
}
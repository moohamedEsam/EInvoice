package com.example.data.client

import androidx.paging.PagingSource
import com.example.common.models.Result
import com.example.data.sync.Syncable
import com.example.models.Client
import com.example.models.ClientView
import kotlinx.coroutines.flow.Flow

interface ClientRepository : Syncable<Client> {
    fun getClients(): Flow<List<Client>>

    fun getClientsPagingSource() : PagingSource<Int, Client>

    fun getClient(id: String): Flow<Client>

    fun getClientView(id: String): Flow<ClientView>

    suspend fun createClient(client: Client): Result<Client>

    suspend fun updateClient(client: Client): Result<Client>

    suspend fun deleteClient(id: String): Result<Unit>

    suspend fun undoDeleteClient(id: String): Result<Unit>

    fun getClientsByCompanyId(companyId: String): Flow<List<Client>>
}
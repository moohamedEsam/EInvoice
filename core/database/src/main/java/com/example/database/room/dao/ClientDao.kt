package com.example.database.room.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.database.models.client.ClientEntity
import com.example.database.models.client.ClientViewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM Client where isDeleted = 0")
    fun getClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client where isDeleted = 0")
    fun getPagedClients(): DataSource.Factory<Int, ClientEntity>

    @Query("SELECT * FROM Client")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM Client WHERE companyId = :companyId")
    fun getClientsByCompanyId(companyId: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE id = :id")
    fun getClientById(id: String): Flow<ClientEntity?>

    @Transaction
    @Query("SELECT * FROM Client WHERE isDeleted = 0 and id = :id")
    fun getClientViewById(id: String): Flow<ClientViewEntity?>

    @Insert
    suspend fun insertClient(client: ClientEntity)

    @Query("DELETE FROM Client where id = :id")
    suspend fun deleteClient(id: String)

    @Query("UPDATE Client SET isDeleted = 1 where id = :clientId")
    suspend fun markClientAsDeleted(clientId: String)

    @Query("UPDATE Client SET isDeleted = 0 where id = :clientId")
    suspend fun undoDeleteClient(clientId: String)

    @Query("delete from client")
    suspend fun deleteAllClients()

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Query("update document set receiverId =:newReceiverId where receiverId = :oldReceiverId")
    suspend fun updateDocumentsReceiverId(oldReceiverId: String, newReceiverId: String)
}
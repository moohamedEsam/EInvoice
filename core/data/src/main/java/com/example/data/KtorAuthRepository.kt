package com.example.data

import com.example.common.models.Result
import com.example.database.room.EInvoiceDatabase
import com.example.database.usecase.ClearAllTablesUseCase
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.network.EInvoiceRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KtorAuthRepository(
    private val remote: EInvoiceRemoteDataSource,
    private val clearAllTablesUseCase: ClearAllTablesUseCase,
) : AuthRepository {

    override suspend fun login(credentials: Credentials): Result<Token> = remote.login(credentials)

    override suspend fun register(register: Register): Result<Token> = remote.register(register)

    override suspend fun logout(): Result<Unit> {
        withContext(Dispatchers.IO) {
            clearAllTablesUseCase()
        }
        return remote.logout()
    }

}
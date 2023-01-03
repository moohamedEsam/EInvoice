package com.example.data

import com.example.common.models.Result
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.network.EInvoiceRemoteDataSource

class KtorAuthRepository(private val remote: EInvoiceRemoteDataSource) : AuthRepository {

    override suspend fun login(credentials: Credentials): Result<Token> = remote.login(credentials)

    override suspend fun register(register: Register): Result<Token> = remote.register(register)

    override suspend fun logout(): Result<Unit> = remote.logout()

}
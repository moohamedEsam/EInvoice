package com.example.network

import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.common.models.Result

interface AuthRemoteDataSource {
    suspend fun login(credentials: Credentials): Result<Token>

    suspend fun register(register: Register): Result<Token>

    suspend fun logout(): Result<Unit>

}
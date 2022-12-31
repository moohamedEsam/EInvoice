package com.example.auth.data

import com.example.auth.models.Credentials
import com.example.auth.models.Register
import com.example.auth.models.Token
import com.example.common.models.Result

interface AuthRepository {
    suspend fun login(credentials: Credentials): Result<Token>

    suspend fun register(register: Register): Result<Token>

    suspend fun logout(): Result<Unit>

}
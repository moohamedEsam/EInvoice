package com.example.data

import com.example.common.models.Result
import com.example.models.auth.*

interface AuthRepository {
    suspend fun login(credentials: Credentials): Result<Token>

    suspend fun register(register: Register): Result<Token>

    suspend fun logout(): Result<Unit>

}
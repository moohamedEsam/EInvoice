package com.example.data

import com.example.common.models.Result

interface AuthRepository {
    suspend fun login(credentials: com.example.models.auth.Credentials): Result<com.example.models.auth.Token>

    suspend fun register(register: com.example.models.auth.Register): Result<com.example.models.auth.Token>

    suspend fun logout(): Result<Unit>

}
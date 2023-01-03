package com.example.network

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.models.auth.Credentials
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.network.models.ApiResponse
import com.example.network.models.Urls
import com.example.network.models.asResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorAuthRemoteDataSource(private val client: HttpClient) : AuthRemoteDataSource {

    override suspend fun login(credentials: Credentials): Result<Token> = tryWrapper {
        val response = client.post(Urls.LOGIN) {
            setBody(credentials)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Token>>()
        apiResponse.asResult()
    }

    override suspend fun register(register: Register): Result<Token> = tryWrapper {
        val response = client.post(Urls.REGISTER) {
            setBody(register)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Token>>()
        apiResponse.asResult()
    }

    override suspend fun logout(): Result<Unit> = tryWrapper {
        val authProvider = client.plugin(Auth).providers.firstOrNull() as? BearerAuthProvider
            ?: return@tryWrapper Result.Error("No auth provider found")
        authProvider.clearToken()
        Result.Success(Unit)
    }
}
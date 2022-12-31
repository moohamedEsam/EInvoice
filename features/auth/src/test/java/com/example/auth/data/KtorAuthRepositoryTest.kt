package com.example.auth.data

import com.example.auth.models.ApiResponse
import com.example.auth.models.Credentials
import com.example.auth.models.Token
import com.example.common.models.Result
import com.google.common.truth.Truth.assertThat
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class KtorAuthRepositoryTest {
    private var authRepository: AuthRepository

    init {
        val mockEngine = MockEngine { request ->
            val body = Json.encodeToString(ApiResponse(true, null, "token"))
            respond(
                body,
                HttpStatusCode.OK,
                headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        authRepository = KtorAuthRepository(client)
    }

    @Test
    fun `login with valid credentials`() = runTest {
        val result = authRepository.login(Credentials("username", "password"))
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(Token("token"))
    }
}
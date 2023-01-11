package com.example.network.di

import android.util.Log
import com.example.common.functions.loadTokenFromSharedPref
import com.example.common.functions.saveTokenToSharedPref
import com.example.models.auth.Token
import com.example.network.EInvoiceRemoteDataSource
import com.example.network.KtorEInvoiceRemoteDataSource
import com.example.network.models.ApiResponse
import com.example.network.models.Urls
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient() }
    single<EInvoiceRemoteDataSource> { KtorEInvoiceRemoteDataSource(get()) }
}

fun Scope.provideHttpClient() = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.i("ktor", "log: $message")
            }
        }
    }

    install(Auth) {
        bearer {
            loadTokens {
                val token = loadTokenFromSharedPref(androidContext()) ?: return@loadTokens null
                BearerTokens(token, token)
            }

            refreshTokens {
                val token = loadTokenFromSharedPref(androidContext()) ?: return@refreshTokens null
                val refreshToken = client.post(Urls.REFRESH_TOKEN) {
                    setBody(mapOf("token" to token))
                    contentType(ContentType.Application.Json)
                    markAsRefreshTokenRequest()
                }.body<ApiResponse<Token>>().data!!
                if (token != refreshToken.value)
                    saveTokenToSharedPref(androidContext(), refreshToken.value)
                BearerTokens(token, token)
            }
        }
    }
}

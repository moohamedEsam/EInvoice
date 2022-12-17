package com.example.einvoice.data.network

import com.example.einvoice.data.models.ApiResponse
import com.example.einvoice.data.models.Token
import com.example.einvoice.models.Credentials
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class KtorRemoteDataSource(private val client: HttpClient) : EInvoiceRemoteDataSource {

    override suspend fun login(credentials: Credentials): ApiResponse<Token> = try {
        val response = client.post(Urls.LOGIN) {
            setBody(credentials)
        }
        mapApiResponse(response.body())
    } catch (e: Exception) {
        ApiResponse.Error(e.localizedMessage)
    }


    private fun <T> mapApiResponse(response: ApiResponse<T>): ApiResponse<T> = when (response) {
        is ApiResponse.Success -> ApiResponse.Success(response.data)
        is ApiResponse.Error -> ApiResponse.Error(response.error)
        is ApiResponse.Unknown -> if (response.isSuccess && response.data != null)
            ApiResponse.Success(response.data)
        else
            ApiResponse.Error(response.error)
        else -> ApiResponse.Error("Unknown error")
    }

}
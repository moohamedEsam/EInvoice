package com.example.network

import com.example.common.functions.tryWrapper
import com.example.common.models.Result
import com.example.models.Company
import com.example.network.models.ApiResponse
import com.example.network.models.Urls
import com.example.network.models.asResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorEInvoiceRemoteDataSource(private val client: HttpClient) : EInvoiceRemoteDataSource {
    override suspend fun createCompany(company: Company): Result<Company> = tryWrapper {
        val response = client.post(Urls.COMPANY) {
            setBody(company)
            contentType(ContentType.Application.Json)
        }
        val apiResponse = response.body<ApiResponse<Company>>()
        apiResponse.asResult()
    }

}
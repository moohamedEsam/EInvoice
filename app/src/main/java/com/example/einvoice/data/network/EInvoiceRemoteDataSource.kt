package com.example.einvoice.data.network

import com.example.einvoice.data.models.ApiResponse
import com.example.einvoice.data.models.Token
import com.example.einvoice.models.Credentials


interface EInvoiceRemoteDataSource {
    suspend fun login(credentials: Credentials) : ApiResponse<Token>
}
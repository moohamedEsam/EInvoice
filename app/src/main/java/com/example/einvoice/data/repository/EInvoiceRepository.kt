package com.example.einvoice.data.repository

import com.example.einvoice.models.Credentials

interface EInvoiceRepository {
    suspend fun login(credentials: Credentials): Result<Unit>
}
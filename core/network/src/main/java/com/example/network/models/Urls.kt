package com.example.network.models

import android.content.Context
import android.util.Log
import com.example.network.serializers.settingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object Urls {
    private var baseUrl = "http://192.168.1.4:7081"
    fun refreshToken() = "$baseUrl/User/refresh_token"
    fun login() = "$baseUrl/User/sign_in"
    fun register() = "$baseUrl/User/create"
    fun company() = "$baseUrl/Company"
    fun client() = "$baseUrl/Client"
    fun branch() = "$baseUrl/Branch"
    fun item() = "$baseUrl/Item"
    private fun constants() = "$baseUrl/Constants"
    fun unitTypes() = "${constants()}/unit_types"
    fun taxTypes() = "${constants()}/tax_types"
    fun document() = "$baseUrl/Document"
    fun syncDocumentStatus() = "${document()}/sync"
    fun getCompany(companyId: String) = "${company()}/$companyId"
    fun getClient(clientId: String) = "${client()}/$clientId"
    fun getBranch(branchId: String) = "${branch()}/$branchId"

    fun getItem(itemId: String) = "${item()}/$itemId"

    fun getDocument(documentId: String) = "${document()}/$documentId"

    fun cancelDocument(documentId: String) = "${document()}/cancel/$documentId"

    suspend fun setIpAddress(context: Context) {
        context.settingsDataStore.data.distinctUntilChanged()
            .collectLatest { baseUrl = it.ipAddress }
    }
}
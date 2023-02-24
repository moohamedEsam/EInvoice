package com.example.document.screens.all

import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import java.util.*

data class DocumentsScreenState(
    val isSyncing: Boolean,
    val isConnectedToNetwork: Boolean,
    val filters: Filters,
) {
    data class Filters(
        val company: Company? = null,
        val client: Client? = null,
        val branch: Branch? = null,
        val status: DocumentStatus? = null,
        val date: Date? = null,
        val query: String = ""
    )

    companion object
}

fun DocumentsScreenState.Companion.empty() = DocumentsScreenState(
    isSyncing = false,
    filters = DocumentsScreenState.Filters(
        company = null,
        client = null,
        branch = null,
        status = null,
        date = null,
        query = ""
    ),
    isConnectedToNetwork = false,
)
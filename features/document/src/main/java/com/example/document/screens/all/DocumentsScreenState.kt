package com.example.document.screens.all

import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView

data class DocumentsScreenState(
    val documents: List<DocumentView>,
    val query: String,
    val isSyncing: Boolean,
    val isConnectedToNetwork: Boolean,
    val companyFilter: Company?,
    val clientFilter: Client?,
    val branchFilter: Branch?,
    val statusFilter: DocumentStatus?,
){
    companion object
}

fun DocumentsScreenState.Companion.empty() = DocumentsScreenState(
    documents = emptyList(),
    query = "",
    isSyncing = false,
    companyFilter = null,
    clientFilter = null,
    branchFilter = null,
    statusFilter = null,
    isConnectedToNetwork = false,
)
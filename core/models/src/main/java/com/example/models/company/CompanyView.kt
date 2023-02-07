package com.example.models.company

import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.document.Document

data class CompanyView(
    val company: Company,
    val branches: List<Branch>,
    val clients: List<Client>,
    val documents:List<Document>
){
    companion object
}

fun CompanyView.Companion.empty() = CompanyView(
    company = Company.empty(),
    branches = emptyList(),
    clients = emptyList(),
    documents = emptyList()
)
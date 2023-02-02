package com.example.models.company

import com.example.models.Branch
import com.example.models.Client

data class CompanyView(
    val company: Company,
    val branches: List<Branch>,
    val clients: List<Client>,
)

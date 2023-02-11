package com.example.models

import com.example.models.company.Company

data class ClientView(
    val client: Client,
    val company: Company,
)
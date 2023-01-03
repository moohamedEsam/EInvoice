package com.example.models

import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: String,
    val registrationNumber: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address,
    val businessType: BusinessType,
    val taxStatus: TaxStatus,
    val companyId: String
)

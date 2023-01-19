package com.example.network.models

import com.example.models.Client
import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus

@kotlinx.serialization.Serializable
data class NetworkClient(
    val id: String,
    val registrationNumber: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address?,
    val businessType: Int,
    val status: Int,
    val companyId: String
)

fun NetworkClient.asClient() = Client(
    id = id,
    registrationNumber = registrationNumber,
    name = name,
    email = email,
    phone = phone,
    address = address,
    businessType = BusinessType.values().first { it.ordinal == businessType },
    status = TaxStatus.values().first { it.ordinal == status },
    companyId = companyId
)

fun Client.asNetworkClient() = NetworkClient(
    id = id,
    registrationNumber = registrationNumber,
    name = name,
    email = email,
    phone = phone,
    address = address,
    businessType = businessType.ordinal,
    status = status.ordinal,
    companyId = companyId
)
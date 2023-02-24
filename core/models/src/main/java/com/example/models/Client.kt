package com.example.models

import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Client(
    val registrationNumber: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: Address?,
    val businessType: BusinessType,
    val status: TaxStatus,
    val companyId: String,
    val id: String = UUID.randomUUID().toString(),
    val isSynced: Boolean = false,
    val syncError: String? = null
){
    companion object
}

fun Client.Companion.empty() = Client(
    registrationNumber = "",
    name = "",
    email = "",
    phone = "",
    address = null,
    businessType = BusinessType.P,
    status = TaxStatus.Taxable,
    companyId = ""
)

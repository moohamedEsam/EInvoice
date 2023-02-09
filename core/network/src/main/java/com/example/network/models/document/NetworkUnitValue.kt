package com.example.network.models.document

import com.example.models.invoiceLine.UnitValue

@kotlinx.serialization.Serializable
data class NetworkUnitValue(
    val currencyEgp: Double,
    val currencySold: String,
    val currencyExchangeRate: Double = 1.0
)

fun UnitValue.asNetworkUnitValue() = NetworkUnitValue(
    currencyEgp = currencyEgp,
    currencySold = currencySold,
)

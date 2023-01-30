package com.example.models.invoiceLine

@kotlinx.serialization.Serializable
data class UnitValue(
    val currencyEgp: String,
    val currencySold: String,
)

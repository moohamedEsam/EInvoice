package com.example.models.invoiceLine

@kotlinx.serialization.Serializable
data class UnitValue(
    val currencyEgp: Double,
    val currencySold: String,
){
    companion object
}

fun UnitValue.Companion.empty() = UnitValue(
    currencyEgp = 0.0,
    currencySold = "",
)

package com.example.models.invoiceLine

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TaxView(
    val code: String,
    @SerialName("desc_en")
    val name: String,
    @SerialName("taxSubTypes")
    val subTaxes: List<SubTax>,
)

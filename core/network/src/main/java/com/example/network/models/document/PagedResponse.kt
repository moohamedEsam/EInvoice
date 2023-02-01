package com.example.network.models.document

@kotlinx.serialization.Serializable
data class PagedResponse<T>(
    val data:List<T>,
    val page:Int,
    val pageSize:Int,
    val total:Int,
)

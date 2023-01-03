package com.example.models.auth

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Token(val value: String)

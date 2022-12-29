package com.example.auth.models

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Token(val value: String)

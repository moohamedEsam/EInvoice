package com.example.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val email: String, val password: String)

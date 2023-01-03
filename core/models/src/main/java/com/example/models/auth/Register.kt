package com.example.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class Register(val username: String, val email: String, val password: String)

package com.example.domain.auth

import com.example.common.models.Result
import com.example.models.auth.Credentials
import com.example.models.auth.Token

fun interface LoginUseCase : suspend (Credentials) -> Result<Token>
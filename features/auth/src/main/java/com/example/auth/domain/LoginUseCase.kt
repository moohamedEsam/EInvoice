package com.example.auth.domain

import com.example.auth.models.Credentials
import com.example.auth.models.Token
import com.example.common.models.Result

fun interface LoginUseCase : suspend (Credentials) -> Result<Token>
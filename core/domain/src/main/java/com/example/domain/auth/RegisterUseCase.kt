package com.example.domain.auth

import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.common.models.Result

fun interface RegisterUseCase : suspend (Register) -> Result<Token>
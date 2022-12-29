package com.example.auth.domain

import com.example.auth.models.Register
import com.example.auth.models.Token
import com.example.common.Result

fun interface RegisterUseCase : suspend (Register) -> Result<Token>
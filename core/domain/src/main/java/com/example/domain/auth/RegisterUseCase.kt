package com.example.domain.auth

import android.content.Context
import com.example.common.functions.saveTokenToSharedPref
import com.example.models.auth.Register
import com.example.models.auth.Token
import com.example.common.models.Result
import com.example.data.AuthRepository

fun interface RegisterUseCase : suspend (Register) -> Result<Token>

fun registerUseCase(authRepository: AuthRepository, context: Context) = RegisterUseCase { register ->
    val result = authRepository.register(register)
    result.ifSuccess {
        saveTokenToSharedPref(context, it.value)
    }
}
package com.example.domain.auth

import android.content.Context
import com.example.common.functions.saveTokenToSharedPref
import com.example.common.models.Result
import com.example.data.AuthRepository
import com.example.domain.sync.OneTimeSyncUseCase
import com.example.models.auth.Credentials
import com.example.models.auth.Token

fun interface LoginUseCase : suspend (Credentials) -> Result<Token>

fun loginUseCase(
    authRepository: AuthRepository,
    context: Context,
    syncUseCase: OneTimeSyncUseCase
) = LoginUseCase { credentials ->
    val result = authRepository.login(credentials)
    result.ifSuccess {
        saveTokenToSharedPref(context, it.token)
        syncUseCase()
    }
}
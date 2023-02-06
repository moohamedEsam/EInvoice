package com.example.domain.auth

import android.content.Context
import com.example.common.functions.saveTokenToSharedPref
import com.example.common.models.Result
import com.example.data.auth.AuthRepository

fun interface LogoutUseCase : suspend () -> Result<Unit>

fun logoutUseCase(authRepository: AuthRepository, context: Context) = LogoutUseCase {
    val result = authRepository.logout()
    result.ifSuccess {
        saveTokenToSharedPref(context, null)
    }
}
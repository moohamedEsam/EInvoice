package com.example.domain.auth

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.common.functions.saveTokenToSharedPref
import com.example.common.models.Result
import com.example.data.AuthRepository
import com.example.domain.sync.OneTimeSyncUseCase
import com.example.models.auth.Credentials
import com.example.models.auth.Token
import com.example.worker.SynchronizerWorker

fun interface LoginUseCase : suspend (Credentials) -> Result<Token>

fun loginUseCase(
    authRepository: AuthRepository,
    context: Context,
    syncUseCase: OneTimeSyncUseCase
) = LoginUseCase { credentials ->
    val result = authRepository.login(credentials)
    result.ifSuccess {
        saveTokenToSharedPref(context, it.value)
        syncUseCase()
    }
}
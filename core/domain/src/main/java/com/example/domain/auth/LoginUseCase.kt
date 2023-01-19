package com.example.domain.auth

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.common.functions.saveTokenToSharedPref
import com.example.common.models.Result
import com.example.data.AuthRepository
import com.example.models.auth.Credentials
import com.example.models.auth.Token
import com.example.worker.SynchronizerWorker

fun interface LoginUseCase : suspend (Credentials) -> Result<Token>

fun loginUseCase(authRepository: AuthRepository, context: Context) = LoginUseCase { credentials ->
    val result = authRepository.login(credentials)
    result.ifSuccess {
        saveTokenToSharedPref(context, it.value)
        val workRequest = OneTimeWorkRequestBuilder<SynchronizerWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(workRequest)
    }
}
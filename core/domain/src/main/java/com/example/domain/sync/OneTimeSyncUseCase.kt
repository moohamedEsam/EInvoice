package com.example.domain.sync

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.worker.SynchronizerWorker

fun interface OneTimeSyncUseCase : () -> Unit


fun oneTimeSyncUseCase(context: Context) = OneTimeSyncUseCase {
    val workRequest = OneTimeWorkRequestBuilder<SynchronizerWorker>()
        .setConstraints(SynchronizerWorker.workConstraints)
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .addTag(SynchronizerWorker.workName)
        .build()

    val workManager = WorkManager.getInstance(context)
    workManager.enqueue(workRequest)
}
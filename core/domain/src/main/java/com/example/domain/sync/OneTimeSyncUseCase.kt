package com.example.domain.sync

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.worker.SynchronizerWorker

fun interface OneTimeSyncUseCase : () -> LiveData<WorkInfo>


fun oneTimeSyncUseCase(context: Context) = OneTimeSyncUseCase {
    val workRequest = OneTimeWorkRequestBuilder<SynchronizerWorker>()
        .setConstraints(SynchronizerWorker.workConstraints)
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .addTag(SynchronizerWorker.workName)
        .build()

    val workManager = WorkManager.getInstance(context)
    workManager.enqueue(workRequest)
    workManager.getWorkInfoByIdLiveData(workRequest.id)
}
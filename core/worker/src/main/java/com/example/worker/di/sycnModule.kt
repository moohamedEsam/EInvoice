package com.example.worker.di

import com.example.data.sync.Synchronizer
import com.example.worker.SynchronizerWorker
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val syncModule = module {
    worker {
        SynchronizerWorker(
            appContext = androidContext(),
            workerParams = get(),
            companyRepository = get(),
            branchRepository = get(),
            synchronizer = Synchronizer(),
            dispatcher = Dispatchers.IO
        )
    }
}
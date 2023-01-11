package com.example.einvoice

import android.app.Application
import androidx.work.*
import com.example.auth.di.authModule
import com.example.branch.di.branchModule
import com.example.company.di.companyModule
import com.example.data.di.dataModule
import com.example.database.di.databaseModule
import com.example.domain.di.domainModule
import com.example.einvoice.di.appModule
import com.example.maplocation.mapLocationModule
import com.example.network.di.networkModule
import com.example.worker.SynchronizerWorker
import com.example.worker.di.syncModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ApplicationClass)
            workManagerFactory()
            modules(
                listOf(
                    authModule,
                    appModule,
                    networkModule,
                    dataModule,
                    domainModule,
                    companyModule,
                    databaseModule,
                    mapLocationModule,
                    branchModule,
                    syncModule
                )
            )
        }

        val workRequest = PeriodicWorkRequestBuilder<SynchronizerWorker>(1, TimeUnit.DAYS)
            .setConstraints(SynchronizerWorker.workConstraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SynchronizerWorker.workName,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
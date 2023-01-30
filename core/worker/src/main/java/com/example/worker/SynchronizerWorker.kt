package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.data.branch.BranchRepository
import com.example.data.client.ClientRepository
import com.example.data.company.CompanyRepository
import com.example.data.document.DocumentRepository
import com.example.data.item.ItemRepository
import com.example.data.sync.Synchronizer
import kotlinx.coroutines.*

class SynchronizerWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val companyRepository: CompanyRepository,
    private val branchRepository: BranchRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val documentRepository: DocumentRepository,
    private val synchronizer: Synchronizer
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            withContext(synchronizer.dispatcher) {
                val isSyncSuccessful = listOf(
                    companyRepository.syncWith(synchronizer),
                    branchRepository.syncWith(synchronizer),
                    clientRepository.syncWith(synchronizer),
                    itemRepository.syncWith(synchronizer),
                    documentRepository.syncWith(synchronizer)
                ).all { it }
                if (!isSyncSuccessful)
                    Result.retry()
                Result.success()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = appContext.syncForegroundInfo()

    companion object {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        const val workName = "SynchronizerWorker"
    }
}
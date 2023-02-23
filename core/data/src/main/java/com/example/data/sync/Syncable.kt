package com.example.data.sync

import com.example.common.models.Result
import kotlinx.coroutines.*

interface Syncable<T> {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

data class Synchronizer(val dispatcher: CoroutineDispatcher = Dispatchers.IO)


suspend fun <T> Synchronizer.handleSync(
    remoteFetcher: suspend () -> Result<List<T>>,
    remoteDeleter: suspend () -> Unit,
    remoteCreator: suspend () -> Unit,
    remoteUpdater: suspend () -> Unit,
    localCreator: suspend (T) -> Unit,
    afterLocalCreate: suspend () -> Unit,
): Boolean {
    return withContext(dispatcher) {
        awaitAll(
            async { remoteDeleter() },
            async { remoteCreator() },
            async { remoteUpdater() }
        )
        val result = remoteFetcher()
        if (result !is Result.Success) return@withContext false


        for (record in result.data)
            localCreator(record)

        afterLocalCreate()

        true
    }
}
package com.example.data.sync

import com.example.common.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface Syncable<T> {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

data class Synchronizer(val dispatcher: CoroutineDispatcher = Dispatchers.IO)


suspend fun <T> Synchronizer.handleSync(
    remoteFetcher: suspend () -> Result<List<T>>,
    remoteDeleter: suspend () -> Result<Unit>,
    remoteCreator: suspend () -> Result<Unit>,
    remoteUpdater: suspend () -> Result<Unit>,
    localCreator: suspend (T) -> Unit,
    beforeLocalCreate: suspend () -> Unit,
): Boolean {
    val remoteCreateResult = remoteCreator()
    if (remoteCreateResult !is Result.Success) return false

    val remoteDeleteResult = remoteDeleter()
    if (remoteDeleteResult !is Result.Success) return false

    val remoteUpdateResult = remoteUpdater()
    if (remoteUpdateResult !is Result.Success) return false

    val result = remoteFetcher()
    if (result !is Result.Success) return false

    beforeLocalCreate()

    for (record in result.data)
        localCreator(record)


    return true
}
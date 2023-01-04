package com.example.data.sync

import com.example.common.models.Result

interface Syncable<T> {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

class Synchronizer


suspend fun <T> Synchronizer.handleSync(
    remoteFetcher: suspend () -> Result<List<T>>,
    remoteDeleter: suspend () -> Result<Unit>,
    remoteCreator: suspend () -> Result<Unit>,
    localCreator: suspend (T) -> Unit,
    localDeleter: suspend (T) -> Unit,
): Boolean {
    val remoteCreateResult = remoteCreator()
    if (remoteCreateResult !is Result.Success) return false

    val remoteDeleteResult = remoteDeleter()
    if (remoteDeleteResult !is Result.Success) return false

    val result = remoteFetcher()
    if (result !is Result.Success) return false

    for (record in result.data) {
        localDeleter(record)
        localCreator(record)
    }

    return true
}
package com.example.domain.networkStatus

import kotlinx.coroutines.flow.Flow

fun interface NetworkObserver{
    fun observeNetworkStatus(): Flow<NetworkStatus>
}

enum class NetworkStatus {
    CONNECTED,
    DISCONNECTED,
}
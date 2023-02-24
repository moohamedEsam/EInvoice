package com.example.einvoice.presentation.shared

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.common.models.SnackBarEvent
import com.example.domain.auth.LogoutUseCase
import com.example.domain.sync.OneTimeSyncUseCase
import com.example.functions.SnackBarManager
import kotlinx.coroutines.launch

class LayoutViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val oneTimeSyncUseCase: OneTimeSyncUseCase,
    snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {

    fun logout() = viewModelScope.launch {
        logoutUseCase()
    }

    fun sync(owner: LifecycleOwner) {
        oneTimeSyncUseCase().observe(owner) { workInfo ->
            viewModelScope.launch {
                val event = getSnackBarEventFromWorkInfo(workInfo) ?: return@launch
                showSnackBarEvent(event)
            }
        }
    }

    private fun getSnackBarEventFromWorkInfo(workInfo: WorkInfo): SnackBarEvent? =
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> SnackBarEvent("sync succeeded")
            WorkInfo.State.FAILED -> SnackBarEvent("Sync failed")
            WorkInfo.State.ENQUEUED -> SnackBarEvent("Sync enqueued")
            else -> null
        }

}
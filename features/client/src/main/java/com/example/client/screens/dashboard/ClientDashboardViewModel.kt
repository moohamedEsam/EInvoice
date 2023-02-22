package com.example.client.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.client.DeleteClientUseCase
import com.example.domain.client.GetClientUseCase
import com.example.domain.client.UndoDeleteClientUseCase
import com.example.domain.document.DaysRange
import com.example.domain.document.GetDocumentsByTypeInDurationUseCase
import com.example.models.Client
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ClientDashboardViewModel(
    private val clientId: String,
    private val getClientUseCase: GetClientUseCase,
    private val getDocumentsByClientUseCase: GetDocumentsByTypeInDurationUseCase,
    private val deleteClientUseCase: DeleteClientUseCase,
    private val undoDeleteClientUseCase: UndoDeleteClientUseCase
) : ViewModel() {
    private val _client: MutableStateFlow<Client?> = MutableStateFlow(null)
    private val _lastDate = MutableStateFlow(Date())
    private val _documents = MutableStateFlow(emptyList<DocumentView>())
    private val _isDeleteEnabled = _documents.map(List<DocumentView>::isEmpty)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val uiState = combine(
        _client,
        _documents,
        _lastDate,
        _isDeleteEnabled
    ) { client, documents, startDate, isDeleteEnabled ->
        if (client == null) return@combine ClientDashBoardState.random()
        ClientDashBoardState(
            client = client,
            documents = documents,
            endDate = startDate,
            isDeleteEnabled = isDeleteEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ClientDashBoardState.random())

    fun onDatePicked(date: Date) {
        _lastDate.value = date
    }

    fun onDeleteClick(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteClientUseCase(clientId)
            onResult(result)
        }
    }

    fun onUndoDeleteClick(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = undoDeleteClientUseCase(clientId)
            onResult(result)
        }
    }

    init {
        observeClient()
        observeDocuments()
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            _lastDate.collectLatest { endDate ->
                val startDate = Calendar.getInstance().apply {
                    time = endDate
                    add(Calendar.MONTH, -1)
                }.time
                val params = GetDocumentsByTypeInDurationUseCase.Params(
                    type = GetDocumentsByTypeInDurationUseCase.Types.Client,
                    id = clientId,
                    daysRange = DaysRange(startDate, endDate)
                )
                getDocumentsByClientUseCase(params)
                    .collect { documents ->
                        _documents.value = documents
                    }
            }
        }
    }

    private fun observeClient() {
        viewModelScope.launch {
            getClientUseCase(clientId)
                .collectLatest { client ->
                    _client.value = client
                }
        }
    }
}
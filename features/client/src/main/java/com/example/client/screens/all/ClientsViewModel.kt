package com.example.client.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.client.GetClientsUseCase
import com.example.models.Client
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientsViewModel(private val getClientsUseCase: GetClientsUseCase) : ViewModel() {
    private val _clients = MutableStateFlow(emptyList<Client>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()
    val clients = combine(_query, _clients) { query, clients ->
        if (query.isBlank())
            clients
        else
            clients.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            getClientsUseCase().collectLatest {
                _clients.value = it
            }
        }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }
}
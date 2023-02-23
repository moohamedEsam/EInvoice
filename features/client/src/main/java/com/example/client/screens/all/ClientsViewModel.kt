package com.example.client.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.domain.client.GetClientsPagingSourceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class ClientsViewModel(private val getClientsPagingSourceUseCase: GetClientsPagingSourceUseCase) :
    ViewModel() {
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()
    val clients = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5
        ),
        pagingSourceFactory = getClientsPagingSourceUseCase
    ).flow.cachedIn(viewModelScope)
        .combine(query) { clients, query ->
            if (query.isNotBlank())
                clients.filter { it.name.contains(query, true) }
            else
                clients
        }


    fun onQueryChanged(query: String) {
        _query.value = query
    }
}
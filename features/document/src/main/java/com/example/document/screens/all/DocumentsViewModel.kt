package com.example.document.screens.all

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.document.GetDocumentsUseCase
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DocumentsViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase
) : ViewModel() {
    private val _documents = MutableStateFlow(emptyList<DocumentView>())
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val documents = combine(_documents, _query) { documents, query ->
        if (query.isBlank()) documents
        else documents.filter {
            val names = buildList {
                add(it.company.name)
                add(it.client.name)
                addAll(it.invoices.map { invoice -> invoice.item.name })
            }
            names.any { name -> name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setQuery(value: String) = viewModelScope.launch {
        _query.update { value }
    }

    init {
        viewModelScope.launch {
            getDocumentsUseCase().collectLatest {
                _documents.update { _ -> it }
            }
        }
    }

}
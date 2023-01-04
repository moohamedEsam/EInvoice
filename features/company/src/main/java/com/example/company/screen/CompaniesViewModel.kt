package com.example.company.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.company.GetCompaniesUseCase
import com.example.models.Company
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompaniesViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase
) : ViewModel() {
    private val _companies = MutableStateFlow(emptyList<Company>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val companies = combine(_companies, _query){ companies, query ->
        if(query.isBlank()) return@combine companies
        companies.filter { company ->
            company.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            getCompaniesUseCase().collectLatest {
                _companies.update { _ -> it }
            }
        }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }
}
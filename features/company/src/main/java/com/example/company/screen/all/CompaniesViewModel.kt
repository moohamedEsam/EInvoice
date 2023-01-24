package com.example.company.screen.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.company.*
import com.example.models.company.Company
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompaniesViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val deleteCompanyUseCase: DeleteCompanyUseCase,
    private val undoDeleteCompanyUseCase: UndoDeleteCompanyUseCase
) : ViewModel() {
    private val _companies = MutableStateFlow(emptyList<Company>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val companies = combine(_companies, _query) { companies, query ->
        if (query.isBlank()) return@combine companies
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

    fun deleteCompany(company: Company, onResult: (result: Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            val result = deleteCompanyUseCase(company.id)
            onResult(result)
        }
    }

    fun undoDeleteCompany(company: Company) {
        viewModelScope.launch {
            undoDeleteCompanyUseCase(company.id)
        }
    }
}
package com.example.company.screen.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.common.models.Result
import com.example.domain.company.*
import com.example.models.company.Company
import com.example.models.company.CompanyView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompaniesViewModel(
    private val getCompanyPagingSource: GetCompanyPagingSource,
    private val deleteCompanyUseCase: DeleteCompanyUseCase,
    private val undoDeleteCompanyUseCase: UndoDeleteCompanyUseCase
) : ViewModel() {
    private val _companies = MutableStateFlow(emptyList<CompanyView>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val companies = combine(_companies, _query) { companies, query ->
        if (query.isBlank()) return@combine companies
        companies.filter { companyView ->
            companyView.company.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 5
        ),
        pagingSourceFactory = getCompanyPagingSource
    ).flow.cachedIn(viewModelScope)


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
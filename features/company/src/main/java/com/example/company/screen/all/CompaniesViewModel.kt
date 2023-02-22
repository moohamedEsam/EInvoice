package com.example.company.screen.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.domain.company.*
import kotlinx.coroutines.flow.*

class CompaniesViewModel(companyPagingSource: GetCompanyPagingSource) : ViewModel() {
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 5
        ),
        pagingSourceFactory = companyPagingSource
    ).flow.cachedIn(viewModelScope)
        .combine(_query) { pagingData, query ->
            if (query.isBlank()) return@combine pagingData
            pagingData.filter { companyView ->
                companyView.company.name.contains(query, ignoreCase = true)
            }
        }


    fun setQuery(query: String) {
        _query.update { query }
    }
}
package com.example.branch.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.domain.branch.GetBranchesPagingSourceUseCase
import com.example.domain.branch.GetBranchesUseCase
import com.example.models.branch.Branch
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BranchesViewModel(
    private val getBranchesPagingSourceUseCase: GetBranchesPagingSourceUseCase
) : ViewModel() {
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val branches = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5
        ),
        pagingSourceFactory = getBranchesPagingSourceUseCase
    ).flow.cachedIn(viewModelScope)
        .combine(query) { branches, query ->
            if (query.isNotBlank())
                branches.filter { it.name.contains(query, true) }
            else
                branches
        }

    fun setQuery(query: String) {
        _query.update { query }
    }
}
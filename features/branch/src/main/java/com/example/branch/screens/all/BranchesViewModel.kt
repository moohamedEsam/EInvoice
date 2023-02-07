package com.example.branch.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.branch.GetBranchesUseCase
import com.example.models.branch.Branch
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BranchesViewModel(
    private val getBranchesUseCase: GetBranchesUseCase,
) : ViewModel() {
    private val _branches = MutableStateFlow(emptyList<Branch>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val branches = getFilteredBranchesState()


    init {
        observeBranch()
    }

    private fun observeBranch() {
        viewModelScope.launch {
            getBranchesUseCase().collectLatest {
                _branches.update { _ -> it }
            }
        }
    }

    private fun getFilteredBranchesState() = combine(_branches, _query) { _branches, query ->
        if (query.isBlank()) return@combine _branches
        _branches.filter { branch ->
            branch.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun setQuery(query: String) {
        _query.update { query }
    }
}
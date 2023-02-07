package com.example.branch.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.branch.DeleteBranchUseCase
import com.example.domain.branch.GetBranchViewUseCase
import com.example.domain.branch.UndoDeleteBranchUseCase
import com.example.domain.document.GetDocumentsByBranchUseCase
import com.example.models.branch.BranchView
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BranchDashboardViewModel(
    private val branchId: String,
    private val getBranchViewUseCase: GetBranchViewUseCase,
    private val getDocumentsByBranchUseCase: GetDocumentsByBranchUseCase,
    private val deleteBranchUseCase: DeleteBranchUseCase,
    private val undoDeleteBranchUseCase: UndoDeleteBranchUseCase
) : ViewModel() {
    private val _branchView: MutableStateFlow<BranchView?> = MutableStateFlow(null)
    private val _lastDate = MutableStateFlow(Date())
    private val _documents = MutableStateFlow(emptyList<DocumentView>())
    private val _isDeleteEnabled = combine(_documents, _branchView) { documents, branchView ->
        branchView?.items?.isEmpty() == true && documents.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val uiState = combine(
        _branchView,
        _documents,
        _lastDate,
        _isDeleteEnabled
    ) { branchView, documents, startDate, isDeleteEnabled ->
        if (branchView == null) return@combine BranchDashboardState.random()
        BranchDashboardState(
            branchView = branchView,
            documents = documents,
            pickedDate = startDate,
            isDeleteEnabled = isDeleteEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), BranchDashboardState.random())

    fun onDatePicked(date: Date) {
        _lastDate.value = date
    }

    fun onDeleteClick(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteBranchUseCase(branchId)
            onResult(result)
        }
    }

    fun onUndoDeleteClick(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = undoDeleteBranchUseCase(branchId)
            onResult(result)
        }
    }

    init {
        observeBranch()
        observeDocuments()
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            _lastDate.collectLatest { toDate ->
                val fromDate = Calendar.getInstance().apply {
                    time = toDate
                    add(Calendar.MONTH, -1)
                }.time
                val params = GetDocumentsByBranchUseCase.Params(
                    branchId = branchId,
                    fromDate = fromDate,
                    toDate = toDate
                )
                getDocumentsByBranchUseCase(params)
                    .collect { documents ->
                        _documents.value = documents
                    }
            }
        }
    }

    private fun observeBranch() {
        viewModelScope.launch {
            getBranchViewUseCase(branchId)
                .collectLatest { branchView ->
                    _branchView.value = branchView
                }
        }
    }
}
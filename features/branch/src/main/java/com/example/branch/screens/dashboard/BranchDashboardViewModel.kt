package com.example.branch.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.branch.DeleteBranchUseCase
import com.example.domain.branch.GetBranchViewUseCase
import com.example.domain.branch.UndoDeleteBranchUseCase
import com.example.domain.document.DaysRange
import com.example.domain.document.GetDocumentsByTypeInDurationUseCase
import com.example.functions.SnackBarManager
import com.example.models.branch.BranchView
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BranchDashboardViewModel(
    private val branchId: String,
    private val getBranchViewUseCase: GetBranchViewUseCase,
    private val getDocumentsByBranchUseCase: GetDocumentsByTypeInDurationUseCase,
    private val deleteBranchUseCase: DeleteBranchUseCase,
    private val undoDeleteBranchUseCase: UndoDeleteBranchUseCase,
    private val snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
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

    fun onDeleteClick() {
        viewModelScope.launch {
            val result = deleteBranchUseCase(branchId)
            val event = result.getSnackBarEvent(
                successMessage = "Branch deleted successfully",
                successActionLabel = "Undo",
                successAction = { undoDeleteBranchUseCase(branchId) },
                errorActionLabel = "Retry",
                errorAction = ::onDeleteClick
            )
            showSnackBarEvent(event)
        }
    }

    init {
        observeBranch()
        observeDocuments()
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            _lastDate.collectLatest { endDate ->
                val startDate = Calendar.getInstance().apply {
                    time = endDate
                    add(Calendar.MONTH, -1)
                }.time
                val params = GetDocumentsByTypeInDurationUseCase.Params(
                    type = GetDocumentsByTypeInDurationUseCase.Types.Branch,
                    id = branchId,
                    daysRange = DaysRange(startDate, endDate)
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
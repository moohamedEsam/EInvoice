package com.example.company.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.company.DeleteCompanyUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UndoDeleteCompanyUseCase
import com.example.domain.document.GetDocumentsByCompanyUseCase
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CompanyDashboardViewModel(
    private val companyId: String,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getDocumentsUseCase: GetDocumentsByCompanyUseCase,
    private val deleteCompanyUseCase: DeleteCompanyUseCase,
    private val undoDeleteCompanyUseCase: UndoDeleteCompanyUseCase
) : ViewModel() {

    private val _companyView: MutableStateFlow<CompanyView> = MutableStateFlow(CompanyView.empty())
    private val _pickedDate = MutableStateFlow(Date())
    private val _documents = MutableStateFlow<List<DocumentView>>(emptyList())

    private fun getDateMinusOneMonth(fromDate: Date) = Calendar.getInstance().apply {
        time = fromDate
        add(Calendar.MONTH, -1)
    }.time

    private val _isDeleteEnabled = combine(_companyView, _documents) { company, documents ->
        company.clients.isEmpty() && company.branches.isEmpty() && documents.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val uiState =
        combine(
            _companyView,
            _documents,
            _isDeleteEnabled,
            _pickedDate
        ) { company, documents, isDeleteEnabled, pickedDate ->
            CompanyDashboardState(
                companyView = company,
                documents = documents,
                invoices = documents.flatMap { it.invoices },
                isDeleteEnabled = isDeleteEnabled,
                pickedDate = pickedDate
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        observeCompany()
        observeDocuments()
    }

    fun deleteCompany(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteCompanyUseCase(companyId)
            onResult(result)
        }
    }

    fun undoDeleteCompany(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = undoDeleteCompanyUseCase(companyId)
            onResult(result)
        }
    }

    fun setPickedDate(date: Date) {
        viewModelScope.launch {
            _pickedDate.value = date
        }
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            _pickedDate.collectLatest {toDate->
                val fromDate = getDateMinusOneMonth(toDate)
                val params = GetDocumentsByCompanyUseCase.Params(companyId, fromDate, toDate)
                getDocumentsUseCase(params).distinctUntilChanged().collectLatest {
                    _documents.value = it
                }
            }
        }
    }

    private fun observeCompany() {
        viewModelScope.launch {
            getCompanyUseCase(companyId).distinctUntilChanged().collectLatest {
                _companyView.value = it
            }
        }
    }
}
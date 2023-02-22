package com.example.company.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.company.DeleteCompanyUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UndoDeleteCompanyUseCase
import com.example.domain.document.DaysRange
import com.example.domain.document.GetDocumentsByTypeInDurationUseCase
import com.example.functions.BaseSnackBarManager
import com.example.functions.SnackBarManager
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CompanyDashboardViewModel(
    private val companyId: String,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getDocumentsUseCase: GetDocumentsByTypeInDurationUseCase,
    private val deleteCompanyUseCase: DeleteCompanyUseCase,
    private val undoDeleteCompanyUseCase: UndoDeleteCompanyUseCase,
    private val snackBarManager: BaseSnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {

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

    fun deleteCompany() {
        viewModelScope.launch {
            val result = deleteCompanyUseCase(companyId)
            val event = result.getSnackBarEvent(
                successMessage = "Company deleted",
                successActionLabel = "Undo",
                successAction = ::undoDeleteCompany,
                errorActionLabel = "Try again",
                errorAction = ::deleteCompany
            )

            snackBarManager.showSnackBarEvent(event)
        }
    }

    private fun undoDeleteCompany() {
        viewModelScope.launch {
            val result = undoDeleteCompanyUseCase(companyId)
            val event = result.getSnackBarEvent(
                successMessage = "Company restored",
                errorActionLabel = "Try again",
                errorAction = ::undoDeleteCompany
            )
            snackBarManager.showSnackBarEvent(event)
        }
    }

    fun setPickedDate(date: Date) {
        viewModelScope.launch {
            _pickedDate.value = date
        }
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            _pickedDate.collectLatest { endDate ->
                val startDate = getDateMinusOneMonth(endDate)
                val params = GetDocumentsByTypeInDurationUseCase.Params(
                    type = GetDocumentsByTypeInDurationUseCase.Types.Company,
                    id = companyId,
                    daysRange = DaysRange(startDate, endDate)
                )
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
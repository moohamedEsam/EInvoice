package com.example.company.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.document.GetDocumentsByCompanyUseCase
import com.example.models.company.CompanyView
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompanyDashboardViewModel(
    private val companyId: String,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getDocumentsUseCase: GetDocumentsByCompanyUseCase
) : ViewModel() {

    private val _companyView: MutableStateFlow<CompanyView?> = MutableStateFlow(null)
    private val _documents = MutableStateFlow(emptyList<DocumentView>())

    val uiState = combine(_companyView, _documents) { company, documents ->
        if (company == null) return@combine null
        CompanyDashboardState(
            companyView = company,
            documents = documents,
            invoices = documents.flatMap { it.invoices }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    init {
        observeCompany()
        observeDocuments()
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            getDocumentsUseCase(companyId).collectLatest {
                _documents.value = it
            }
        }
    }

    private fun observeCompany() {
        viewModelScope.launch {
            getCompanyUseCase(companyId).collectLatest {
                _companyView.value = it
            }
        }
    }
}
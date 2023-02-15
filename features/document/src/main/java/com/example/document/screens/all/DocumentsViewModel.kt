package com.example.document.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.document.CancelDocumentUseCase
import com.example.domain.document.GetDocumentsUseCase
import com.example.domain.document.SyncDocumentsStatusUseCase
import com.example.domain.networkStatus.NetworkObserver
import com.example.domain.networkStatus.NetworkStatus
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DocumentsViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val networkObserver: NetworkObserver,
    private val cancelDocumentUseCase: CancelDocumentUseCase,
    private val syncDocumentsStatusUseCase: SyncDocumentsStatusUseCase
) : ViewModel() {
    private val _documents = MutableStateFlow(emptyList<DocumentView>())
    private val _query = MutableStateFlow("")
    private val _companyFilter: MutableStateFlow<Company?> = MutableStateFlow(null)
    private val _clientFilter: MutableStateFlow<Client?> = MutableStateFlow(null)
    private val _branchFilter: MutableStateFlow<Branch?> = MutableStateFlow(null)
    private val _statusFilter: MutableStateFlow<DocumentStatus?> = MutableStateFlow(null)
    private val _isSyncing = MutableStateFlow(false)
    private val _dateFilter: MutableStateFlow<Date?> = MutableStateFlow(null)
    private val _lastFilterClicked: MutableStateFlow<FilterType?> = MutableStateFlow(null)
    val lastFilterClicked = _lastFilterClicked.asStateFlow()
    private val _isConnectedToNetwork = MutableStateFlow(false)
    val state = combine(
        _companyFilter,
        _clientFilter,
        _branchFilter,
        _statusFilter,
        _query,
    ) { companyFilter, clientFilter, branchFilter, statusFilter, query ->
        createScreenState(query, companyFilter, clientFilter, branchFilter, statusFilter)
    }.combine(_dateFilter) { screenState, dateFilter ->
        screenState.copy(dateFilter = dateFilter)
    }.combine(_documents) { screenState, documents ->
        val filteredDocuments = filterDocuments(documents, screenState)
        screenState.copy(documents = filteredDocuments)
    }.combine(_isSyncing) { screenState, isSyncing ->
        screenState.copy(isSyncing = isSyncing)
    }.combine(_isConnectedToNetwork) { screenState, isConnectedToNetwork ->
        screenState.copy(isConnectedToNetwork = isConnectedToNetwork)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DocumentsScreenState.empty())

    private fun createScreenState(
        query: String,
        companyFilter: Company?,
        clientFilter: Client?,
        branchFilter: Branch?,
        statusFilter: DocumentStatus?
    ) = DocumentsScreenState(
        documents = _documents.value,
        query = query,
        isSyncing = _isSyncing.value,
        companyFilter = companyFilter,
        clientFilter = clientFilter,
        branchFilter = branchFilter,
        statusFilter = statusFilter,
        isConnectedToNetwork = _isConnectedToNetwork.value
    )

    private fun filterDocuments(
        documents: List<DocumentView>,
        screenState: DocumentsScreenState
    ) = documents.asSequence()
        .filter { document -> filterByQuery(document, screenState) }
        .filter { document -> filterByCompany(document, screenState) }
        .filter { document -> filterByClient(document, screenState) }
        .filter { document -> filterByBranch(document, screenState) }
        .filter { document -> filterByStatus(document, screenState) }
        .filter { document -> filterByDate(document, screenState) }
        .toList()

    fun setQuery(value: String) = viewModelScope.launch {
        _query.update { value }
    }

    fun setCompanyFilter(value: Company?) = viewModelScope.launch {
        _companyFilter.update { value }
    }

    fun setClientFilter(value: Client?) = viewModelScope.launch {
        _clientFilter.update { value }
    }

    fun setBranchFilter(value: Branch?) = viewModelScope.launch {
        _branchFilter.update { value }
    }

    fun setStatusFilter(value: DocumentStatus?) = viewModelScope.launch {
        _statusFilter.update { value }
    }

    fun setDateFilter(value: Date?) = viewModelScope.launch {
        _dateFilter.update { value }
    }

    fun getAvailableCompanies(): List<Company> {
        return _documents.value.map { it.company }.distinct().sortedBy { it.name }
    }

    fun getAvailableClients(): List<Client> {
        return _documents.value.map { it.client }.distinct().sortedBy { it.name }
    }

    fun getAvailableBranches(): List<Branch> {
        return _documents.value.map { it.branch }.distinct().sortedBy { it.name }
    }

    fun setLastFilterClicked(value: FilterType?) = viewModelScope.launch {
        _lastFilterClicked.update { value }
    }

    fun cancelDocument(documentId: String) = viewModelScope.launch {
        cancelDocumentUseCase(documentId)
    }

    fun syncDocumentsStatus() = viewModelScope.launch {
        _isSyncing.update { true }
        syncDocumentsStatusUseCase()
        _isSyncing.update { false }
    }

    init {
        viewModelScope.launch {
            getDocumentsUseCase().collectLatest {
                _documents.update { _ -> it }
            }
        }
        viewModelScope.launch {
            networkObserver.observeNetworkStatus().collectLatest {
                _isConnectedToNetwork.update { _ -> it == NetworkStatus.CONNECTED }
            }
        }
    }

    private fun filterByQuery(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        val queryMatches = buildList {
            add(documentView.client.name)
            add(documentView.branch.name)
            addAll(documentView.invoices.map { it.item.name })
        }
        return queryMatches.any { it.contains(state.query, true) }
    }

    private fun filterByDate(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        return if (state.dateFilter == null) true
        else {
            val documentDate = getDayFromDate(documentView.date)
            val filterDate = getDayFromDate(state.dateFilter)
            documentDate == filterDate
        }
    }

    private fun getDayFromDate(date: Date) = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

    private fun filterByCompany(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        return if (state.companyFilter != null)
            documentView.company.id == state.companyFilter.id
        else true
    }

    private fun filterByClient(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        return if (state.clientFilter == null) true
        else documentView.client.id == state.clientFilter.id
    }

    private fun filterByBranch(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        return if (state.branchFilter == null) true
        else documentView.branch.id == state.branchFilter.id

    }

    private fun filterByStatus(documentView: DocumentView, state: DocumentsScreenState): Boolean {
        return if (state.statusFilter == null) true
        else documentView.status == state.statusFilter
    }

}
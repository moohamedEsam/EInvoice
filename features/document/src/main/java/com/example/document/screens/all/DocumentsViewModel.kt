package com.example.document.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.domain.document.*
import com.example.domain.networkStatus.NetworkObserver
import com.example.domain.networkStatus.NetworkStatus
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.*

class DocumentsViewModel(
    private val getDocumentsPagingSourceUseCase: GetDocumentsPagingSourceUseCase,
    private val networkObserver: NetworkObserver,
    private val cancelDocumentUseCase: CancelDocumentUseCase,
    private val sendDocumentUseCase: SendDocumentUseCase,
    private val syncDocumentsStatusUseCase: SyncDocumentsStatusUseCase
) : ViewModel() {
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
    private val _documents = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { getDocumentsPagingSourceUseCase() }
    ).flow.cachedIn(viewModelScope)


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
        documents = PagingData.empty(),
        query = query,
        isSyncing = _isSyncing.value,
        companyFilter = companyFilter,
        clientFilter = clientFilter,
        branchFilter = branchFilter,
        statusFilter = statusFilter,
        isConnectedToNetwork = _isConnectedToNetwork.value
    )

    private fun filterDocuments(
        documents: PagingData<DocumentView>,
        screenState: DocumentsScreenState
    ) = documents
        .filter { document -> filterByQuery(document, screenState) }
        .filter { document -> filterByCompany(document, screenState) }
        .filter { document -> filterByClient(document, screenState) }
        .filter { document -> filterByBranch(document, screenState) }
        .filter { document -> filterByStatus(document, screenState) }
        .filter { document -> filterByDate(document, screenState) }


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

    fun setLastFilterClicked(value: FilterType?) = viewModelScope.launch {
        _lastFilterClicked.update { value }
    }

    fun cancelDocument(documentId: String) = viewModelScope.launch {
        cancelDocumentUseCase(documentId)
    }

    fun sendDocument(documentId: String) = viewModelScope.launch {
        sendDocumentUseCase(documentId)
    }

    fun syncDocumentsStatus() = viewModelScope.launch {
        _isSyncing.update { true }
        syncDocumentsStatusUseCase()
        _isSyncing.update { false }
    }

    init {
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
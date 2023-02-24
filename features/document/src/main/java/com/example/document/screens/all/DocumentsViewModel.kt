package com.example.document.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.domain.branch.GetBranchesPagingSourceUseCase
import com.example.domain.client.GetClientsPagingSourceUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.domain.document.*
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
    private val getDocumentsPagingSourceUseCase: GetDocumentsPagingSourceUseCase,
    getCompaniesUseCase: GetCompaniesUseCase,
    private val getBranchesPagingSourceUseCase: GetBranchesPagingSourceUseCase,
    private val getClientsPagingSourceUseCase: GetClientsPagingSourceUseCase,
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
    val availableCompanies =
        getCompaniesUseCase().map { PagingData.from(it) }.cachedIn(viewModelScope)

    val availableBranches = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { getBranchesPagingSourceUseCase() }
    ).flow.cachedIn(viewModelScope)

    val availableClients = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { getClientsPagingSourceUseCase() }
    ).flow.cachedIn(viewModelScope)

    private val filters = combine(
        _companyFilter,
        _clientFilter,
        _branchFilter,
        _statusFilter,
        _dateFilter
    ) { companyFilter, clientFilter, branchFilter, statusFilter, dateFilter ->
        DocumentsScreenState.Filters(
            company = companyFilter,
            client = clientFilter,
            branch = branchFilter,
            status = statusFilter,
            date = dateFilter
        )
    }.combine(_query) { filters, query -> filters.copy(query = query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DocumentsScreenState.Filters())

    val documents = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { getDocumentsPagingSourceUseCase() }
    ).flow.cachedIn(viewModelScope)
        .combine(filters) { documents, filters -> filterDocuments(documents, filters) }


    val state = combine(
        filters,
        _isSyncing,
        _isConnectedToNetwork
    ) { filters, isSyncing, isConnectedToNetwork ->
        DocumentsScreenState(
            filters = filters,
            isSyncing = isSyncing,
            isConnectedToNetwork = isConnectedToNetwork
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DocumentsScreenState.empty())

    private fun filterDocuments(
        documents: PagingData<DocumentView>,
        filters: DocumentsScreenState.Filters
    ) = documents
        .filter { document -> filterByQuery(document, filters.query) }
        .filter { document -> filters.company == null || document.company.id == filters.company.id }
        .filter { document -> filters.client == null || document.client.id == filters.client.id }
        .filter { document -> filters.branch == null || document.branch.id == filters.branch.id }
        .filter { document -> filters.status == null || document.status == filters.status }
        .filter { document -> filterByDate(document, filters.date) }


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

    private fun filterByQuery(documentView: DocumentView, query: String): Boolean {
        val queryMatches = buildList {
            add(documentView.client.name)
            add(documentView.branch.name)
            addAll(documentView.invoices.map { it.item.name })
        }
        return queryMatches.any { it.contains(query, true) }
    }

    private fun filterByDate(documentView: DocumentView, dateFilter: Date?): Boolean {
        return if (dateFilter == null) true
        else {
            val documentDate = getDayFromDate(documentView.date)
            val filterDate = getDayFromDate(dateFilter)
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
}
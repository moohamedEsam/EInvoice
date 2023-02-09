package com.example.document.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.invoiceLine.getTotals
import org.koin.androidx.compose.viewModel


@Composable
fun DocumentsScreen(
    onDocumentClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
) {
    val viewModel: DocumentsViewModel by viewModel()
    val state by viewModel.state.collectAsState()
    val lastFilterClicked by viewModel.lastFilterClicked.collectAsState()
    var showDialog by remember {
        mutableStateOf(false)
    }

    DocumentsScreenContent(
        state = state,
        onQueryChange = viewModel::setQuery,
        onDocumentClick = onDocumentClick,
        onAddDocumentClick = onAddDocumentClick,
        onSyncDocumentsClick = viewModel::syncDocumentsStatus,
        onFilterByCompanyClick = {
            if (state.companyFilter != null)
                viewModel.setCompanyFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.COMPANY)
        },
        onFilterByClientClick = {
            if (state.clientFilter != null)
                viewModel.setClientFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.CLIENT)
        },
        onFilterByBranchClick = {
            if (state.branchFilter != null)
                viewModel.setBranchFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.BRANCH)
        },
        onFilterByStatusClick = {
            if (state.statusFilter != null)
                viewModel.setStatusFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.STATUS)
        },
        onDocumentCancelClick = viewModel::cancelDocument,
    )
    if (showDialog && lastFilterClicked != null)
        ShowFilterDialog(
            companies = viewModel.getAvailableCompanies(),
            clients = viewModel.getAvailableClients(),
            branches = viewModel.getAvailableBranches(),
            onDismiss = { showDialog = false },
            onCompanySelected = viewModel::setCompanyFilter,
            onClientSelected = viewModel::setClientFilter,
            onBranchSelected = viewModel::setBranchFilter,
            onStatusSelected = viewModel::setStatusFilter,
            lastFilterClicked = lastFilterClicked!!
        )
}

@Composable
private fun DocumentsScreenContent(
    state: DocumentsScreenState,
    onQueryChange: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
    onSyncDocumentsClick: () -> Unit,
    onFilterByCompanyClick: () -> Unit,
    onFilterByClientClick: () -> Unit,
    onFilterByBranchClick: () -> Unit,
    onFilterByStatusClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchField(state, onQueryChange)
        ActionsRow(onSyncDocumentsClick, state)
        FiltersRow(
            state = state,
            onFilterByCompanyClick = onFilterByCompanyClick,
            onFilterByClientClick = onFilterByClientClick,
            onFilterByBranchClick = onFilterByBranchClick,
            onFilterByStatusClick = onFilterByStatusClick
        )
        DocumentsList(
            modifier = Modifier.weight(1f),
            onDocumentClick = onDocumentClick,
            documents = state.documents,
            onDocumentCancelClick = onDocumentCancelClick
        )
        CreateNewDocumentFloatingButton(
            onClick = onAddDocumentClick
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DocumentsList(
    modifier: Modifier = Modifier,
    onDocumentClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    documents: List<DocumentView>
) {
    Text(text = "Documents", style = MaterialTheme.typography.headlineSmall)
    LazyVerticalStaggeredGrid(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(documents) { document ->
            DocumentItem(
                document = document,
                onClick = { onDocumentClick(document.id) },
                onDocumentCancelClick = onDocumentCancelClick
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FiltersRow(
    state: DocumentsScreenState,
    onFilterByCompanyClick: () -> Unit,
    onFilterByClientClick: () -> Unit,
    onFilterByBranchClick: () -> Unit,
    onFilterByStatusClick: () -> Unit
) {
    Text(text = "Filters", style = MaterialTheme.typography.headlineSmall)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = state.companyFilter != null,
            onClick = onFilterByCompanyClick,
            label = { Text(state.companyFilter?.name ?: "Company") })
        FilterChip(
            selected = state.clientFilter != null,
            onClick = onFilterByClientClick,
            label = { Text(state.clientFilter?.name ?: "Client") })
        FilterChip(
            selected = state.branchFilter != null,
            onClick = onFilterByBranchClick,
            label = { Text(state.branchFilter?.name ?: "Branch") })
        FilterChip(
            selected = state.statusFilter != null,
            onClick = onFilterByStatusClick,
            label = { Text(state.statusFilter?.name ?: "Status") })

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ActionsRow(
    onSyncDocumentsClick: () -> Unit,
    state: DocumentsScreenState
) {
    Text(text = "Actions", style = MaterialTheme.typography.headlineSmall)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = onSyncDocumentsClick,
            label = { Text(text = "Sync Documents status") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Sync, contentDescription = null) },
            enabled = !state.isSyncing && state.isConnectedToNetwork
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchField(
    state: DocumentsScreenState,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = state.query,
        onValueChange = onQueryChange,
        label = { Text("Search") },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ColumnScope.CreateNewDocumentFloatingButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        content = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
        modifier = Modifier
            .align(Alignment.End)
            .padding(8.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentItem(
    document: DocumentView,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDocumentCancelClick: (String) -> Unit
) {
    val total = document.invoices.sumOf { it.getTotals().total }
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "from: ${document.company.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "to: ${document.client.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "branch: ${document.branch.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Row {
                Text("Status: ")
                Text(
                    text = document.status.name,
                    color = document.status.getStatusColor(MaterialTheme.colorScheme.onSecondaryContainer)
                )
            }
            Text(
                text = "invoices: ${document.invoices.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "total: %.2f $".format(total),
                style = MaterialTheme.typography.bodyMedium
            )
            if (document.status in listOf(
                    DocumentStatus.Valid,
                    DocumentStatus.Submitted,
                    DocumentStatus.Invalid
                )
            )
                AssistChip(
                    onClick = { onDocumentCancelClick(document.id) },
                    label = { Text("Cancel") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                )
        }
    }
}

@Composable
private fun ShowFilterDialog(
    companies: List<Company>,
    clients: List<Client>,
    branches: List<Branch>,
    onDismiss: () -> Unit,
    onCompanySelected: (Company) -> Unit,
    onClientSelected: (Client) -> Unit,
    onBranchSelected: (Branch) -> Unit,
    onStatusSelected: (DocumentStatus) -> Unit,
    lastFilterClicked: FilterType
) {
    when (lastFilterClicked) {
        FilterType.COMPANY -> {
            ShowCompanyFilterDialog(
                companies = companies,
                onDismiss = onDismiss,
                onCompanySelected = {
                    onCompanySelected(it)
                    onDismiss()
                }
            )
        }
        FilterType.CLIENT -> {
            ShowClientFilterDialog(
                clients = clients,
                onDismiss = onDismiss,
                onClientSelected = {
                    onClientSelected(it)
                    onDismiss()
                }
            )
        }
        FilterType.BRANCH -> {
            ShowBranchFilterDialog(
                branches = branches,
                onDismiss = onDismiss,
                onBranchSelected = {
                    onBranchSelected(it)
                    onDismiss()
                }
            )
        }
        FilterType.STATUS -> {
            ShowStatusFilterDialog(
                onDismiss = onDismiss,
                onStatusSelected = {
                    onStatusSelected(it)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun ShowCompanyFilterDialog(
    companies: List<Company>,
    onDismiss: () -> Unit,
    onCompanySelected: (Company) -> Unit
) {
    FilterDialog(
        items = companies,
        onItemSelect = onCompanySelected,
        setItemName = { it.name },
        onDismiss = onDismiss
    )
}

@Composable
private fun ShowClientFilterDialog(
    clients: List<Client>,
    onDismiss: () -> Unit,
    onClientSelected: (Client) -> Unit
) {
    FilterDialog(
        items = clients,
        onItemSelect = onClientSelected,
        setItemName = { it.name },
        onDismiss = onDismiss
    )
}

@Composable
private fun ShowBranchFilterDialog(
    branches: List<Branch>,
    onDismiss: () -> Unit,
    onBranchSelected: (Branch) -> Unit
) {
    FilterDialog(
        items = branches,
        onItemSelect = onBranchSelected,
        setItemName = { it.name },
        onDismiss = onDismiss
    )
}

@Composable
private fun ShowStatusFilterDialog(
    onDismiss: () -> Unit,
    onStatusSelected: (DocumentStatus) -> Unit
) {
    FilterDialog(
        items = DocumentStatus.values().toList(),
        onItemSelect = onStatusSelected,
        setItemName = { it.name },
        onDismiss = onDismiss
    )
}

@Preview(showBackground = true)
@Composable
fun DocumentsScreenPreview() {
    DocumentsScreenContent(
        state = DocumentsScreenState(
            documents = List(50) { DocumentView.empty().copy(status = DocumentStatus.Invalid) },
            query = "",
            isSyncing = false,
            companyFilter = null,
            clientFilter = null,
            branchFilter = null,
            statusFilter = null,
            isConnectedToNetwork = true,
        ),
        onQueryChange = {},
        onDocumentClick = {},
        onAddDocumentClick = {},
        onSyncDocumentsClick = {},
        onFilterByCompanyClick = {},
        onFilterByClientClick = {},
        onFilterByBranchClick = {},
        onDocumentCancelClick = {},
    ) {

    }
}
package com.example.document.screens.all

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.einvoicecomponents.datePickerColors
import com.example.einvoicecomponents.loadStateItem
import com.example.einvoicecomponents.toDate
import com.example.einvoicecomponents.toLocalDate
import com.example.functions.getStatusColor
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.invoiceLine.getTotals
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


@Composable
fun DocumentsScreen(
    onDocumentClick: (String) -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    onDocumentUpdateClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit
) {
    val viewModel: DocumentsViewModel by viewModel()
    val state by viewModel.state.collectAsState()
    val lastFilterClicked by viewModel.lastFilterClicked.collectAsState()
    var showDialog by remember {
        mutableStateOf(false)
    }

    DocumentsScreenContent(
        state = state,
        documentsFlow = viewModel.documents,
        onQueryChange = viewModel::setQuery,
        onDocumentClick = onDocumentClick,
        onAddDocumentClick = onAddDocumentClick,
        onSyncDocumentsClick = viewModel::syncDocumentsStatus,
        onFilterByCompanyClick = {
            if (state.filters.company != null)
                viewModel.setCompanyFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.COMPANY)
        },
        onFilterByClientClick = {
            if (state.filters.client != null)
                viewModel.setClientFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.CLIENT)
        },
        onFilterByBranchClick = {
            if (state.filters.branch != null)
                viewModel.setBranchFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.BRANCH)
        },
        onFilterByStatusClick = {
            if (state.filters.status != null)
                viewModel.setStatusFilter(null)
            else
                showDialog = true
            viewModel.setLastFilterClicked(FilterType.STATUS)
        },
        onDocumentCancelClick = viewModel::cancelDocument,
        onDocumentSendClick = viewModel::sendDocument,
        onDatePicked = viewModel::setDateFilter,
        onCreateCreditClick = onCreateCreditClick,
        onCreateDebitClick = onCreateDebitClick,
        onDocumentUpdateClick = onDocumentUpdateClick
    )
    if (showDialog && lastFilterClicked != null)
        ShowFilterDialog(
            companies = viewModel.availableCompanies,
            clients = viewModel.availableClients,
            branches = viewModel.availableBranches,
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
    documentsFlow: Flow<PagingData<DocumentView>>,
    onQueryChange: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    onDocumentSendClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
    onSyncDocumentsClick: () -> Unit,
    onFilterByCompanyClick: () -> Unit,
    onFilterByClientClick: () -> Unit,
    onFilterByBranchClick: () -> Unit,
    onFilterByStatusClick: () -> Unit,
    onDatePicked: (Date?) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onDocumentUpdateClick: (String) -> Unit
) {
    val simpleDateFormat by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
    }
    val documents = documentsFlow.collectAsLazyPagingItems()
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SearchField(state, onQueryChange) }
            item { ActionsRow(onSyncDocumentsClick, state) }
            item {
                FiltersRow(
                    state = state,
                    onFilterByCompanyClick = onFilterByCompanyClick,
                    onFilterByClientClick = onFilterByClientClick,
                    onFilterByBranchClick = onFilterByBranchClick,
                    onFilterByStatusClick = onFilterByStatusClick,
                    onDatePicked = onDatePicked,
                    dateFormat = simpleDateFormat
                )
            }

            documentsList(
                onDocumentClick = onDocumentClick,
                onDocumentCancelClick = onDocumentCancelClick,
                onCreateCreditClick = onCreateCreditClick,
                onDocumentUpdateClick = onDocumentUpdateClick,
                onCreateDebitClick = onCreateDebitClick,
                onDocumentSendClick = onDocumentSendClick,
                documents = documents,
                isConnectedToNetwork = state.isConnectedToNetwork
            )
        }

        FloatingActionButton(
            onClick = onAddDocumentClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add document")
        }
    }
}

private fun LazyListScope.documentsList(
    onDocumentClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onDocumentUpdateClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    onDocumentSendClick: (String) -> Unit,
    documents: LazyPagingItems<DocumentView>,
    isConnectedToNetwork: Boolean
) {

    item {
        Text(text = "Documents", style = MaterialTheme.typography.headlineSmall)
    }

    items(documents) { document ->
        DocumentItem(
            document = document ?: return@items,
            onClick = { onDocumentClick(document.id) },
            onDocumentCancelClick = onDocumentCancelClick,
            dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            onCreateCreditClick = onCreateCreditClick,
            onCreateDebitClick = onCreateDebitClick,
            onDocumentSendClick = onDocumentSendClick,
            isConnectedToNetwork = isConnectedToNetwork,
            onDocumentUpdateClick = onDocumentUpdateClick
        )
    }

    loadStateItem(documents)

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FiltersRow(
    state: DocumentsScreenState,
    onFilterByCompanyClick: () -> Unit,
    onFilterByClientClick: () -> Unit,
    onFilterByBranchClick: () -> Unit,
    onFilterByStatusClick: () -> Unit,
    onDatePicked: (Date?) -> Unit,
    dateFormat: SimpleDateFormat,
) {
    Text(text = "Filters", style = MaterialTheme.typography.headlineSmall)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = state.filters.company != null,
            onClick = onFilterByCompanyClick,
            label = { Text(state.filters.company?.name ?: "Company") })
        FilterChip(
            selected = state.filters.client != null,
            onClick = onFilterByClientClick,
            label = { Text(state.filters.client?.name ?: "Client") })
        FilterChip(
            selected = state.filters.branch != null,
            onClick = onFilterByBranchClick,
            label = { Text(state.filters.branch?.name ?: "Branch") })
        FilterChip(
            selected = state.filters.status != null,
            onClick = onFilterByStatusClick,
            label = { Text(state.filters.status?.name ?: "Status") })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateFilter(
                currentDate = state.filters.date,
                onDatePicked = onDatePicked,
                simpleDateFormat = dateFormat
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilter(
    currentDate: Date?,
    modifier: Modifier = Modifier,
    onDatePicked: (Date?) -> Unit,
    simpleDateFormat: SimpleDateFormat
) {
    val materialDatePickerState = rememberMaterialDialogState()
    FilterChip(
        selected = currentDate != null,
        onClick = {
            if (currentDate != null)
                onDatePicked(null)
            else
                materialDatePickerState.show()
        },
        label = { Text(if (currentDate != null) simpleDateFormat.format(currentDate) else "Date") },
        modifier = modifier
    )
    MaterialDialog(
        dialogState = materialDatePickerState,
        buttons = {
            positiveButton("Ok") {}
            negativeButton("Cancel") {}
        }
    ) {
        datepicker(
            initialDate = currentDate?.toLocalDate() ?: LocalDate.now(),
            onDateChange = {
                onDatePicked(it.toDate())
            },
            waitForPositiveButton = true,
            allowedDateValidator = {
                it.isBefore(LocalDate.now().plusDays(1))
            },
            colors = datePickerColors()
        )
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
        value = state.filters.query,
        onValueChange = onQueryChange,
        label = { Text("Search") },
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentItem(
    document: DocumentView,
    isConnectedToNetwork: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDocumentUpdateClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    onDocumentSendClick: (String) -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    dateFormat: SimpleDateFormat
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
                text = "company: ${document.company.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "client: ${document.client.name}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "branch: ${document.branch.name}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Divider()
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "invoices: ${document.invoices.size}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "total: %.2f $".format(total),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Divider()
            FlowRow(
                mainAxisSpacing = 4.dp,
                crossAxisSpacing = 4.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Status: ", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = document.status.name,
                        color = document.status.getStatusColor(MaterialTheme.colorScheme.onSecondaryContainer),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Text(
                    text = "date: ${dateFormat.format(document.date)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Divider()
            DocumentActionRow(
                document = document,
                onDocumentUpdateClick = onDocumentUpdateClick,
                onDocumentSendClick = onDocumentSendClick,
                isConnectedToNetwork = isConnectedToNetwork,
                onDocumentCancelClick = onDocumentCancelClick,
                onCreateCreditClick = onCreateCreditClick,
                onCreateDebitClick = onCreateDebitClick
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DocumentActionRow(
    document: DocumentView,
    onDocumentUpdateClick: (String) -> Unit,
    onDocumentSendClick: (String) -> Unit,
    isConnectedToNetwork: Boolean,
    onDocumentCancelClick: (String) -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (document.status.isUpdatable())
            AssistChip(
                onClick = { onDocumentUpdateClick(document.id) },
                label = { Text("Update") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null
                    )
                }
            )
        if (document.status.isSendable())
            AssistChip(
                onClick = { onDocumentSendClick(document.id) },
                label = { Text("Send") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = null
                    )
                },
                enabled = isConnectedToNetwork && document.isSynced
            )
        if (document.status.isCancelable())
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
                enabled = isConnectedToNetwork && document.isSynced
            )

        if (document.documentType != "I") return
        AssistChip(
            onClick = { onCreateCreditClick(document.id) },
            label = { Text("Create Credit") },
            enabled = document.status == DocumentStatus.Valid
        )

        AssistChip(
            onClick = { onCreateDebitClick(document.id) },
            label = { Text("Create Debit") },
            enabled = document.status == DocumentStatus.Valid
        )
    }
}

@Composable
private fun ShowFilterDialog(
    companies: Flow<PagingData<Company>>,
    clients: Flow<PagingData<Client>>,
    branches: Flow<PagingData<Branch>>,
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
    companies: Flow<PagingData<Company>>,
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
    clients: Flow<PagingData<Client>>,
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
    branches: Flow<PagingData<Branch>>,
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
        items = flowOf(PagingData.from(DocumentStatus.values().toList())),
        onItemSelect = onStatusSelected,
        setItemName = { it.name },
        onDismiss = onDismiss
    )
}

@Preview(showBackground = true)
@Composable
fun DocumentsScreenPreview() {
    DocumentsScreenContent(
        state = DocumentsScreenState.empty(),
        documentsFlow = flowOf(PagingData.from(List(50) { DocumentView.empty() })),
        onQueryChange = {},
        onDocumentClick = {},
        onAddDocumentClick = {},
        onSyncDocumentsClick = {},
        onFilterByCompanyClick = {},
        onFilterByClientClick = {},
        onFilterByBranchClick = {},
        onDocumentCancelClick = {},
        onDatePicked = {},
        onFilterByStatusClick = {},
        onCreateCreditClick = {},
        onCreateDebitClick = {},
        onDocumentSendClick = {},
        onDocumentUpdateClick = {},
    )
}
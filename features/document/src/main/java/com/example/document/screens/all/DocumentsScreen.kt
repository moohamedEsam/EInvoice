package com.example.document.screens.all

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.datePickerColors
import com.example.einvoicecomponents.toDate
import com.example.einvoicecomponents.toLocalDate
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
        onDocumentSendClick = viewModel::sendDocument,
        onDatePicked = viewModel::setDateFilter,
        onCreateCreditClick = onCreateCreditClick,
        onCreateDebitClick = onCreateDebitClick,
        onDocumentUpdateClick = onDocumentUpdateClick
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
            documents = state.documents,
            onDocumentCancelClick = onDocumentCancelClick,
            onAddDocumentClick = onAddDocumentClick,
            onCreateCreditClick = onCreateCreditClick,
            onCreateDebitClick = onCreateDebitClick,
            onDocumentSendClick = onDocumentSendClick,
            isConnectedToNetwork = state.isConnectedToNetwork,
            onDocumentUpdateClick = onDocumentUpdateClick
        )
    }
}

private fun LazyListScope.documentsList(
    onDocumentClick: (String) -> Unit,
    onDocumentCancelClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onDocumentUpdateClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    onDocumentSendClick: (String) -> Unit,
    documents: List<DocumentView>,
    isConnectedToNetwork: Boolean
) {
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Documents", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onAddDocumentClick) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add"
                )
            }
        }
    }

    items(documents) { document ->
        DocumentItem(
            document = document,
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateFilter(
                currentDate = state.dateFilter,
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
        value = state.query,
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
                        enabled = isConnectedToNetwork
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
                        enabled = isConnectedToNetwork
                    )

                if (document.documentType != "I") return@Row
                AssistChip(
                    onClick = { onCreateCreditClick(document.id) },
                    label = { Text("Create Credit") }
                )

                AssistChip(
                    onClick = { onCreateDebitClick(document.id) },
                    label = { Text("Create Debit") }
                )
            }
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
            documents = List(50) {
                DocumentView.empty().copy(status = DocumentStatus.Invalid, documentType = "I")
            },
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
        onDatePicked = {},
        onFilterByStatusClick = {},
        onCreateCreditClick = {},
        onCreateDebitClick = {},
        onDocumentSendClick = {},
        onDocumentUpdateClick = {},
    )
}
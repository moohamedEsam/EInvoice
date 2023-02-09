package com.example.document.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.document.screens.form.DocumentSummery
import com.example.document.screens.form.InvoiceLineItem
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.UnitValue
import com.example.models.invoiceLine.empty
import com.example.models.invoiceLine.getTotals
import com.example.models.item.Item
import com.example.models.item.empty
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun DocumentDetailsScreen(
    documentId: String,
    onCompanyClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onClientClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
) {
    val viewModel: DocumentDetailsViewModel by viewModel { parametersOf(documentId) }
    val document by viewModel.document.collectAsState()
    val isEditEnabled by viewModel.isEditEnabled.collectAsState()
    val showTaxDialog by viewModel.showTaxDialog.collectAsState()
    val invoiceLine by viewModel.invoiceLine.collectAsState()
    DocumentScreenContent(
        documentView = document,
        isEditEnabled = isEditEnabled,
        isDeleteEnabled = document.status < DocumentStatus.Submitted,
        onCompanyClick = { onCompanyClick(document.company.id) },
        onBranchClick = { onBranchClick(document.branch.id) },
        onClientClick = { onClientClick(document.client.id) },
        onEditClick = { onEditClick(document.id) },
        onDeleteClick = {
            viewModel.deleteDocument { result ->
                val event = if (result is Result.Success)
                    SnackBarEvent("Document Deleted Successfully", "Undo") {
                        viewModel.undoDeleteDocument {}
                    }
                else
                    SnackBarEvent((result as? Result.Error)?.exception ?: "Error Deleting Document")
                onShowSnackBarEvent(event)
            }
        },
        onShowTaxesClick = viewModel::onShowTaxesClick,
        onShowSnackBarEvent = onShowSnackBarEvent
    )
    if (showTaxDialog) {
        TaxesDialog(
            taxes = invoiceLine.taxes,
            onDismiss = viewModel::onTaxDialogDismiss,
            title = "Taxes for ${invoiceLine.item.name}",
            invoiceTotal = invoiceLine.getTotals().total
        )
    }
}

@Composable
private fun DocumentScreenContent(
    documentView: DocumentView,
    isEditEnabled: Boolean,
    isDeleteEnabled: Boolean,
    onCompanyClick: () -> Unit,
    onBranchClick: () -> Unit,
    onClientClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShowTaxesClick: (InvoiceLineView) -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    val simpleFormatter by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DocumentHeader(
            isEditEnabled = isEditEnabled,
            isDeleteEnabled = isDeleteEnabled,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
        DocumentInfo(
            onCompanyClick = onCompanyClick,
            documentView = documentView,
            onBranchClick = onBranchClick,
            onClientClick = onClientClick,
            simpleFormatter = simpleFormatter,
            onShowSnackBarEvent = onShowSnackBarEvent
        )

        DocumentInvoices(
            documentView = documentView,
            onShowTaxesClick = onShowTaxesClick,
            modifier = Modifier.weight(1f)
        )

        DocumentSummery(
            invoicesState = MutableStateFlow(documentView.invoices),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DocumentInvoices(
    documentView: DocumentView,
    onShowTaxesClick: (InvoiceLineView) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(text = "Invoices", style = MaterialTheme.typography.headlineMedium)
    LazyVerticalGrid(
        columns = GridCells.Adaptive(250.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(documentView.invoices) {
            InvoiceLineItem(
                invoiceLineView = it,
                modifier = Modifier.fillMaxWidth(),
                actionRow = {
                    AssistChip(
                        onClick = { onShowTaxesClick(it) },
                        label = { Text(text = "Show Taxes") }
                    )
                }
            )
        }
    }
}

@Composable
private fun DocumentInfo(
    onCompanyClick: () -> Unit,
    documentView: DocumentView,
    onBranchClick: () -> Unit,
    onClientClick: () -> Unit,
    simpleFormatter: SimpleDateFormat,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Company: ")
        TextButton(onClick = onCompanyClick) {
            Text(text = documentView.company.name)
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Branch: ")
        TextButton(onClick = onBranchClick) {
            Text(text = documentView.branch.name)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Client: ")
        TextButton(onClick = onClientClick) {
            Text(text = documentView.client.name)
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row{
            Text(text = "Status: ", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = documentView.status.name,
                color = documentView.status.getStatusColor(MaterialTheme.colorScheme.onSurface),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    if (documentView.error == null) return@clickable
                    val event = SnackBarEvent(documentView.error ?: "")
                    onShowSnackBarEvent(event)
                }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Document Type:${documentView.documentType}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Row {
        Text(text = "Internal Id: ${documentView.internalId}")
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Date: ${simpleFormatter.format(documentView.date)}")
    }
}

@Composable
private fun DocumentHeader(
    isEditEnabled: Boolean,
    isDeleteEnabled: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        Text(text = "Document Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onEditClick, enabled = isEditEnabled) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit"
            )
        }
        IconButton(onClick = onDeleteClick, enabled = isDeleteEnabled) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DocumentDetailsScreenPreview() {

    DocumentScreenContent(
        documentView = DocumentView(
            id = "1",
            branch = Branch.empty().copy(name = "Branch Name"),
            client = Client.empty().copy(name = "Client Name"),
            date = Date(),
            status = DocumentStatus.Valid,
            company = Company.empty().copy(name = "Company Name"),
            internalId = "2",
            documentType = "I",
            invoices = List(20) {
                InvoiceLineView.empty().copy(
                    item = Item.empty().copy(name = "Item Name $it"),
                    quantity = Random.nextInt(0, 200).toFloat(),
                    unitValue = UnitValue(
                        currencySold = "EUR",
                        currencyEgp = Random.nextInt(0, 200).toDouble(),
                    )
                )
            }
        ),
        onBranchClick = {},
        onClientClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onCompanyClick = {},
        onShowTaxesClick = {},
        isEditEnabled = true,
        isDeleteEnabled = true,
        onShowSnackBarEvent = {}
    )

}
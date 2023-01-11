package com.example.document.screens.details

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import com.example.document.screens.form.DocumentSummery
import com.example.document.screens.form.InvoiceLineItem
import com.example.functions.getStatusColor
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
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
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
            viewModel.deleteDocument()
        },
        onShowTaxesClick = viewModel::onShowTaxesClick
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
    onShowTaxesClick: (InvoiceLineView) -> Unit
) {
    val simpleFormatter by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
    }
    Column {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                DocumentHeader(
                    isEditEnabled = isEditEnabled,
                    isDeleteEnabled = isDeleteEnabled,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
            item {
                DocumentInfo(
                    onCompanyClick = onCompanyClick,
                    documentView = documentView,
                    onBranchClick = onBranchClick,
                    onClientClick = onClientClick,
                    simpleFormatter = simpleFormatter
                )
            }

            documentInvoices(
                documentView = documentView,
                onShowTaxesClick = onShowTaxesClick
            )

        }
        DocumentSummery(
            invoicesState = MutableStateFlow(documentView.invoices),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.documentInvoices(
    documentView: DocumentView,
    onShowTaxesClick: (InvoiceLineView) -> Unit
) {
    item {
        Text(text = "Invoices", style = MaterialTheme.typography.headlineMedium)
    }
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

@Composable
private fun DocumentInfo(
    onCompanyClick: () -> Unit,
    documentView: DocumentView,
    onBranchClick: () -> Unit,
    onClientClick: () -> Unit,
    simpleFormatter: SimpleDateFormat
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Company: ")
        TextButton(onClick = onCompanyClick) {
            Text(text = documentView.company.name)
        }
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.SpaceBetween,
    ) {
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
    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.SpaceBetween,
        crossAxisAlignment = FlowCrossAxisAlignment.Center
    ) {
        Row {
            Text(text = "Status: ", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = documentView.status.name,
                color = documentView.status.getStatusColor(MaterialTheme.colorScheme.onSurface),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = "Document Type:${documentView.documentType}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.SpaceBetween,
        crossAxisAlignment = FlowCrossAxisAlignment.Center
    ) {
        Text(text = "Internal Id: ${documentView.internalId}")
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
        isEditEnabled = true,
        isDeleteEnabled = true,
        onCompanyClick = {},
        onBranchClick = {},
        onClientClick = {},
        onEditClick = {},
        onDeleteClick = {}
    ) {}

}
package com.example.branch.screens.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.ChipCard
import com.example.einvoicecomponents.DocumentTransaction
import com.example.einvoicecomponents.InvoiceStartDatePicker
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.getTotals
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BranchGeneralPage(
    uiState: BranchDashboardState,
    onDocumentClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDatePicked: (Date) -> Unit,
) {
    val simpleDateFormat by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BranchHeaderRow(
            branchName = uiState.branchView.branch.name,
            isDeleteEnabled = uiState.isDeleteEnabled,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
        )
        BranchTransactionsOverview(uiState = uiState)
        InvoiceStartDatePicker(
            pickedDate = uiState.pickedDate,
            simpleDateFormatter = simpleDateFormat,
            onDatePicked = onDatePicked,
            minDate = uiState.documents.minOfOrNull { it.date } ?: Date(),
            maxDate = uiState.documents.maxOfOrNull { it.date } ?: Date(),
            modifier = Modifier.align(Alignment.End)
        )
        Transactions(
            documents = uiState.documents,
            onDocumentClick = onDocumentClick,
            simpleDateFormat = simpleDateFormat,
        )
    }
}

@Composable
private fun BranchHeaderRow(
    branchName: String,
    isDeleteEnabled: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = branchName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
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

@Composable
private fun BranchTransactionsOverview(
    uiState: BranchDashboardState
) {
    var totals by remember {
        mutableStateOf(getTotalSales(uiState))
    }
    LaunchedEffect(key1 = uiState.documents) {
        totals = getTotalSales(uiState)
    }

    val chips = buildList {
        add("items" to uiState.branchView.items.size.toString())
        add("documents" to uiState.documents.size.toString())
        add("Sales" to "%.2f $".format(totals))
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips) {
            ChipCard(
                mainLabel = it.first,
                supportingLabel = it.second,
                modifier = Modifier.widthIn(
                    min = (LocalConfiguration.current.screenWidthDp / 3).dp,
                )
            )
        }
    }
}

@Composable
private fun Transactions(
    documents: List<DocumentView>,
    onDocumentClick: (String) -> Unit,
    simpleDateFormat: SimpleDateFormat,
) {
    Text(
        text = "Transactions",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 16.dp)
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(documents) {
            DocumentTransaction(
                document = it,
                onClick = { onDocumentClick(it.id) },
                simpleDateFormatter = simpleDateFormat,
            )
        }
    }
}

private fun getTotalSales(uiState: BranchDashboardState) =
    uiState.documents.flatMap { it.invoices }.sumOf { it.getTotals().total }


@Preview(showBackground = true)
@Composable
fun BranchGeneralPagePreview() {
    BranchGeneralPage(
        onDeleteClick = {},
        uiState = BranchDashboardState.random(),
        onEditClick = {},
        onDocumentClick = {},
        onDatePicked = {}
    )
}
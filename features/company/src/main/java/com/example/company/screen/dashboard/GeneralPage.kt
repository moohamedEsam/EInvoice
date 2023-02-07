package com.example.company.screen.dashboard

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
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.Document
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.UnitValue
import com.example.models.invoiceLine.empty
import com.example.models.invoiceLine.getTotals
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun GeneralPage(
    state: CompanyDashboardState,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDocumentClick: (String) -> Unit,
    onDatePicked: (Date) -> Unit,
    modifier: Modifier = Modifier,
) {
    val simpleDateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PageHeader(
            company = state.companyView.company,
            onEditClick = onEditClick,
            isDeleteEnabled = state.isDeleteEnabled,
            onDeleteClick = onDeleteClick
        )
        InvoiceStartDatePicker(
            pickedDate = state.pickedDate,
            simpleDateFormatter = simpleDateFormatter,
            onDatePicked = onDatePicked,
            modifier = Modifier.align(Alignment.End),
            minDate = state.documents.minOfOrNull { it.date } ?: Date(),
            maxDate = state.documents.maxOfOrNull { it.date } ?: Date()
        )
        CompanyOverview(state.companyView, state.documents, state.invoices)
        CompanyTransactions(state.documents, simpleDateFormatter, onDocumentClick)
    }
}



@Composable
private fun PageHeader(
    company: Company,
    onEditClick: () -> Unit,
    isDeleteEnabled: Boolean,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = company.name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onEditClick) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
        }

        CompanyDeleteButton(
            isDeleteEnabled = isDeleteEnabled,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
private fun CompanyOverview(
    companyView: CompanyView,
    documents: List<DocumentView>,
    invoices: List<InvoiceLineView>
) {
    var invoicesTotals by remember {
        mutableStateOf(invoices.sumOf { it.getTotals().total })
    }
    LaunchedEffect(key1 = invoices) {
        invoicesTotals = invoices.sumOf { it.getTotals().total }
    }
    val chipValues = buildList {
        add(documents.count().toString() to "Invoices")
        add("%.2f $".format(invoicesTotals) to "Total")
        add(companyView.clients.count().toString() to "Clients")
        add(companyView.branches.count().toString() to "Branches")
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chipValues) {
            ChipCard(
                mainLabel = it.first,
                supportingLabel = it.second,
                modifier = Modifier.widthIn((LocalConfiguration.current.screenWidthDp / 3).dp)
            )
        }
    }
}


@Composable
private fun CompanyTransactions(
    documents: List<DocumentView>,
    simpleDateFormatter: SimpleDateFormat,
    onDocumentClick: (String) -> Unit
) {
    Text(
        text = "Transactions",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 16.dp)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(documents.sortedByDescending { it.date }) {
            DocumentTransaction(
                document = it,
                simpleDateFormatter = simpleDateFormatter,
                onClick = { onDocumentClick(it.id) }
            )
        }
    }

}


@Composable
private fun CompanyDeleteButton(
    isDeleteEnabled: Boolean,
    onDeleteClick: () -> Unit
) {
    IconButton(onClick = onDeleteClick, enabled = isDeleteEnabled) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralPagePreview() {
    GeneralPage(
        state = CompanyDashboardState(
            CompanyView(
                company = Company.empty().copy(name = "Company Name"),
                branches = List(Random.nextInt(0, 100)) { Branch.empty() },
                clients = List(Random.nextInt(0, 100)) { Client.empty() },
                documents = List(Random.nextInt(0, 100)) { Document.empty() },
            ),
            invoices = List(Random.nextInt(0, 100)) { randomInvoiceLineView() },
            documents = List(Random.nextInt(0, 100)) {
                DocumentView(
                    id = it.toString(),
                    branch = Branch.empty().copy(name = "Branch $it"),
                    client = Client.empty().copy(name = "Client $it"),
                    company = Company.empty(),
                    internalId = it.toString(),
                    documentType = "I",
                    date = Date(),
                    invoices = List(Random.nextInt(0, 100)) { randomInvoiceLineView() }
                )
            },
            isDeleteEnabled = true,
            pickedDate = Date()
        ),
        onDeleteClick = {},
        onEditClick = {},
        onDocumentClick = {},
        onDatePicked = {}
    )
}

@Composable
private fun randomInvoiceLineView() = InvoiceLineView.empty().copy(
    quantity = Random.nextInt(0, 100).toFloat(),
    unitValue = UnitValue(
        currencyEgp = Random.nextInt(0, 100).toDouble(),
        currencySold = "EGP"
    )
)
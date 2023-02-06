package com.example.company.screen.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.Document
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.empty
import com.example.models.invoiceLine.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun GeneralPage(
    state: CompanyDashboardState,
    onDeleteClick: () -> Unit,
    isDeleteEnabledState: MutableStateFlow<Boolean>,
    onEditClick: () -> Unit,
    onDocumentClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val simpleDateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    Column(
        modifier = modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PageHeader(state.companyView.company, onEditClick, isDeleteEnabledState, onDeleteClick)
        InvoiceStartDatePicker(simpleDateFormatter, modifier = Modifier.align(Alignment.End))
        CompanyOverview(state.companyView, state.invoices)
        CompanyTransactions(state.documents, simpleDateFormatter, onDocumentClick)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InvoiceStartDatePicker(
    simpleDateFormatter: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text(simpleDateFormatter.format(Date())) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = "Delete"
            )
        },
        modifier = modifier
    )
}

@Composable
private fun PageHeader(
    company: Company,
    onEditClick: () -> Unit,
    isDeleteEnabledState: MutableStateFlow<Boolean>,
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
            isDeleteEnabledState = isDeleteEnabledState,
            onDeleteClick = onDeleteClick
        )
    }
}

@Composable
private fun CompanyOverview(companyView: CompanyView, invoices: List<InvoiceLineView>) {
    val invoicesTotals by remember {
        mutableStateOf(invoices.sumOf { it.getTotals().total })
    }
    val chipValues = buildList {
        add(companyView.documents.count().toString() to "Invoices")
        add("%.2f $".format(invoicesTotals) to "Total")
        add(companyView.clients.count().toString() to "Clients")
        add(companyView.branches.count().toString() to "Branches")
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chipValues) {
            ChipCard(
                it.first,
                it.second,
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
            CompanyTransaction(
                document = it,
                simpleDateFormatter = simpleDateFormatter,
                onClick = { onDocumentClick(it.id) }
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyTransaction(
    document: DocumentView,
    modifier: Modifier = Modifier,
    simpleDateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    val total by remember {
        mutableStateOf(document.invoices.sumOf { it.getTotals().total })
    }
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = document.client.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = document.branch.name, style = MaterialTheme.typography.bodySmall)
            }

            Text(text = document.status.toString(), style = MaterialTheme.typography.bodyLarge)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "%.2f $".format(total), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = simpleDateFormatter.format(document.date),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ChipCard(mainLabel: String, supportingLabel: String, modifier: Modifier = Modifier) {
    OutlinedCard {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = supportingLabel)
            Text(
                text = mainLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CompanyDeleteButton(
    isDeleteEnabledState: MutableStateFlow<Boolean>,
    onDeleteClick: () -> Unit
) {
    val isDeleteEnabled by isDeleteEnabledState.collectAsState()
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
            }
        ),
        onDeleteClick = {},
        onEditClick = {},
        isDeleteEnabledState = MutableStateFlow(true),
        onDocumentClick = {}
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
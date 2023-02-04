package com.example.document.screens.form

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.OutlinedSearchTextField
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.InvoiceTax
import com.example.models.invoiceLine.empty
import com.example.models.item.Item
import com.example.models.item.empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InvoicesTaxesPage(
    invoicesState: StateFlow<List<InvoiceLineView>>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    taxMapper: (InvoiceTax) -> Pair<String, String>,
    onRemoveTax: (InvoiceLineView, InvoiceTax) -> Unit,
    onAddTax: (InvoiceLineView) -> Unit,
    onEditTax: (InvoiceLineView, InvoiceTax) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val invoices by invoicesState.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedSearchTextField(
            queryState = queryState,
            onQueryChange = onQueryChange,
            label = "Search By Tax Code",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 4.dp, end = 4.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
            state = listState,
        ) {
            invoices.forEach { invoice ->
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = invoice.item.name,
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        IconButton(onClick = { onAddTax(invoice) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add tax",
                            )
                        }
                    }
                }

                items(invoice.taxes) { tax ->
                    InvoiceTaxItem(
                        tax = tax,
                        onEditTax = { onEditTax(invoice, tax) },
                        onRemoveTax = { onRemoveTax(invoice, tax) },
                        mapper = taxMapper
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceTaxItem(
    tax: InvoiceTax,
    onEditTax: () -> Unit,
    onRemoveTax: () -> Unit,
    mapper: (InvoiceTax) -> Pair<String, String>
) {
    val convertResult by remember {
        mutableStateOf(mapper(tax))
    }
    val taxSubTypeName by remember {
        derivedStateOf {
            "${convertResult.second} (${tax.taxSubTypeCode})"
        }
    }
    val taxTypeName by remember {
        derivedStateOf {
            "${convertResult.first} (${tax.taxTypeCode})"
        }
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text = taxSubTypeName, style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = taxTypeName, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "rate: %.2f".format(tax.rate) + " %",
                style = MaterialTheme.typography.bodyLarge
            )

        }
        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = onEditTax,
                label = { Text(text = "Edit") },
            )

            AssistChip(
                onClick = onRemoveTax,
                label = { Text(text = "Remove") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.error,
                )
            )
        }
        Divider()
    }
}


@Preview(showBackground = true)
@Composable
fun InvoicesTaxesPagePreview() {
    val invoicesState = MutableStateFlow(
        List(10) {
            InvoiceLineView.empty().copy(
                item = Item.empty().copy(name = "Item $it"),
                taxes = List(Random.nextInt(1, 20)) {
                    InvoiceTax(
                        Random.nextInt(0, 100).toFloat(),
                        "code",
                        "sub code"
                    )
                }
            )
        }
    )
    InvoicesTaxesPage(
        invoicesState = invoicesState,
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onRemoveTax = { _, _ -> },
        onAddTax = {},
        onEditTax = { _, _ -> },
        taxMapper = { tax ->
            Pair(tax.taxTypeCode, tax.taxSubTypeCode)
        }
    )
}
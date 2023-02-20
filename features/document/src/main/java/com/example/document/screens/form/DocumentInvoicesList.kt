package com.example.document.screens.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.textField.OutlinedSearchTextField
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import com.example.models.item.empty
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocumentInvoicesList(
    invoicesState: StateFlow<List<InvoiceLineView>>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onInvoiceRemove: (InvoiceLineView) -> Unit,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onShowItemTaxes: (InvoiceLineView) -> Unit,
    onAddClick: () -> Unit,
    onAddTaxClick: (InvoiceLineView) -> Unit,
    modifier: Modifier = Modifier
) {
    val invoices by invoicesState.collectAsState()
    val columnState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedSearchTextField(
            queryState = queryState,
            onQueryChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Search by Item Name"
        )
        LazyColumn(
            modifier = modifier
                .weight(1f)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = columnState
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Invoices", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add invoice"
                        )
                    }
                }
            }
            items(invoices, key = { it.id }) { invoice ->
                InvoiceLineItem(
                    invoiceLineView = invoice,
                    modifier = Modifier.animateItemPlacement(),
                    actionRow = {
                        InvoiceLineItemActions(
                            onAddTaxClick = onAddTaxClick,
                            invoice = invoice,
                            onInvoiceEdit = onInvoiceEdit,
                            onShowItemTaxes = onShowItemTaxes,
                            onInvoiceRemove = onInvoiceRemove
                        )
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InvoiceLineItemActions(
    onAddTaxClick: (InvoiceLineView) -> Unit,
    invoice: InvoiceLineView,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onShowItemTaxes: (InvoiceLineView) -> Unit,
    onInvoiceRemove: (InvoiceLineView) -> Unit
) {
    AssistChip(
        onClick = { onAddTaxClick(invoice) },
        label = { Text(text = "Add Tax") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Tax"
            )
        }
    )

    AssistChip(
        onClick = { onInvoiceEdit(invoice) },
        label = { Text("Edit") },
        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
    )

    AssistChip(
        onClick = { onShowItemTaxes(invoice) },
        label = { Text(text = "Show Taxes") },
    )

    AssistChip(
        onClick = { onInvoiceRemove(invoice) },
        label = { Text("Delete") },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.error
        ),
        leadingIcon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        },
    )
}

@Composable
fun InvoiceLineItem(
    invoiceLineView: InvoiceLineView,
    modifier: Modifier = Modifier,
    actionRow: @Composable () -> Unit = {}
) {
    val totals = invoiceLineView.getTotals()

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = invoiceLineView.item.name, style = MaterialTheme.typography.headlineSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                Text("Quantity: ${invoiceLineView.quantity}")
                Text("Price: %.2f".format(invoiceLineView.unitValue.currencyEgp))
            }
            Divider()
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                Text("Discount: ${invoiceLineView.discountRate}%")
                Text("Total: %.2f".format(totals.discount))
            }
            Divider()
            Text(text = "Totals", style = MaterialTheme.typography.bodyLarge)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                Text("Tax Total: %.2f".format(totals.taxes))
                Text("Total: %.2f".format(totals.total))
            }
            Divider()

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actionRow()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DocumentInvoicesListPreview() {
    val invoiceLineView = List(20) {
        InvoiceLineView.empty()
            .copy(
                item = Item.empty().copy(name = "item"),
                taxes = List(10) { InvoiceTax(Random.nextInt(0, 99).toFloat(), "V001", "T1") },
                quantity = Random.nextInt(1, 2000).toFloat(),
                unitValue = UnitValue(
                    Random.nextDouble(5000.0),
                    ""
                ),
                discountRate = Random.nextInt(1, 100).toFloat(),
                id = it.toString()
            )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DocumentInvoicesList(
            onInvoiceRemove = {},
            onInvoiceEdit = {},
            invoicesState = MutableStateFlow(invoiceLineView),
            modifier = Modifier.fillMaxSize(),
            onAddTaxClick = {},
            onAddClick = {},
            onShowItemTaxes = {},
            queryState = MutableStateFlow(""),
            onQueryChange = {}
        )
    }
}
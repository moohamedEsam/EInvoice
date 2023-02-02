package com.example.document.screens.form

import androidx.compose.animation.animateContentSize
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
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.BaseExposedDropDownMenu
import com.example.einvoicecomponents.ValidationOutlinedTextField
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import com.example.models.item.empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

@Composable
fun DocumentInvoicesList(
    invoicesState: StateFlow<List<InvoiceLineView>>,
    onInvoiceRemove: (InvoiceLineView) -> Unit,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onAddClick: () -> Unit,
    onAddTaxClick: (InvoiceLineView) -> Unit,
    modifier: Modifier = Modifier
) {
    val invoices by invoicesState.collectAsState()
    val columnState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = columnState,
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

        items(invoices) { invoice ->
            InvoiceLineItem(
                invoiceLineView = invoice,
                onInvoiceEdit = {
                    onInvoiceEdit(invoice)
                },
                onInvoiceDelete = onInvoiceRemove,
                onAddTaxClick = onAddTaxClick,
                modifier = Modifier.heightIn(max = 280.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceLineItem(
    invoiceLineView: InvoiceLineView,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onInvoiceDelete: (InvoiceLineView) -> Unit,
    onAddTaxClick: (InvoiceLineView) -> Unit,
    modifier: Modifier = Modifier
) {
    val totals by remember {
        mutableStateOf(invoiceLineView.getTotals())
    }
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = invoiceLineView.item.name, style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quantity: ${invoiceLineView.quantity}")
                Text("Price: %.2f".format(invoiceLineView.unitValue.currencyEgp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Discount: ${invoiceLineView.discountRate}%")
                Text("Total: %.2f".format(totals.discount))
            }

            Text(text = "Totals", style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tax Total: %.2f".format(totals.taxes))
                Text("Total: %.2f".format(totals.total))
            }
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AssistChip(
                    onClick = { onAddTaxClick(invoiceLineView) },
                    label = { Text(text = "Add Tax") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Tax"
                        )
                    }
                )


                AssistChip(
                    onClick = { onInvoiceEdit(invoiceLineView) },
                    label = { Text("Edit") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                )

                AssistChip(
                    onClick = { onInvoiceDelete(invoiceLineView) },
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
        }
    }
}


@Composable
private fun EmptyInvoiceLineItem(
    itemsState: StateFlow<List<Item>>,
    itemState: StateFlow<Item?>,
    onItemChange: (Item) -> Unit,
    quantityState: StateFlow<String>,
    quantityValidationResult: StateFlow<ValidationResult>,
    onQuantityChange: (String) -> Unit,
    unitValueState: StateFlow<String>,
    unitValueValidationResult: StateFlow<ValidationResult>,
    onUnitValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    OutlinedCard {
        Column(modifier = Modifier.padding(8.dp)) {
            BaseExposedDropDownMenu(
                optionsState = itemsState,
                selectedOptionState = itemState,
                onOptionSelect = onItemChange,
                textFieldValue = { it?.name ?: "" },
                textFieldLabel = "Item",
                optionsLabel = { it.name },
            )

            ValidationOutlinedTextField(
                valueState = quantityState,
                validationState = quantityValidationResult,
                label = "Quantity",
                onValueChange = onQuantityChange,
            )

            ValidationOutlinedTextField(
                valueState = unitValueState,
                validationState = unitValueValidationResult,
                label = "Price",
                onValueChange = onUnitValueChange,
            )

            Button(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Add")
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
                discountRate = Random.nextInt(1, 100).toFloat()
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
            onAddClick = {}
        )
    }
}
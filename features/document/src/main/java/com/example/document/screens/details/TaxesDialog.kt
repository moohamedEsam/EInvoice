package com.example.document.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.models.invoiceLine.InvoiceTax

@Composable
fun TaxesDialog(
    title: String,
    invoiceTotal: Double,
    taxes: List<InvoiceTax>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = title)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(taxes) { tax ->
                        TaxItem(tax = tax, invoiceTotal = invoiceTotal)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxItem(
    tax: InvoiceTax,
    invoiceTotal: Double
) {
    OutlinedCard {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Tax: ${tax.taxSubTypeCode} - ${tax.taxTypeCode}")
            Text(text = "Tax Rate: ${tax.rate}%")
            Text(text = "Tax Total: ${tax.rate * invoiceTotal / 100}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaxesDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

    }
    TaxesDialog(
        title = "item 1 taxes",
        invoiceTotal = 150.0,
        taxes = listOf(
            InvoiceTax(
                taxSubTypeCode = "VAT",
                taxTypeCode = "VAT",
                rate = 20.0f
            ),
            InvoiceTax(
                taxSubTypeCode = "VAT",
                taxTypeCode = "VAT",
                rate = 20.0f
            ),
        )
    ) {

    }
}
package com.example.document.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.getTotals
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DocumentSummery(
    invoicesState: StateFlow<List<InvoiceLineView>>,
    modifier: Modifier = Modifier
) {
    val invoices by invoicesState.collectAsState()
    val invoicesTotals by remember {
        derivedStateOf {
            invoices.map { it.getTotals() }
        }
    }
    Text(text = "Summery", style = MaterialTheme.typography.headlineSmall)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Total: %.3f".format(invoicesTotals.sumOf { it.total }))
        Text("Tax Totals: %.3f".format(invoicesTotals.sumOf { it.taxes }))
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Net: %.3f".format(invoicesTotals.sumOf { it.net }))
        Text("Discount Totals: %.3f".format(invoicesTotals.sumOf { it.discount }))
    }


}
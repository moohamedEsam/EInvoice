package com.example.document.screens.form

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.getTotals
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
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
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        mainAxisAlignment = MainAxisAlignment.SpaceBetween,
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp,
    ) {
        Text("Total: %.3f".format(invoicesTotals.sumOf { it.total }))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Tax Totals: %.3f".format(invoicesTotals.sumOf { it.taxes }))
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        mainAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.SpaceBetween,
        crossAxisSpacing = 8.dp,
    ) {
        Text("Net: %.3f".format(invoicesTotals.sumOf { it.net }))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Discount Totals: %.3f".format(invoicesTotals.sumOf { it.discount }))
    }

}
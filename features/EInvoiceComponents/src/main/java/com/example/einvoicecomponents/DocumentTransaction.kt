package com.example.einvoicecomponents

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.getTotals
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentTransaction(
    document: DocumentView,
    modifier: Modifier = Modifier,
    simpleDateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    val total = document.invoices.sumOf { it.getTotals().total }


    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = document.status.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = document.status.getStatusColor(MaterialTheme.colorScheme.onSurface)
            )
            Spacer(modifier = Modifier.width(8.dp))
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
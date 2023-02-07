package com.example.einvoicecomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.item.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: Item,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = item.description, maxLines = 2)
            Text(text = "price: ${item.price}")
            Text(text = "taxStatus: ${item.status}")
            Text(text = "itemCode: ${item.itemCode}")
            Text(text = "unitTypeCode: ${item.unitTypeCode}")
        }
    }
}
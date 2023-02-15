package com.example.einvoicecomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.item.Item
import com.example.models.item.empty
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                crossAxisSpacing = 8.dp
            ) {
                Text(text = "price: ${item.price}")
                Text(text = "taxStatus: ${item.status}")
            }
            Divider()
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                crossAxisSpacing = 8.dp
            ) {
                Text(text = "itemCode: ${item.itemCode}")
                Text(text = "unitTypeCode: ${item.unitTypeCode}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardPreview() {
    ItemCard(item = Item.empty().copy(name = "item")) {}
}
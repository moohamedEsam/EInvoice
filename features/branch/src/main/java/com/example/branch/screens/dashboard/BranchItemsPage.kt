package com.example.branch.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.ItemCard
import com.example.models.item.Item

@Composable
fun ItemsPage(
    items: List<Item>,
    onItemClicked: (String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClicked(item.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
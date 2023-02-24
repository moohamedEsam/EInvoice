package com.example.document.screens.all

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> FilterDialog(
    items: Flow<PagingData<T>>,
    onItemSelect: (T) -> Unit,
    setItemName: (T) -> String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier=Modifier.fillMaxHeight(0.5f)) {
            val options = items.collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { item ->
                    if (item == null) return@items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemSelect(item) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = setItemName(item),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Divider()
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentsScreenFilterDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

    }

    FilterDialog(
        items = flowOf(PagingData.from(List(50) { it.toString() })),
        onItemSelect = {},
        setItemName = { it }
    ) {

    }
}
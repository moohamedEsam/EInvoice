package com.example.document.screens.all

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> FilterDialog(
    items: List<T>,
    onItemSelect: (T) -> Unit,
    setItemName: (T) -> String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
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
        items = List(50) { it.toString() },
        onItemSelect = {},
        setItemName = { it }
    ) {

    }
}
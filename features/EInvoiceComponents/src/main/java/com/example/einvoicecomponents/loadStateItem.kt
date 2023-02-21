package com.example.einvoicecomponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

fun LazyListScope.loadStateItem(state: LazyPagingItems<*>) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (state.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
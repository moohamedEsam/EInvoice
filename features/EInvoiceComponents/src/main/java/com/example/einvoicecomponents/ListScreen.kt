package com.example.einvoicecomponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.textField.OutlinedSearchTextField
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ListScreenContent(
    modifier: Modifier = Modifier,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    floatingButtonText: String,
    onFloatingButtonClick: () -> Unit,
    listContent: LazyListScope.() -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val nestedScrollConnection = object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    expanded = consumed.y < 0f || available.y < 0f
                    return super.onPostScroll(consumed, available, source)
                }
            }
            OutlinedSearchTextField(
                queryState = queryState,
                onQueryChange = onQueryChange,
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(nestedScrollConnection),
            ) {
                listContent()
            }
        }
        CreateNewItemFloatingButton(
            onCreateNewCompanyClick = onFloatingButtonClick,
            label = floatingButtonText,
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
            isExpanded = { expanded }
        )
    }
}

@Composable
private fun CreateNewItemFloatingButton(
    onCreateNewCompanyClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isExpanded: () -> Boolean
) {
    ExtendedFloatingActionButton(
        onClick = onCreateNewCompanyClick,
        modifier = modifier,
        text = { Text(label) },
        icon = { Icon(Icons.Filled.Add, contentDescription = label) },
        expanded = isExpanded()
    )
}
package com.example.einvoicecomponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListScreenContent(
    modifier: Modifier = Modifier,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    floatingButtonText: String,
    adaptiveItemSize: Dp,
    onFloatingButtonClick: () -> Unit,
    listContent: LazyStaggeredGridScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
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
        LazyVerticalStaggeredGrid(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .nestedScroll(nestedScrollConnection),
            columns = StaggeredGridCells.Adaptive(adaptiveItemSize),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listContent()
        }
        CreateNewCompanyFloatingButton(onFloatingButtonClick, floatingButtonText) {
            expanded
        }
    }
}

@Composable
private fun ColumnScope.CreateNewCompanyFloatingButton(
    onCreateNewCompanyClick: () -> Unit,
    label: String,
    isExpanded: () -> Boolean
) {
    ExtendedFloatingActionButton(
        onClick = onCreateNewCompanyClick,
        modifier = Modifier.align(Alignment.End),
        text = { Text(label) },
        icon = { Icon(Icons.Filled.Add, contentDescription = label) },
        expanded = isExpanded()
    )
}
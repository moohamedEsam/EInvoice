package com.example.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedSearchTextField(
    queryState: StateFlow<String>,
    modifier: Modifier = Modifier,
    label: String = "Search",
    onQueryChange: (String) -> Unit
) {
    val value by queryState.collectAsState()
    OutlinedTextField(
        value = value,
        onValueChange = onQueryChange,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = label
            )
        }
    )
}
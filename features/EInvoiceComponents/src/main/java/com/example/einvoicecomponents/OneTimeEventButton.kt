package com.example.einvoicecomponents

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ColumnScope.OneTimeEventButton(
    enabled: StateFlow<Boolean>,
    loading: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit = {}
) {
    val isEnabled by enabled.collectAsState()
    val isLoading by loading.collectAsState()
    if (isLoading)
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    else
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = modifier,
            shape = MaterialTheme.shapes.small
        ) {
            Text(label, textAlign = TextAlign.Center)
        }

}
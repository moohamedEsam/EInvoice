package com.example.einvoicecomponents

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OneTimeEventFloatingButton(
    enabled: StateFlow<Boolean>,
    loading: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    val isEnabled by enabled.collectAsState()
    val isLoading by loading.collectAsState()
    FloatingActionButton(
        onClick = {
            if (isEnabled)
                onClick()
        },
        modifier = modifier,
    ) {
        if (isLoading)
            CircularProgressIndicator()
        else
            Text(label, textAlign = TextAlign.Center)
    }

}
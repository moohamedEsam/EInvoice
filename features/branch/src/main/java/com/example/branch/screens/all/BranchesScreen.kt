package com.example.branch.screens.all

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BranchesScreen(
    snackbarHostState: SnackbarHostState,
    onCreateBranchClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ExtendedFloatingActionButton(
            onClick = onCreateBranchClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create branch"
                )
            },
            text = { Text(text = "Create branch") }
        )
    }
}
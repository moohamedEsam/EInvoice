package com.example.einvoice.presentation.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EInvoiceLayout(startScreen: String) {
    val snackbarHostState by remember {
        mutableStateOf(SnackbarHostState())
    }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = {
            EInvoiceNavGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
                paddingValues = it,
                startScreen = startScreen
            )
        }
    )
}
package com.example.einvoice.presentation.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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
        },
        topBar = {
            EInvoiceTopBar(
                navController = navController
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EInvoiceTopBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: return
    CenterAlignedTopAppBar(
        title = {
            Text(text = currentRoute)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (navController.backQueue.isNotEmpty()) {
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }

        }
    )
}

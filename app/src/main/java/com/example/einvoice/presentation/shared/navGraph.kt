package com.example.einvoice.presentation.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.auth.presentation.screens.login.LoginScreen
import com.example.einvoice.R

@Composable
fun EInvoiceNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LoginScreen,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(LoginScreen) {
            LoginScreen(
                snackbarHostState = snackbarHostState,
                logo = R.drawable.invoice,
                onLoggedIn = {},
                onRegisterClick = {}
            )
        }
    }
}
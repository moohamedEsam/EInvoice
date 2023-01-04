package com.example.einvoice.presentation.shared

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.auth.login.loginScreen
import com.example.auth.register.navigateToRegister
import com.example.auth.register.registerScreen
import com.example.company.screen.companiesScreen
import com.example.einvoice.R

@Composable
fun EInvoiceNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    startScreen: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier.padding(paddingValues)
    ) {
        loginScreen(
            snackbarHostState = snackbarHostState,
            logo = R.drawable.invoice,
            onLoggedIn = {},
            onRegisterClick = navController::navigateToRegister
        )

        registerScreen(
            logo = R.drawable.invoice,
            snackbarHostState = snackbarHostState,
            onRegistered = { },
            onLoginClick = navController::popBackStack
        )

        companiesScreen(snackbarHostState) {

        }
    }
}
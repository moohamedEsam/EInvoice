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
import com.example.branch.screens.all.branchesScreen
import com.example.branch.screens.form.branchFormScreen
import com.example.branch.screens.form.navigateToBranchFormScreen
import com.example.company.screen.all.companiesScreen
import com.example.einvoice.R
import com.example.maplocation.mapScreen
import com.example.maplocation.navigateToMapScreen

@Composable
fun EInvoiceNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    startScreen: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier
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

        branchFormScreen(snackbarHostState) {
            navController.navigateToMapScreen()
        }

        mapScreen { lat, lng ->
            navController.previousBackStackEntry?.arguments?.putDouble("lat", lat)
            navController.previousBackStackEntry?.arguments?.putDouble("lng", lng)
            navController.popBackStack()
        }

        branchesScreen(
            snackbarHostState,
            onCreateBranchClick = navController::navigateToBranchFormScreen
        )
    }
}
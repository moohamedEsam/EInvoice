package com.example.auth.register

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val RegisterScreenRoute = "RegisterScreen"

fun NavHostController.navigateToRegister() {
    navigate(RegisterScreenRoute)
}

fun NavGraphBuilder.registerScreen(
    logo: Any,
    snackbarHostState: SnackbarHostState,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    composable(RegisterScreenRoute) {
        RegisterScreen(
            logo = logo,
            snackbarHostState = snackbarHostState,
            onRegistered = onRegistered,
            onLoginClick = onLoginClick
        )
    }
}

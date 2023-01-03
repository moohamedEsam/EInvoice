package com.example.auth.register

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val RegisterScreen = "RegisterScreen"

fun NavHostController.navigateToRegister() {
    navigate(RegisterScreen)
}

fun NavGraphBuilder.registerScreen(
    logo: Any,
    snackbarHostState: SnackbarHostState,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    composable(RegisterScreen) {
        RegisterScreen(
            logo = logo,
            snackbarHostState = snackbarHostState,
            onRegistered = onRegistered,
            onLoginClick = onLoginClick
        )
    }
}

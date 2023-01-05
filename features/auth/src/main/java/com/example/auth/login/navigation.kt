package com.example.auth.login

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val LoginScreenRoute = "login"

fun NavHostController.navigateToLoginScreen() {
    navigate(LoginScreenRoute) {
        popUpTo(LoginScreenRoute) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.loginScreen(
    snackbarHostState: SnackbarHostState,
    logo: Any,
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit
) {
    composable(LoginScreenRoute) {
        LoginScreen(
            snackbarHostState = snackbarHostState,
            logo = logo,
            onLoggedIn = onLoggedIn,
            onRegisterClick = onRegisterClick
        )
    }
}
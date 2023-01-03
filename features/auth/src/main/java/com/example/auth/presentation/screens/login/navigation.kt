package com.example.auth.presentation.screens.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val LoginScreen = "login"

fun NavHostController.navigateToLoginScreen() {
    navigate(LoginScreen) {
        popUpTo(LoginScreen) {
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
    composable(LoginScreen) {
        LoginScreen(
            snackbarHostState = snackbarHostState,
            logo = logo,
            onLoggedIn = onLoggedIn,
            onRegisterClick = onRegisterClick
        )
    }
}
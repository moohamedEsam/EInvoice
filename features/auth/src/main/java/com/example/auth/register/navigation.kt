package com.example.auth.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val RegisterScreenRoute = "RegisterScreen"

fun NavHostController.navigateToRegister() {
    navigate(RegisterScreenRoute)
}

fun NavGraphBuilder.registerScreen(
    logo: Any,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    composable(RegisterScreenRoute) {
        RegisterScreen(
            logo = logo,
            onRegistered = onRegistered,
            onLoginClick = onLoginClick
        )
    }
}

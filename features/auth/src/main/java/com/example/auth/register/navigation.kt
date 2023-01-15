package com.example.auth.register

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val RegisterScreenRoute = "RegisterScreen"

fun NavHostController.navigateToRegister() {
    navigate(RegisterScreenRoute)
}

fun NavGraphBuilder.registerScreen(
    logo: Any,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit
) {
    composable(RegisterScreenRoute) {
        RegisterScreen(
            logo = logo,
            onShowSnackBarEvent = onShowSnackBarEvent,
            onRegistered = onRegistered,
            onLoginClick = onLoginClick
        )
    }
}

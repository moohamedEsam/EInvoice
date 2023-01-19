package com.example.client.screens.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val ClientsScreenRoute = "clients screen"


fun NavGraphBuilder.clientsScreen(
    onClientClicked: (String) -> Unit,
    onCreateClientClicked: () -> Unit,
) {
    composable(ClientsScreenRoute) {
        ClientsScreen(
            onClientClicked = { onClientClicked(it.id) },
            onCreateClientClicked = onCreateClientClicked,
        )
    }
}

fun NavHostController.navigateToClientsScreen() {
    navigate(ClientsScreenRoute){
        launchSingleTop = true
    }
}
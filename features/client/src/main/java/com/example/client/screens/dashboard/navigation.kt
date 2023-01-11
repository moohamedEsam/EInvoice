package com.example.client.screens.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val ClientDashboardScreenRoute = "client Dashboard"


fun NavGraphBuilder.clientDashboardScreen(
    onDocumentClick: (String) -> Unit,
    onClientEditClick: (String) -> Unit,
) {
    composable("$ClientDashboardScreenRoute/{clientId}") {
        val clientId = it.arguments?.getString("clientId") ?: ""
        ClientDashboardScreen(
            clientId = clientId,
            onDocumentClick = onDocumentClick,
        ) { onClientEditClick(clientId) }
    }
}


fun NavHostController.navigateToClientDashboardScreen(clientId: String) {
    navigate("$ClientDashboardScreenRoute/$clientId")
}
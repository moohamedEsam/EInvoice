package com.example.client.screens.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val ClientDashboardScreenRoute = "client Dashboard"


fun NavGraphBuilder.clientDashboardScreen(
    onDocumentClick: (String) -> Unit,
    onClientEditClick: (String) -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
) {
    composable("$ClientDashboardScreenRoute/{clientId}") {
        val clientId = it.arguments?.getString("clientId") ?: ""
        ClientDashboardScreen(
            clientId = clientId,
            onDocumentClick = onDocumentClick,
            onEditClick = { onClientEditClick(clientId) },
            onShowSnackBarEvent = onShowSnackBarEvent,
        )
    }
}


fun NavHostController.navigateToClientDashboardScreen(clientId: String) {
    navigate("$ClientDashboardScreenRoute/$clientId")
}
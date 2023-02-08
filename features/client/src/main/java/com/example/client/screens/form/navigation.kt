package com.example.client.screens.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent
import com.example.maplocation.latKey
import com.example.maplocation.lngKey

const val ClientFormScreenRoute = "Client Form"
private const val clientIdKey = "clientId"
fun NavGraphBuilder.clientFormScreen(
    onLocationRequested: () -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onClientCreated: (String) -> Unit
) {
    composable(
        route="$ClientFormScreenRoute/{$clientIdKey}",
    ) {
        val clientId = it.arguments?.getString(clientIdKey) ?: " "
        val lat = it.arguments?.getDouble(latKey) ?: 0.0
        val lng = it.arguments?.getDouble(lngKey) ?: 0.0
        ClientFormScreen(
            onLocationRequested = onLocationRequested,
            onShowSnackBarEvent = onShowSnackBarEvent,
            onClientCreated = onClientCreated,
            clientId = clientId,
            lat = lat,
            lng = lng
        )
    }
}

fun NavHostController.navigateToClientFormScreen(clientId: String = " ") {
    navigate("$ClientFormScreenRoute/$clientId")
}
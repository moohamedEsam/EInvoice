package com.example.client.screens.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.maplocation.latKey
import com.example.maplocation.lngKey

const val ClientFormScreenRoute = "Client Form"
private const val clientIdKey = "clientId"
fun NavGraphBuilder.clientFormScreen(
    onLocationRequested: () -> Unit
) {
    composable(
        route="$ClientFormScreenRoute/{$clientIdKey}",
    ) {
        val clientId = it.arguments?.getString(clientIdKey) ?: " "
        val lat = it.arguments?.getDouble(latKey) ?: 0.0
        val lng = it.arguments?.getDouble(lngKey) ?: 0.0
        ClientFormScreen(
            clientId = clientId,
            lat = lat,
            lng = lng,
            onLocationRequested = onLocationRequested
        )
    }
}

fun NavHostController.navigateToClientFormScreen(clientId: String = " ") {
    navigate("$ClientFormScreenRoute/$clientId")
}
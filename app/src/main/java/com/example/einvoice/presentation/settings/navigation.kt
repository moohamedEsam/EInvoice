package com.example.einvoice.presentation.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val SettingsScreenRoute = "Settings Screen"

fun NavGraphBuilder.settingsScreen() {
    composable(
        route = SettingsScreenRoute,
        content = {
            SettingsScreen()
        }
    )
}

fun NavHostController.navigateToSettingsScreen() {
    navigate(SettingsScreenRoute)
}
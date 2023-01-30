package com.example.item.screens.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val ItemsScreenRoute = "items"

fun NavGraphBuilder.itemsScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    composable(ItemsScreenRoute) {
        ItemsScreen(onShowSnackBarEvent)
    }
}


fun NavHostController.navigateToItemsScreen() {
    navigate(ItemsScreenRoute) {
        launchSingleTop = true
    }
}
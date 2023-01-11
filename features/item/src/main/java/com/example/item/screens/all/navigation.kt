package com.example.item.screens.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val ItemsScreenRoute = "items"

fun NavGraphBuilder.itemsScreen() {
    composable(ItemsScreenRoute) {
        ItemsScreen()
    }
}


fun NavHostController.navigateToItemsScreen() {
    navigate(ItemsScreenRoute) {
        launchSingleTop = true
    }
}
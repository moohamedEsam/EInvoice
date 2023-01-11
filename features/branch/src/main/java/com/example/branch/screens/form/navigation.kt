package com.example.branch.screens.form

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val BranchFormScreenRoute = "Branch Form Screen"

fun NavGraphBuilder.branchFormScreen(
    snackbarHostState: SnackbarHostState,
    onLocationRequested: () -> Unit = {}
) {
    composable(BranchFormScreenRoute) {
        val lat = it.arguments?.getDouble("lat")
        val lng = it.arguments?.getDouble("lng")
        BranchFormScreen(lat, lng, snackbarHostState, onLocationRequested)
    }
}

fun NavHostController.navigateToBranchFormScreen(
    lat: Double? = null,
    lng: Double? = null
) {
    navigate("$BranchFormScreenRoute?lat=$lat&lng=$lng"){
        restoreState = true
        launchSingleTop = true
    }
}
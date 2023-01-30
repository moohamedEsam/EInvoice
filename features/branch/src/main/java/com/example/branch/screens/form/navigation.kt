package com.example.branch.screens.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent
import com.example.maplocation.latKey
import com.example.maplocation.lngKey

const val BranchFormScreenRoute = "Branch Form"
const val branchIdKey = "id"

fun NavGraphBuilder.branchFormScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onLocationRequested: () -> Unit = {}
) {
    composable("$BranchFormScreenRoute/{$branchIdKey}") {
        val lat = it.arguments?.getDouble(latKey)
        val lng = it.arguments?.getDouble(lngKey)
        val id = it.arguments?.getString(branchIdKey) ?: " "
        BranchFormScreen(
            latitude = lat,
            longitude = lng,
            branchId = id,
            onShowSnackbarEvent = onShowSnackBarEvent,
            onLocationRequested = onLocationRequested
        )
    }
}

fun NavHostController.navigateToBranchFormScreen(
    id: String = " "
) {
    navigate("$BranchFormScreenRoute/$id") {
        restoreState = true
        launchSingleTop = true
    }
}
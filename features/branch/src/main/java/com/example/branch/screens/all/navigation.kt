package com.example.branch.screens.all

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val BranchesScreenRoute = "branches"

fun NavGraphBuilder.branchesScreen(
    snackbarHostState: SnackbarHostState,
    onCreateBranchClick: () -> Unit,
) {
    composable(BranchesScreenRoute) {
        BranchesScreen(
            snackbarHostState = snackbarHostState,
            onCreateBranchClick = onCreateBranchClick,
        )
    }
}

fun NavHostController.navigateToBranchesScreen() {
    navigate(BranchesScreenRoute) {
        launchSingleTop = true
    }
}
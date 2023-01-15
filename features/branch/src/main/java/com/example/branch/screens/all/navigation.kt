package com.example.branch.screens.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val BranchesScreenRoute = "branches"

fun NavGraphBuilder.branchesScreen(
    onCreateBranchClick: () -> Unit,
    onBranchClick: (String) -> Unit,
) {
    composable(BranchesScreenRoute) {
        BranchesScreen(
            onBranchClick = onBranchClick,
            onAddBranchClick = onCreateBranchClick
        )
    }
}

fun NavHostController.navigateToBranchesScreen() {
    navigate(BranchesScreenRoute) {
        launchSingleTop = true
    }
}
package com.example.branch.screens.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val BranchDashboardScreenRoute= "Branch Dashboard"

fun NavGraphBuilder.branchDashboardScreen(
    onDocumentClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
){
    composable("$BranchDashboardScreenRoute/{branchId}"){
        val branchId = it.arguments?.getString("branchId") ?: ""
        BranchDashboardScreen(
            branchId = branchId,
            onDocumentClick = onDocumentClick
        ) { onEditClick(branchId) }
    }
}


fun NavHostController.navigateToBranchDashboardScreen(branchId: String){
    navigate("$BranchDashboardScreenRoute/$branchId")
}
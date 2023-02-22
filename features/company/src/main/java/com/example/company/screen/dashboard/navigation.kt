package com.example.company.screen.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val CompanyDashboardScreenRoute = "Company Dashboard"


fun NavGraphBuilder.companyDashboardScreen(
    onClientClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onCreateDocumentClick: () -> Unit
) {
    composable("$CompanyDashboardScreenRoute/{companyId}") { backStackEntry ->
        val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
        CompanyDashboardScreen(
            companyId = companyId,
            onClientClick = onClientClick,
            onBranchClick = onBranchClick,
            onDocumentClick = onDocumentClick,
            onEditClick = onEditClick,
            onCreateDocumentClick =  onCreateDocumentClick
        )
    }
}

fun NavHostController.navigateToCompanyDashboardScreen(companyId: String) {
    navigate("$CompanyDashboardScreenRoute/$companyId")
}
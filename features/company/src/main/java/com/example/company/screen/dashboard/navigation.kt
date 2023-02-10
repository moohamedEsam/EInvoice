package com.example.company.screen.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val CompanyDashboardScreenRoute = "Company Dashboard"


fun NavGraphBuilder.companyDashboardScreen(
    onClientClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onShowSnackbar: (SnackBarEvent) -> Unit,
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
            onShowSnackBarEvent = onShowSnackbar,
            onCreateDocumentClick = onCreateDocumentClick
        )
    }
}

fun NavHostController.navigateToCompanyDashboardScreen(companyId: String) {
    navigate("$CompanyDashboardScreenRoute/$companyId")
}
package com.example.company.screen.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent
import com.example.models.company.Company

const val CompaniesScreenRoute = "companies"

fun NavGraphBuilder.companiesScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onCompanyClick: (Company) -> Unit,
    onCreateNewCompany: () -> Unit,
    onCompanyEditClick: (String) -> Unit,
) {
    composable(CompaniesScreenRoute) {
        CompaniesScreen(
            onShowSnackBarEvent = onShowSnackBarEvent,
            onCompanyClick = onCompanyClick,
            onCreateNewCompanyClick = onCreateNewCompany,
            onCompanyEditClick = { onCompanyEditClick(it.id) }
        )
    }
}

fun NavHostController.navigateToCompaniesScreen() {
    navigate(CompaniesScreenRoute) {
        launchSingleTop = true
    }
}
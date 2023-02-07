package com.example.company.screen.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val CompaniesScreenRoute = "companies"

fun NavGraphBuilder.companiesScreen(
    onCompanyClick: (String) -> Unit,
    onCreateNewCompany: () -> Unit,
    onCompanyEditClick: (String) -> Unit,
) {
    composable(CompaniesScreenRoute) {
        CompaniesScreen(
            onCompanyClick = { onCompanyClick(it.id) },
            onCreateNewCompanyClick = onCreateNewCompany
        ) { onCompanyEditClick(it.id) }
    }
}

fun NavHostController.navigateToCompaniesScreen() {
    navigate(CompaniesScreenRoute) {
        launchSingleTop = true
    }
}
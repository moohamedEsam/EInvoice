package com.example.company.screen

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.company.screen.all.CompaniesScreen
import com.example.models.Company

const val CompaniesScreenRoute = "companies"

fun NavGraphBuilder.companiesScreen(
    snackbarHostState: SnackbarHostState,
    onCompanyClick: (Company) -> Unit
) {
    composable(CompaniesScreenRoute) {
        CompaniesScreen(onCompanyClick)
    }
}

fun NavHostController.navigateToCompaniesScreen() {
    navigate(CompaniesScreenRoute)
}
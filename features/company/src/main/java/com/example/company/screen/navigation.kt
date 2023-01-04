package com.example.company.screen

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.models.Company

const val CompaniesScreen = "companies"

fun NavGraphBuilder.companiesScreen(
    snackbarHostState: SnackbarHostState,
    onCompanyClick: (Company) -> Unit
) {
    composable(CompaniesScreen) {
        CompaniesScreen(onCompanyClick)
    }
}

fun NavHostController.navigateToCompaniesScreen() {
    navigate(CompaniesScreen)
}
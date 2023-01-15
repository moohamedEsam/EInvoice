package com.example.company.screen.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent
import com.example.models.Company

const val CompaniesScreenRoute = "companies"

fun NavGraphBuilder.companiesScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onCompanyClick: (Company) -> Unit
) {
    composable(CompaniesScreenRoute) {
        CompaniesScreen(onShowSnackBarEvent, onCompanyClick)
    }
}

fun NavHostController.navigateToCompaniesScreen() {
    navigate(CompaniesScreenRoute){
        launchSingleTop = true
    }
}
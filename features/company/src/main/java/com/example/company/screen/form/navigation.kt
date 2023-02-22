package com.example.company.screen.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val CompanyFormScreenRoute = "company form screen"
private const val CompanyIdKey = "companyId"

fun NavGraphBuilder.companyFormScreen() {
    composable("$CompanyFormScreenRoute/{$CompanyIdKey}") {
        val companyId = it.arguments?.getString(CompanyIdKey) ?: "  "
        CompanyFormScreen(companyId = companyId)
    }
}


fun NavHostController.navigateToCompanyFormScreen(
    companyId: String = "  "
) {
    navigate("$CompanyFormScreenRoute/$companyId")
}
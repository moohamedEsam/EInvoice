package com.example.company.screen.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val CompanyFormScreenRoute = "company form screen"
private const val CompanyIdKey = "companyId"

fun NavGraphBuilder.companyFormScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
) {
    composable("$CompanyFormScreenRoute/{$CompanyIdKey}") {
        val companyId = it.arguments?.getString(CompanyIdKey) ?: "  "
        CompanyFormScreen(
            companyId = companyId,
            onShowSnackBarEvent = onShowSnackBarEvent
        )
    }
}


fun NavHostController.navigateToCompanyFormScreen(
    companyId: String = "  "
) {
    navigate("$CompanyFormScreenRoute/$companyId")
}
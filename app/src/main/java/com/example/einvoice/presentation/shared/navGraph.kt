package com.example.einvoice.presentation.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.auth.login.loginScreen
import com.example.auth.register.navigateToRegister
import com.example.auth.register.registerScreen
import com.example.branch.screens.all.branchesScreen
import com.example.branch.screens.dashboard.branchDashboardScreen
import com.example.branch.screens.dashboard.navigateToBranchDashboardScreen
import com.example.branch.screens.form.branchFormScreen
import com.example.branch.screens.form.navigateToBranchFormScreen
import com.example.client.screens.all.clientsScreen
import com.example.client.screens.dashboard.clientDashboardScreen
import com.example.client.screens.dashboard.navigateToClientDashboardScreen
import com.example.client.screens.form.clientFormScreen
import com.example.client.screens.form.navigateToClientFormScreen
import com.example.common.models.SnackBarEvent
import com.example.company.screen.all.companiesScreen
import com.example.company.screen.all.navigateToCompaniesScreen
import com.example.company.screen.dashboard.companyDashboardScreen
import com.example.company.screen.dashboard.navigateToCompanyDashboardScreen
import com.example.company.screen.form.companyFormScreen
import com.example.company.screen.form.navigateToCompanyFormScreen
import com.example.document.screens.all.documentsScreen
import com.example.document.screens.form.documentFormScreen
import com.example.document.screens.form.navigateToDocumentFormScreen
import com.example.einvoice.R
import com.example.item.screens.all.itemsScreen
import com.example.maplocation.latKey
import com.example.maplocation.lngKey
import com.example.maplocation.mapScreen
import com.example.maplocation.navigateToMapScreen

@Composable
fun EInvoiceNavGraph(
    navController: NavHostController,
    startScreen: String,
    onShowSnackbarEvent: (SnackBarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier
    ) {
        loginScreen(
            onShowSnackBarEvent = onShowSnackbarEvent,
            logo = R.drawable.invoice,
            onLoggedIn = navController::navigateToCompaniesScreen,
            onRegisterClick = navController::navigateToRegister
        )

        registerScreen(
            logo = R.drawable.invoice,
            onShowSnackBarEvent = onShowSnackbarEvent,
            onRegistered = { },
            onLoginClick = navController::popBackStack
        )

        companiesScreen(
            onCompanyClick = {navController.navigateToCompanyDashboardScreen(it)},
            onCreateNewCompany = navController::navigateToCompanyFormScreen
        ) { navController.navigateToCompanyFormScreen(it) }

        companyFormScreen(onShowSnackbarEvent)

        companyDashboardScreen(
            onClientClick = navController::navigateToClientDashboardScreen,
            onBranchClick = navController::navigateToBranchDashboardScreen,
            onDocumentClick = navController::navigateToDocumentFormScreen,
            onEditClick = navController::navigateToCompanyFormScreen,
            onShowSnackbar = onShowSnackbarEvent
        )

        branchFormScreen(onShowSnackbarEvent) {
            navController.navigateToMapScreen()
        }

        mapScreen { lat, lng ->
            navController.previousBackStackEntry?.arguments?.putDouble(latKey, lat)
            navController.previousBackStackEntry?.arguments?.putDouble(lngKey, lng)
            navController.popBackStack()
        }

        branchesScreen(
            onCreateBranchClick = navController::navigateToBranchFormScreen,
            onBranchEditClick = navController::navigateToBranchFormScreen,
            onBranchClick = navController::navigateToBranchDashboardScreen
        )

        branchDashboardScreen(
            onDocumentClick = navController::navigateToDocumentFormScreen,
            onEditClick = navController::navigateToBranchFormScreen,
            onShowSnackBarEvent = onShowSnackbarEvent
        )

        clientsScreen(
            onClientClicked = navController::navigateToClientDashboardScreen,
            onCreateClientClicked = navController::navigateToClientFormScreen,
            onEditClick = navController::navigateToClientFormScreen
        )

        clientFormScreen(
            onLocationRequested = navController::navigateToMapScreen,
            onShowSnackBarEvent = onShowSnackbarEvent,
            onClientCreated = navController::navigateToClientDashboardScreen
        )

        clientDashboardScreen(
            onDocumentClick = navController::navigateToDocumentFormScreen,
            onClientEditClick = navController::navigateToClientFormScreen,
            onShowSnackBarEvent = onShowSnackbarEvent
        )

        itemsScreen(onShowSnackBarEvent = onShowSnackbarEvent)

        documentsScreen(
            onDocumentClick = {
                navController.navigateToDocumentFormScreen(it)
            },
            onAddDocumentClick = navController::navigateToDocumentFormScreen
        )

        documentFormScreen(onShowSnackbarEvent)
    }
}
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
import com.example.company.screen.all.companiesScreen
import com.example.company.screen.all.navigateToCompaniesScreen
import com.example.company.screen.dashboard.companyDashboardScreen
import com.example.company.screen.dashboard.navigateToCompanyDashboardScreen
import com.example.company.screen.form.companyFormScreen
import com.example.company.screen.form.navigateToCompanyFormScreen
import com.example.document.screens.all.documentsScreen
import com.example.document.screens.details.documentDetailsScreen
import com.example.document.screens.details.navigateToDocumentDetailsScreen
import com.example.document.screens.form.documentFormScreen
import com.example.document.screens.form.navigateToCreditDocumentFormScreen
import com.example.document.screens.form.navigateToDebitDocumentFormScreen
import com.example.document.screens.form.navigateToDocumentFormScreen
import com.example.einvoice.R
import com.example.einvoice.presentation.settings.settingsScreen
import com.example.item.screens.all.itemsScreen
import com.example.maplocation.latKey
import com.example.maplocation.lngKey
import com.example.maplocation.mapScreen
import com.example.maplocation.navigateToMapScreen

@Composable
fun EInvoiceNavGraph(
    navController: NavHostController,
    startScreen: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startScreen,
        modifier = modifier
    ) {
        loginScreen(
            logo = R.drawable.logo,
            onLoggedIn = navController::navigateToCompaniesScreen,
            onRegisterClick = navController::navigateToRegister
        )

        registerScreen(
            logo = R.drawable.logo,
            onRegistered = navController::popBackStack,
            onLoginClick = navController::popBackStack
        )

        companiesScreen(
            onCompanyClick = { navController.navigateToCompanyDashboardScreen(it) },
            onCreateNewCompany = navController::navigateToCompanyFormScreen
        ) { navController.navigateToCompanyFormScreen(it) }

        companyFormScreen()

        companyDashboardScreen(
            onClientClick = navController::navigateToClientDashboardScreen,
            onBranchClick = navController::navigateToBranchDashboardScreen,
            onDocumentClick = navController::navigateToDocumentDetailsScreen,
            onEditClick = navController::navigateToCompanyFormScreen,
            onCreateDocumentClick = navController::navigateToDocumentFormScreen
        )

        branchFormScreen(
            onLocationRequested = navController::navigateToMapScreen,
            onBranchSaved = navController::popBackStack
        )

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
            onDocumentClick = navController::navigateToDocumentDetailsScreen,
            onEditClick = navController::navigateToBranchFormScreen
        )

        clientsScreen(
            onClientClicked = navController::navigateToClientDashboardScreen,
            onCreateClientClicked = navController::navigateToClientFormScreen,
            onEditClick = navController::navigateToClientFormScreen
        )

        clientFormScreen(onLocationRequested = navController::navigateToMapScreen)

        clientDashboardScreen(
            onDocumentClick = navController::navigateToDocumentDetailsScreen,
            onClientEditClick = navController::navigateToClientFormScreen
        )

        itemsScreen()

        documentsScreen(
            onDocumentClick = navController::navigateToDocumentDetailsScreen,
            onAddDocumentClick = navController::navigateToDocumentFormScreen,
            onCreateDebitClick = navController::navigateToDebitDocumentFormScreen,
            onCreateCreditClick = navController::navigateToCreditDocumentFormScreen,
            onDocumentUpdateClick = navController::navigateToDocumentFormScreen
        )

        documentFormScreen(
            onDocumentSaved = {
                navController.popBackStack()
                navController.navigateToDocumentDetailsScreen(it)
            },
        )

        documentDetailsScreen(
            onCompanyClick = navController::navigateToCompanyDashboardScreen,
            onBranchClick = navController::navigateToBranchDashboardScreen,
            onClientClick = navController::navigateToClientDashboardScreen,
            onEditClick = navController::navigateToDocumentFormScreen
        )

        settingsScreen()
    }
}
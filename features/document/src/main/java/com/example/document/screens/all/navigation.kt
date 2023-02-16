package com.example.document.screens.all

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val DocumentsScreenRoute = "documents"

fun NavGraphBuilder.documentsScreen(
    onDocumentClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
    onCreateCreditClick: (String) -> Unit,
    onCreateDebitClick: (String) -> Unit,
    onDocumentUpdateClick: (String) -> Unit,
) {
    composable(DocumentsScreenRoute) {
        DocumentsScreen(
            onDocumentClick = onDocumentClick,
            onAddDocumentClick = onAddDocumentClick,
            onCreateCreditClick = onCreateCreditClick,
            onCreateDebitClick = onCreateDebitClick,
            onDocumentUpdateClick = onDocumentUpdateClick,
        )
    }
}

fun NavHostController.navigateToDocumentsScreen() {
    navigate(DocumentsScreenRoute) {
        launchSingleTop = true
    }
}
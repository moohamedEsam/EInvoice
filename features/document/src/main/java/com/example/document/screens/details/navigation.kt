package com.example.document.screens.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val DocumentDetailsScreenRoute = "Document Details"


fun NavGraphBuilder.documentDetailsScreen(
    onCompanyClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onClientClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
) {
    composable(
        route = "$DocumentDetailsScreenRoute/{documentId}",
    ) {
        val documentId = it.arguments?.getString("documentId") ?: ""
        DocumentDetailsScreen(
            documentId = documentId,
            onCompanyClick = onCompanyClick,
            onBranchClick = onBranchClick,
            onClientClick = onClientClick,
            onEditClick = onEditClick,
        )
    }
}


fun NavHostController.navigateToDocumentDetailsScreen(documentId: String) {
    navigate("$DocumentDetailsScreenRoute/$documentId")
}
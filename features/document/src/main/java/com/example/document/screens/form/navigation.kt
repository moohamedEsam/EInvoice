package com.example.document.screens.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.common.models.SnackBarEvent

const val DocumentFormScreenRoute = "document form"


fun NavGraphBuilder.documentFormScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    composable("$DocumentFormScreenRoute/{documentId}") {
        val documentId = it.arguments?.getString("documentId") ?: " "
        DocumentFormScreen(
            documentId = documentId,
            onShowSnackBarEvent = onShowSnackBarEvent
        )
    }
}

fun NavHostController.navigateToDocumentFormScreen(
    documentId: String = " "
) {
    navigate("$DocumentFormScreenRoute/$documentId")
}
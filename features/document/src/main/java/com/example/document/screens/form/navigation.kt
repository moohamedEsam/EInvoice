package com.example.document.screens.form

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.common.models.SnackBarEvent

const val DocumentFormScreenRoute = "document form"


fun NavGraphBuilder.documentFormScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    composable(
        route = "$DocumentFormScreenRoute/{documentId}/{status}",
        arguments = getScreenNavigationArguments()
    ) {
        val documentId = it.arguments?.getString("documentId") ?: " "
        DocumentFormScreen(
            documentId = documentId,
            onShowSnackBarEvent = onShowSnackBarEvent
        )
    }
}

private fun getScreenNavigationArguments() = listOf(
    navArgument("documentId") {
        defaultValue = " "
    },
    navArgument("status") {
        defaultValue = "I"
    }
)

fun NavHostController.navigateToDocumentFormScreen(
    documentId: String = " "
) {
    navigate("$DocumentFormScreenRoute/$documentId/I")
}

fun NavHostController.navigateToCreditDocumentFormScreen(
    documentId: String
) {
    navigate("$DocumentFormScreenRoute/$documentId/C")
}

fun NavHostController.navigateToDebitDocumentFormScreen(
    documentId: String
) {
    navigate("$DocumentFormScreenRoute/$documentId/D")
}
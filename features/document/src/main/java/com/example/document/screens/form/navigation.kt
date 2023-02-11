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
        route = "$DocumentFormScreenRoute/{documentId}",
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
    navArgument("company") {
        defaultValue = null
        nullable = true
    }
)

fun NavHostController.navigateToDocumentFormScreen(
    documentId: String = " "
) {
    navigate("$DocumentFormScreenRoute/$documentId")
}
package com.example.client.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.Result.*
import com.example.common.models.SnackBarEvent
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun ClientDashboardScreen(
    clientId: String,
    onDocumentClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
) {
    val viewModel: ClientDashboardViewModel by viewModel { parametersOf(clientId) }
    val uiState by viewModel.uiState.collectAsState()
    ClientDashboardScreenContent(
        uiState = uiState,
        onDocumentClick = onDocumentClick,
        onEditClick = onEditClick,
        onDeleteClick = {
            viewModel.onDeleteClick { result ->
                val event = if (result is Success)
                    SnackBarEvent(
                        "Client deleted successfully",
                        "Undo"
                    ) { viewModel.onUndoDeleteClick { } }
                else
                    SnackBarEvent((result as? Error)?.exception ?: "Error deleting client")
                onShowSnackBarEvent(event)
            }
        },
        onDatePicked = viewModel::onDatePicked
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ClientDashboardScreenContent(
    uiState: ClientDashBoardState,
    onDocumentClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDatePicked: (Date) -> Unit,
) {
    val pages = listOf("General", "Settings")
    var currentPage by remember {
        mutableStateOf(0)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabRow(selectedTabIndex = currentPage) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = { Text(page) },
                    selected = currentPage == index,
                    onClick = { currentPage = index }
                )
            }
        }

        AnimatedContent(
            targetState = currentPage,
            modifier = Modifier.weight(1f),
        ) { index ->
            when (index) {
                0 -> ClientGeneralPage(
                    uiState = uiState,
                    onDocumentClick = onDocumentClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDatePicked = onDatePicked,
                )
                else -> SettingsPage(client = uiState.client)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ClientDashboardScreenPreview() {
    val state = ClientDashBoardState.random()
    ClientDashboardScreenContent(
        uiState = state,
        onDocumentClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onDatePicked = {},
    )
}
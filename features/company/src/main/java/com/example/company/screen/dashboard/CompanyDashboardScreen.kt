package com.example.company.screen.dashboard

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.Document
import com.example.models.document.empty
import com.example.models.empty
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun CompanyDashboardScreen(
    companyId: String,
    onClientClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onCreateDocumentClick: () -> Unit
) {
    val viewModel: CompanyDashboardViewModel by viewModel { parametersOf(companyId) }
    val state by viewModel.uiState.collectAsState()
    if (state == null)
        return
    CompanyDashboardScreenContent(
        state = state!!,
        onClientClick = onClientClick,
        onBranchClick = onBranchClick,
        onEditClick = { onEditClick(companyId) },
        onDeleteClick = {
            viewModel.deleteCompany { result ->
                val event = getSnackBarEventFromResult(result, viewModel)
                onShowSnackBarEvent(event)
            }
        },
        onDocumentClick = onDocumentClick,
        onDatePicked = { viewModel.setPickedDate(it) },
        onCreateDocumentClick = onCreateDocumentClick
    )
}

private fun getSnackBarEventFromResult(
    result: Result<Unit>,
    viewModel: CompanyDashboardViewModel
) = when (result) {
    is Result.Success -> SnackBarEvent(
        message = "Company deleted",
        actionLabel = "Undo"
    ) {
        viewModel.undoDeleteCompany {}
    }
    is Result.Error -> SnackBarEvent(
        message = result.exception ?: "Error Deleting company"
    )
    else -> SnackBarEvent(message = "Error Deleting company")
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CompanyDashboardScreenContent(
    state: CompanyDashboardState,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onClientClick: (String) -> Unit,
    onBranchClick: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onDatePicked: (Date) -> Unit,
    onCreateDocumentClick: () -> Unit
) {
    val pages = listOf("General", "Clients", "Branches", "Settings")
    val pagerState = rememberPagerState()
    var pageToScroll by remember {
        mutableStateOf(pagerState.currentPage)
    }
    LaunchedEffect(key1 = pageToScroll) {
        pagerState.animateScrollToPage(pageToScroll)
    }
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("CompanyDashboardScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                pages.forEachIndexed { index, page ->
                    Tab(
                        text = { Text(page) },
                        selected = pagerState.currentPage == index,
                        onClick = { pageToScroll = index },
                        modifier = Modifier.testTag("CompanyDashboardTab")
                    )
                }
            }

            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f),
                itemSpacing = 8.dp
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> GeneralPage(
                        state = state,
                        onDeleteClick = onDeleteClick,
                        onEditClick = onEditClick,
                        onDocumentClick = { onDocumentClick(it) },
                        onDatePicked = onDatePicked
                    )

                    1 -> CompanyClientsPage(
                        clients = state.companyView.clients,
                        documents = state.documents,
                        onClientClick = { onClientClick(it.id) }
                    )

                    2 -> CompanyBranchesPage(
                        branches = state.companyView.branches,
                        documents = state.documents,
                        onBranchClick = { onBranchClick(it.id) }
                    )

                    else -> CompanySettingsPage(
                        settings = state.companyView.company.settings,
                    )

                }
            }
        }

        FloatingActionButton(
            onClick = onCreateDocumentClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .testTag("CompanyDashboardCreateDocumentButton")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_MASK)
@Composable
fun CompanyDashboardScreenPreview() {
    CompanyDashboardScreenContent(
        onDeleteClick = {},
        state = CompanyDashboardState(
            companyView = CompanyView(
                company = Company.empty(),
                documents = listOf(Document.empty()),
                branches = listOf(Branch.empty()),
                clients = listOf(Client.empty()),
            ),
            invoices = emptyList(),
            documents = emptyList(),
            isDeleteEnabled = false,
            pickedDate = Date(),
        ),
        onEditClick = {},
        onClientClick = {},
        onBranchClick = {},
        onDocumentClick = {},
        onDatePicked = {},
        onCreateDocumentClick = {}
    )
}


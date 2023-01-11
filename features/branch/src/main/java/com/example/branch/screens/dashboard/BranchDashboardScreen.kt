package com.example.branch.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun BranchDashboardScreen(
    branchId: String,
    onDocumentClick: (String) -> Unit,
    onEditClick: () -> Unit,
) {
    val viewModel: BranchDashboardViewModel by viewModel { parametersOf(branchId) }
    val uiState by viewModel.uiState.collectAsState()
    BranchDashboardScreenContent(
        uiState = uiState,
        onDocumentClick = onDocumentClick,
        onEditClick = onEditClick,
        onDeleteClick = {
            viewModel.onDeleteClick()
        },
        onDatePicked = viewModel::onDatePicked
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BranchDashboardScreenContent(
    uiState: BranchDashboardState,
    onDocumentClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDatePicked: (Date) -> Unit,
) {
    val pages = listOf("General", "Items", "Settings")
    val pagerState = rememberPagerState()
    var pageToScroll by remember {
        mutableStateOf(pagerState.currentPage)
    }
    LaunchedEffect(key1 = pageToScroll) {
        pagerState.animateScrollToPage(pageToScroll)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = { Text(page) },
                    selected = pagerState.currentPage == index,
                    onClick = { pageToScroll = index }
                )
            }
        }

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.weight(1f),
            itemSpacing = 8.dp
        ) { index ->
            when (index) {
                0 -> BranchGeneralPage(
                    uiState = uiState,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onDatePicked = onDatePicked,
                    onDocumentClick = onDocumentClick
                )
                1 -> ItemsPage(
                    items = uiState.branchView.items,
                    onItemClicked = { }
                )
                2 -> SettingsPage(uiState.branchView.branch)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BranchDashboardScreenPreview() {
    val state = BranchDashboardState.random()
    BranchDashboardScreenContent(
        uiState = state,
        onDocumentClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onDatePicked = {},
    )
}
package com.example.branch.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.OutlinedSearchTextField
import com.example.models.Branch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel

@Composable
fun BranchesScreen(
    onBranchClick: (String) -> Unit,
    onAddBranchClick: () -> Unit
) {
    val viewModel : BranchesViewModel by viewModel()
    val branches by viewModel.branches.collectAsState()
    BranchesScreenContent(
        branches = branches,
        onBranchClick = { onBranchClick(it.id) },
        onCreateBranchClick = onAddBranchClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BranchesScreenContent(
    branches: List<Branch>,
    onBranchClick: (Branch) -> Unit,
    onCreateBranchClick: () -> Unit,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var expanded by remember{
            mutableStateOf(false)
        }
        val nestedConnection = object : NestedScrollConnection{
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                expanded = consumed.y < 0f || available.y < 0f
                return super.onPostScroll(consumed, available, source)
            }
        }
        OutlinedSearchTextField(
            queryState = queryState,
            onQueryChange = onQueryChange,
            modifier = Modifier.fillMaxWidth()
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(250.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .nestedScroll(nestedConnection)
        ) {
            items(branches, key = { it.id }) {
                BranchItem(
                    branch = it,
                    onBranchClick = { onBranchClick(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = onCreateBranchClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            text = { Text("Create branch") },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            expanded = expanded
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchItem(
    branch: Branch,
    onBranchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier, onClick = onBranchClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = branch.name, style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Country: ${branch.country}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Governate: ${branch.governate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "City: ${branch.regionCity}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "PostalCode: ${branch.postalCode}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "Street: ${branch.street}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BranchesScreenPreview() {
    val branches =
        List(5) {
            Branch(
                id = it.toString(),
                name = "Branch $it",
                internalId = "1",
                companyId = "",
                street = "something",
                country = "Egypt",
                governate = "Alexandria",
                postalCode = "123",
                regionCity = "banha",
                buildingNumber = "",
                floor = "",
                room = "",
                landmark = "",
                additionalInformation = ""
            )
        }
    BranchesScreenContent(
        branches = branches,
        onBranchClick = {},
        onCreateBranchClick = {},
        queryState = MutableStateFlow(""),
        onQueryChange = {}
    )

}
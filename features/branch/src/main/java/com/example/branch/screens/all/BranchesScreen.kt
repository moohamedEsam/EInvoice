package com.example.branch.screens.all

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.einvoicecomponents.ListScreenContent
import com.example.einvoicecomponents.loadStateItem
import com.example.models.branch.Branch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.viewModel

@Composable
fun BranchesScreen(
    onBranchClick: (String) -> Unit,
    onBranchEditClick: (String) -> Unit,
    onAddBranchClick: () -> Unit
) {
    val viewModel: BranchesViewModel by viewModel()
    val branches = viewModel.branches.collectAsLazyPagingItems()
    BranchesScreenContent(
        branches = branches,
        onBranchClick = { onBranchClick(it.id) },
        onCreateBranchClick = onAddBranchClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery,
        onBranchEditClick = onBranchEditClick
    )
}

@Composable
private fun BranchesScreenContent(
    branches: LazyPagingItems<Branch>,
    onBranchClick: (Branch) -> Unit,
    onCreateBranchClick: () -> Unit,
    onBranchEditClick: (String) -> Unit,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
) {
    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChange,
        floatingButtonText = "Create New Branch",
        onFloatingButtonClick = onCreateBranchClick,
    ) {
        items(branches) { branch ->
            BranchItem(
                branch = branch ?: return@items,
                onBranchClick = { onBranchClick(branch) },
                modifier = Modifier.fillMaxWidth(),
                onBranchEditClick = { onBranchEditClick(branch.id) }
            )
        }

        loadStateItem(branches)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchItem(
    branch: Branch,
    onBranchClick: () -> Unit,
    onBranchEditClick: () -> Unit,
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
            AssistChip(
                onClick = onBranchEditClick,
                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                label = { Text("Edit") },
                modifier = Modifier.align(Alignment.End)
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
                regionCity = "Borg El Arab",
                buildingNumber = "",
                floor = "",
                room = "",
                landmark = "",
                additionalInformation = ""
            )
        }
    BranchesScreenContent(
        branches = flowOf(PagingData.from(branches)).collectAsLazyPagingItems(),
        onBranchClick = {},
        onCreateBranchClick = {},
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onBranchEditClick = {}
    )

}
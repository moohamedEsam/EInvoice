package com.example.company.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.Branch
import com.example.models.document.DocumentView
import com.example.models.empty
@Composable
fun CompanyBranchesPage(
    branches: List<Branch>,
    documents: List<DocumentView>,
    onBranchClick: (Branch) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(branches) { branch ->
            BranchCard(
                branch = branch,
                documents = documents.filter { it.branch.id == branch.id },
                onClick = { onBranchClick(branch) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchCard(
    branch: Branch,
    documents: List<DocumentView>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = branch.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Documents: ${documents.size}")
            Text(text = "Address", style = MaterialTheme.typography.bodyLarge)
            Text("Country: ${branch.country}")
            Text("Governorate: ${branch.governate}")
            Text("City: ${branch.regionCity}")
            Text("street: ${branch.street}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyBranchesPagePreview() {
    CompanyBranchesPage(
        branches = List(10) {
            Branch.empty().copy(
                name = "Branch $it",
            )
        },
        documents = emptyList(),
        onBranchClick = {}
    )
}
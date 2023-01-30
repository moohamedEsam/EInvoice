package com.example.document.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.ListScreenContent
import com.example.models.document.DocumentView
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel


@Composable
fun DocumentsScreen(
    onDocumentClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
) {
    val viewModel: DocumentsViewModel by viewModel()
    val documents by viewModel.documents.collectAsState()

    DocumentsScreenContent(
        documents = documents,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery,
        onDocumentClick = onDocumentClick,
        onAddDocumentClick = onAddDocumentClick,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DocumentsScreenContent(
    documents: List<DocumentView>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onDocumentClick: (String) -> Unit,
    onAddDocumentClick: () -> Unit,
) {
    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChange,
        floatingButtonText = "Create New Document",
        adaptiveItemSize = 200.dp,
        onFloatingButtonClick = onAddDocumentClick,
        listContent = {
            items(documents) { document ->
                DocumentItem(
                    document = document,
                    onClick = { onDocumentClick(document.id) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentItem(
    document: DocumentView,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "from: ${document.company.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "to: ${document.client.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "branch: ${document.branch.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "invoices: ${document.invoices.size}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

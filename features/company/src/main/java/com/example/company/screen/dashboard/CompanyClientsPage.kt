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
import com.example.models.Client
import com.example.models.document.DocumentView
import com.example.models.empty
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus

@Composable
fun CompanyClientsPage(
    clients: List<Client>,
    documents: List<DocumentView>,
    onClientClick: (Client) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clients) { client ->
            ClientCard(
                client = client,
                documents = documents.filter { it.client.id == client.id },
                onClick = { onClientClick(client) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCard(
    client: Client,
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
            Text(text = client.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Business Type: ${client.businessType.name}")
            Text(text = "Tax Status: ${client.status.name}")
            Text(text = "Documents: ${documents.size}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyClientsPagePreview() {
    CompanyClientsPage(
        clients = List(10) {
            Client.empty().copy(
                name = "Client $it",
                status = TaxStatus.Taxable,
                businessType = BusinessType.B
            )
        },
        documents = emptyList(),
        onClientClick = {}
    )
}
package com.example.client.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.ListScreenContent
import com.example.models.Client
import com.example.models.utils.Address
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel

@Composable
fun ClientsScreen(
    onClientClicked: (Client) -> Unit,
    onCreateClientClicked: () -> Unit,
    onEditClick: (String) -> Unit,
) {
    val viewModel: ClientsViewModel by viewModel()
    val clients by viewModel.clients.collectAsState()

    ClientsScreenContent(
        clients = clients,
        queryState = viewModel.query,
        onQueryChanged = viewModel::onQueryChanged,
        onClientClicked = onClientClicked,
        onCreateClientClicked = onCreateClientClicked,
        onEditClick = onEditClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClientsScreenContent(
    clients: List<Client>,
    queryState: StateFlow<String>,
    onQueryChanged: (String) -> Unit,
    onClientClicked: (Client) -> Unit,
    onCreateClientClicked: () -> Unit,
    onEditClick: (String) -> Unit
) {
    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChanged,
        floatingButtonText = "Create New Client",
        adaptiveItemSize = 200.dp,
        onFloatingButtonClick = onCreateClientClicked
    ) {
        items(clients) { client ->
            ClientItem(
                client = client,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onClientClicked(client) },
                onEditClick = { onEditClick(client.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientItem(
    client: Client,
    modifier: Modifier = Modifier,
    onClick: (Client) -> Unit,
    onEditClick: () -> Unit
) {
    OutlinedCard(modifier = modifier, onClick = { onClick(client) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = client.name, style = MaterialTheme.typography.headlineSmall)
            
            Text(
                text = "Business type: ${client.businessType.asString()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Tax status: ${client.status}",
                style = MaterialTheme.typography.bodyMedium
            )


            if (client.address != null) {
                Text(
                    text = "Governorate: ${client.address?.governate}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "City: ${client.address?.regionCity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            AssistChip(
                onClick = onEditClick,
                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                label = { Text("Edit") },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ClientsScreenPreview() {
    val client = Client(
        name = "Client 1",
        address = Address(
            street = "Street 1",
            postalCode = "Zip Code 1",
            country = "Country 1",
            governate = "Governate 1",
            regionCity = "Region City 1",
            buildingNumber = "Building Number 1",
            floor = "Floor 1",
            room = "Room 1",
            landmark = "Landmark 1",
            additionalInformation = "Additional Information 1"
        ),
        phone = "Phone 1",
        email = "Email 1",
        id = "Id 1",
        registrationNumber = "Registration Number 1",
        businessType = BusinessType.B,
        status = TaxStatus.Taxable,
        companyId = "Company Id 1",
    )
    ClientsScreenContent(
        clients = List(20) { client },
        queryState = MutableStateFlow(""),
        onQueryChanged = {},
        onClientClicked = {},
        onCreateClientClicked = {},
    ) {

    }
}





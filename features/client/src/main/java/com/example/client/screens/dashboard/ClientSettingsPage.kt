package com.example.client.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.Client
import com.example.models.empty
import com.example.models.utils.Address
import com.example.models.utils.BusinessType

@Composable
fun SettingsPage(
    client: Client,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (client.businessType == BusinessType.P)
            Text(
                "Business Type: ${client.businessType}",
                style = MaterialTheme.typography.headlineMedium
            )
        if (client.address != null)
            AddressComposable(address = client.address!!)
    }
}

@Composable
private fun AddressComposable(address: Address) {
    Text(
        text = "Address",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Country: ${address.country}",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "City: ${address.regionCity}",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "Street: ${address.street}",
        style = MaterialTheme.typography.headlineSmall
    )

    Text(text = "Optional Info", style = MaterialTheme.typography.headlineMedium)
    Text(
        text = "Room: ${address.room}",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "Floor: ${address.floor}",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "Building: ${address.buildingNumber}",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "Postal Code: ${address.postalCode}",
        style = MaterialTheme.typography.headlineSmall
    )
}

@Preview(showBackground = true)
@Composable
fun BranchSettingsPagePreview() {
    SettingsPage(client = Client.empty())
}
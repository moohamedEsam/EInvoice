package com.example.branch.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.branch.Branch
import com.example.models.branch.empty

@Composable
fun SettingsPage(
    branch: Branch,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Internal Id: ${branch.internalId}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Address",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Country: ${branch.country}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "City: ${branch.regionCity}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Street: ${branch.street}",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(text = "Optional Info", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Room: ${branch.room}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Floor: ${branch.floor}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Building: ${branch.buildingNumber}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Postal Code: ${branch.postalCode}",
            style = MaterialTheme.typography.headlineSmall
        )

    }
}

@Preview(showBackground = true)
@Composable
fun BranchSettingsPagePreview() {
    SettingsPage(branch = Branch.empty().copy(internalId = "1"))
}
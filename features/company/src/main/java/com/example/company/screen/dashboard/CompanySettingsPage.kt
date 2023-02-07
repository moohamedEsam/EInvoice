package com.example.company.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.models.company.CompanySettings

@Composable
fun CompanySettingsPage(
    settings: CompanySettings,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsField("Client Id: ", settings.clientId)
        SettingsField(label = "Client Secret", value = settings.clientSecret)
        SettingsField(label = "Token Pin", value = settings.tokenPin)
        SettingsField(label = "Tax Activity Code", value = settings.taxActivityCode)
    }
}

@Composable
private fun SettingsField(label: String, value: String) {
    Row {
        Text(text = label, style = MaterialTheme.typography.headlineSmall)
        SelectionContainer {
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CompanySettingsPagePreview() {
    CompanySettingsPage(
        settings = CompanySettings(
            clientSecret = "clientSecret",
            clientId = "clientId",
            tokenPin = "123",
            taxActivityCode = "taxActivityCode",
        )
    )

}
package com.example.einvoice.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.network.serializers.settingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var ipAddress by remember {
            mutableStateOf("")
        }

        LaunchedEffect(key1 = Unit) {
            context.settingsDataStore.data.collectLatest { settings ->
                ipAddress = settings.ipAddress
            }
        }

        OutlinedTextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("IP Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                coroutineScope.launch {
                    context.settingsDataStore.updateData { settings ->
                        settings.toBuilder()
                            .setIpAddress(ipAddress)
                            .build()
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
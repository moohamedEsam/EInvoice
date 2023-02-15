package com.example.einvoicecomponents

import android.location.Address
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.models.OptionalAddress
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressComposable(
    addressState: StateFlow<Address>,
    onCountryChange: (String) -> Unit = {},
    onGovernorateChange: (String) -> Unit = {},
    onCityChange: (String) -> Unit = {},
    onStreetChange: (String) -> Unit = {},
    onPostalCodeChange: (String) -> Unit = {},
) {
    val address by addressState.collectAsState()
    OutlinedTextField(
        value = address.countryName ?: "",
        onValueChange = onCountryChange,
        label = { Text("Country*") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = address.adminArea ?: "",
        onValueChange = onGovernorateChange,
        label = { Text("Governate*") },
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = address.subAdminArea ?: "",
            onValueChange = onCityChange,
            label = { Text("City*") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = address.postalCode ?: "",
            onValueChange = onPostalCodeChange,
            label = { Text("PostalCode*") },
            modifier = Modifier.weight(1f)
        )
    }
    OutlinedTextField(
        value = address.thoroughfare ?: "",
        onValueChange = onStreetChange,
        label = { Text("Street*") },
        modifier = Modifier.fillMaxWidth()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionalAddressComposable(
    optionalAddressState: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit
) {
    val optionalAddress by optionalAddressState.collectAsState()
    OutlinedTextField(
        value = optionalAddress.buildingNumber,
        onValueChange = { onOptionalAddressChange(optionalAddress.copy(buildingNumber = it)) },
        label = { Text("Building Number") },
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = optionalAddress.room,
            label = { Text("Room") },
            onValueChange = {
                onOptionalAddressChange(optionalAddress.copy(room = it))
            },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = optionalAddress.floor,
            label = { Text("Floor") },
            onValueChange = {
                onOptionalAddressChange(optionalAddress.copy(floor = it))
            },
            modifier = Modifier.weight(1f)
        )
    }

    OutlinedTextField(
        value = optionalAddress.additionalInformation,
        label = { Text("Additional Info") },
        onValueChange = {
            onOptionalAddressChange(optionalAddress.copy(additionalInformation = it))
        },
        modifier = Modifier.fillMaxWidth()
    )

}
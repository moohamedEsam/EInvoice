package com.example.branch.screens.form

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.branch.models.OptionalAddress
import com.example.common.components.ValidationTextField
import com.example.common.models.ValidationResult
import com.example.models.Company
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun BranchFormScreen(
    latitude: Double? = null,
    longitude: Double? = null,
    snackbarHostState: SnackbarHostState,
    onLocationRequested: () -> Unit = {},
) {
    val viewModel: BranchFormViewModel by viewModel(parameters = { parametersOf(latitude, longitude) })
    if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
        Log.i("BranchFormScreen", "BranchFormScreen: $latitude, $longitude")
        val address = Geocoder(LocalContext.current)
            .getFromLocation(latitude, longitude, 1)?.first()

        if (address != null)
            viewModel.setAddressUsingLatLng(address)
    }
    BranchFormScreenContent(
        companies = viewModel.companies,
        selectedCompany = viewModel.companies.firstOrNull(),
        onCompanySelected = {},
        name = viewModel.name,
        nameValidationResult = viewModel.nameValidationResult,
        onNameValueChange = { },
        internalId = viewModel.internalId,
        internalIdValidationResult = viewModel.internalIdValidationResult,
        onInternalIdValueChange = {},
        address = viewModel.address,
        onAutoDetectLocationClick = onLocationRequested,
        optionalAddress = viewModel.optionalAddress,
        onOptionalAddressChange = {}
    )
}

@Composable
private fun BranchFormScreenContent(
    companies: List<Company>,
    selectedCompany: Company?,
    onCompanySelected: (Company) -> Unit,
    name: StateFlow<String>,
    nameValidationResult: StateFlow<ValidationResult>,
    onNameValueChange: (String) -> Unit,
    internalId: StateFlow<String>,
    internalIdValidationResult: StateFlow<ValidationResult>,
    onInternalIdValueChange: (String) -> Unit,
    address: StateFlow<Address>,
    onAutoDetectLocationClick: () -> Unit,
    optionalAddress: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompanyDropDownMenuBox(
            value = selectedCompany,
            companies = companies,
            onCompanyPicked = onCompanySelected
        )

        ValidationTextField(
            valueState = name,
            validationState = nameValidationResult,
            label = "Name",
            onValueChange = onNameValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        ValidationTextField(
            valueState = internalId,
            validationState = internalIdValidationResult,
            label = "Internal ID",
            onValueChange = onInternalIdValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Address", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onAutoDetectLocationClick) {
                Icon(imageVector = Icons.Outlined.MyLocation, contentDescription = "Add Address")
            }
        }

        BranchAddressContent(addressState = address)

        Text(text = "Optional Address Information", style = MaterialTheme.typography.headlineSmall)
        BranchOptionalAddressContent(
            optionalAddressState = optionalAddress,
            onOptionalAddressChange = onOptionalAddressChange
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyDropDownMenuBox(
    value: Company?,
    companies: List<Company>,
    modifier: Modifier = Modifier,
    onCompanyPicked: (Company) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            value = value?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Company") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            companies.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        onCompanyPicked(it)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchAddressContent(addressState: StateFlow<Address>) {
    val address by addressState.collectAsState()
    TextField(
        value = address.countryName ?: "",
        onValueChange = {},
        label = { Text("Country") },
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = address.adminArea ?: "",
        onValueChange = {},
        label = { Text("Governate") },
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = address.subAdminArea ?: "",
            onValueChange = {},
            label = { Text("City") },
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = address.postalCode ?: "",
            onValueChange = {},
            label = { Text("PostalCode") },
            modifier = Modifier.weight(1f)
        )

    }
    TextField(
        value = address.thoroughfare ?: "",
        onValueChange = {},
        label = { Text("Street") },
        modifier = Modifier.fillMaxWidth()
    )

    TextField(
        value = address.featureName ?: "",
        onValueChange = {},
        label = { Text("Building") },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchOptionalAddressContent(
    optionalAddressState: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit
) {
    val optionalAddress by optionalAddressState.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = optionalAddress.room,
            label = { Text("Room") },
            onValueChange = {},
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = optionalAddress.floor,
            label = { Text("Floor") },
            onValueChange = {},
            modifier = Modifier.weight(1f)
        )
    }

    TextField(
        value = optionalAddress.additionalInformation,
        label = { Text("Additional Info") },
        onValueChange = {},
        modifier = Modifier.fillMaxWidth()
    )

}

@Preview(showBackground = true)
@Composable
fun BranchFormScreenPreview() {
    val companies = List(4) { Company("company", "123", "name", "012", null) }
    val selectedCompany = companies[0]
    val name = MutableStateFlow("")
    val nameValidationResult = MutableStateFlow(ValidationResult.Valid)
    val internalId = MutableStateFlow("")
    val internalIdValidationResult = MutableStateFlow(ValidationResult.Valid)
    val address = MutableStateFlow(
        Address(Locale.ENGLISH).apply {
            countryName = "Egypt"
            adminArea = "Cairo"
            subAdminArea = "Nasr City"
            thoroughfare = "El-Maadi"
            featureName = "El-Maadi"
            postalCode = "12345"
        }
    )
    val optionalAddress = MutableStateFlow(OptionalAddress("", "", "", "", ""))
    BranchFormScreenContent(
        companies = companies,
        selectedCompany = selectedCompany,
        onCompanySelected = {},
        name = name,
        nameValidationResult = nameValidationResult,
        onNameValueChange = {},
        internalId = internalId,
        internalIdValidationResult = internalIdValidationResult,
        onInternalIdValueChange = {},
        address = address,
        optionalAddress = optionalAddress,
        onOptionalAddressChange = {},
        onAutoDetectLocationClick = {}
    )
}
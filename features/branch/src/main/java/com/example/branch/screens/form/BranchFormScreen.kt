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
import com.example.models.OptionalAddress
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.*
import com.example.models.company.Company
import com.example.models.company.CompanySettings
import com.example.models.company.empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun BranchFormScreen(
    latitude: Double? = null,
    longitude: Double? = null,
    branchId: String,
    onShowSnackbarEvent: (SnackBarEvent) -> Unit,
    onLocationRequested: () -> Unit = {},
) {
    val viewModel: BranchFormViewModel by viewModel(parameters = { parametersOf(branchId) })
    if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
        val address = Geocoder(LocalContext.current)
            .getFromLocation(latitude, longitude, 1)?.first()

        if (address != null)
            viewModel.setAddress(address)
    }
    BranchFormScreenContent(
        companies = viewModel.companies,
        selectedCompany = viewModel.selectedCompany,
        onCompanySelected = viewModel::setSelectedCompany,
        name = viewModel.name,
        nameValidationResult = viewModel.nameValidationResult,
        onNameValueChange = viewModel::setName,
        internalId = viewModel.internalId,
        internalIdValidationResult = viewModel.internalIdValidationResult,
        onInternalIdValueChange = viewModel::setInternalId,
        address = viewModel.address,
        onCityChange = viewModel::setRegionCity,
        onStreetChange = viewModel::setStreet,
        onCountryChange = viewModel::setCountry,
        onGovernorateChange = viewModel::setGovernate,
        onPostalCodeChange = viewModel::setPostalCode,
        onAutoDetectLocationClick = onLocationRequested,
        optionalAddress = viewModel.optionalAddress,
        onOptionalAddressChange = viewModel::setOptionalAddress,
        isSaveButtonEnabled = viewModel.isFormValid,
        isLoading = viewModel.isLoading,
        onSaveClick = {
            viewModel.saveBranch { result ->
                if (result is Result.Success) {
                    onShowSnackbarEvent(SnackBarEvent("Branch saved successfully"))
                } else {
                    val snackBarEvent = SnackBarEvent(
                        (result as? Result.Error)?.exception ?: "Error",
                        actionLabel = "Retry",
                    ) {
                        viewModel.saveBranch { }
                    }
                    onShowSnackbarEvent(snackBarEvent)
                }

            }
        }
    )
}

@Composable
private fun BranchFormScreenContent(
    companies: StateFlow<List<Company>>,
    selectedCompany: StateFlow<Company?>,
    onCompanySelected: (Company) -> Unit,
    name: StateFlow<String>,
    nameValidationResult: StateFlow<ValidationResult>,
    onNameValueChange: (String) -> Unit,
    internalId: StateFlow<String>,
    internalIdValidationResult: StateFlow<ValidationResult>,
    onInternalIdValueChange: (String) -> Unit,
    address: StateFlow<Address>,
    onCountryChange: (String) -> Unit,
    onGovernorateChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    onAutoDetectLocationClick: () -> Unit,
    optionalAddress: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit,
    isSaveButtonEnabled: StateFlow<Boolean>,
    isLoading: StateFlow<Boolean>,
    onSaveClick: () -> Unit = {},
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

        AddressComposable(
            addressState = address,
            onCountryChange = onCountryChange,
            onGovernorateChange = onGovernorateChange,
            onCityChange = onCityChange,
            onStreetChange = onStreetChange,
            onPostalCodeChange = onPostalCodeChange
        )

        Text(text = "Optional Address Information", style = MaterialTheme.typography.headlineSmall)
        OptionalAddressComposable(
            optionalAddressState = optionalAddress,
            onOptionalAddressChange = onOptionalAddressChange
        )

        OneTimeEventButton(
            enabled = isSaveButtonEnabled,
            loading = isLoading,
            label = "Save",
            onClick = onSaveClick,
            modifier = Modifier.align(Alignment.End)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun BranchFormScreenPreview() {
    val companies = List(4) {
        Company(
            "company",
            "123",
            "name",
            "012",
            null,
            settings = CompanySettings.empty()
        )
    }
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
        companies = MutableStateFlow(companies),
        selectedCompany = MutableStateFlow(selectedCompany),
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
        onAutoDetectLocationClick = {},
        onCountryChange = {},
        onGovernorateChange = {},
        onCityChange = {},
        onStreetChange = {},
        onPostalCodeChange = {},
        isLoading = MutableStateFlow(false),
        onSaveClick = {},
        isSaveButtonEnabled = MutableStateFlow(true)
    )
}
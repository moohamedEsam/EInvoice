package com.example.client.screens.form

import android.location.Address
import android.location.Geocoder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.*
import com.example.einvoicecomponents.textField.ValidationOutlinedTextField
import com.example.models.company.Company
import com.example.models.OptionalAddress
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun ClientFormScreen(
    clientId: String,
    lat: Double,
    lng: Double,
    onLocationRequested: () -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onClientCreated: (String) -> Unit,
) {
    val viewModel: ClientFormViewModel by viewModel { parametersOf(clientId) }
    if (lat != 0.0 && lng != 0.0) {
        val address =
            Geocoder(LocalContext.current, Locale.getDefault()).getFromLocation(lat, lng, 1)
                ?.first()
        if (address != null)
            viewModel.setAddress(address)
    }
    ClientFormScreenContent(
        nameState = viewModel.name,
        nameValidationResultState = viewModel.nameValidationResult,
        businessTypeState = viewModel.businessType,
        taxStatusState = viewModel.taxStatus,
        onNameChange = viewModel::setName,
        emailState = viewModel.email,
        emailValidationResultState = viewModel.emailValidationResult,
        onEmailChange = viewModel::setEmail,
        phoneState = viewModel.phone,
        phoneValidationResultState = viewModel.phoneValidationResult,
        onPhoneChange = viewModel::setPhone,
        registrationNumberState = viewModel.registrationNumber,
        registrationNumberValidationResultState = viewModel.registrationNumberValidationResult,
        onRegistrationNumberChange = viewModel::setRegistrationNumber,
        addressState = viewModel.address,
        onCountryChange = viewModel::setCountry,
        onCityChange = viewModel::setRegionCity,
        onStreetChange = viewModel::setStreet,
        onGovernorateChange = viewModel::setGovernate,
        onPostalCodeChange = viewModel::setPostalCode,
        optionalAddressState = viewModel.optionalAddress,
        onOptionalAddressChange = viewModel::setOptionalAddress,
        onBusinessTypeChange = viewModel::setBusinessType,
        onTaxStatusChange = viewModel::setTaxStatus,
        companiesState = viewModel.companies,
        selectedCompanyState = viewModel.selectedCompany,
        onCompanySelected = viewModel::setSelectedCompany,
        companyFilterCriteria = { company, query -> company.name.contains(query, true) },
        onLocationRequested = onLocationRequested,
        isLoadingState = viewModel.isLoading,
        isEnabledState = viewModel.isFormValid,
        onFormSubmitted = {
            viewModel.saveClient { result ->
                val event = if (result is Result.Success) {
                    onClientCreated(result.data.id)
                    SnackBarEvent("Client saved successfully")
                } else
                    SnackBarEvent(
                        message = (result as? Result.Error)?.exception ?: " Error saving client",
                        actionLabel = "Retry",
                        action = { viewModel.saveClient {} }
                    )

                onShowSnackBarEvent(event)

            }
        },
    )
}

@Composable
private fun ClientFormScreenContent(
    nameState: StateFlow<String>,
    nameValidationResultState: StateFlow<ValidationResult>,
    onNameChange: (String) -> Unit,
    emailState: StateFlow<String>,
    emailValidationResultState: StateFlow<ValidationResult>,
    onEmailChange: (String) -> Unit,
    phoneState: StateFlow<String>,
    phoneValidationResultState: StateFlow<ValidationResult>,
    onPhoneChange: (String) -> Unit,
    registrationNumberState: StateFlow<String>,
    onRegistrationNumberChange: (String) -> Unit,
    registrationNumberValidationResultState: StateFlow<ValidationResult>,
    addressState: StateFlow<Address>,
    onCountryChange: (String) -> Unit,
    onGovernorateChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    optionalAddressState: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit,
    businessTypeState: StateFlow<BusinessType>,
    onBusinessTypeChange: (BusinessType) -> Unit,
    taxStatusState: StateFlow<TaxStatus>,
    onTaxStatusChange: (TaxStatus) -> Unit,
    companiesState: StateFlow<List<Company>>,
    selectedCompanyState: StateFlow<Company?>,
    onCompanySelected: (Company) -> Unit,
    companyFilterCriteria: (Company, String) -> Boolean,
    onLocationRequested: () -> Unit,
    isLoadingState: StateFlow<Boolean>,
    isEnabledState: StateFlow<Boolean>,
    onFormSubmitted: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Main Information", style = MaterialTheme.typography.headlineSmall)

            CompanyDropDownMenuBox(
                value = selectedCompanyState,
                companies = companiesState,
                onCompanyPicked = onCompanySelected,
                filterCriteria = companyFilterCriteria
            )

            ValidationOutlinedTextField(
                valueState = nameState,
                validationState = nameValidationResultState,
                label = "Name",
                onValueChange = onNameChange
            )

            ValidationOutlinedTextField(
                valueState = phoneState,
                validationState = phoneValidationResultState,
                label = "Phone",
                onValueChange = onPhoneChange
            )

            ValidationOutlinedTextField(
                valueState = registrationNumberState,
                validationState = registrationNumberValidationResultState,
                label = "Registration Number",
                onValueChange = onRegistrationNumberChange
            )

            ValidationOutlinedTextField(
                valueState = emailState,
                validationState = emailValidationResultState,
                label = "Email",
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            ClientTaxStatusRow(
                taxStatusState = taxStatusState,
                onTaxStatusChange = onTaxStatusChange
            )

            ClientBusinessTypeRow(
                businessTypeState = businessTypeState,
                onBusinessTypeChange = onBusinessTypeChange
            )
            ClientAddress(
                businessTypeState = businessTypeState,
                onLocationRequested = onLocationRequested,
                addressState = addressState,
                onCountryChange = onCountryChange,
                onGovernorateChange = onGovernorateChange,
                onCityChange = onCityChange,
                onStreetChange = onStreetChange,
                onPostalCodeChange = onPostalCodeChange,
                optionalAddressState = optionalAddressState,
                onOptionalAddressChange = onOptionalAddressChange
            )
        }
        OneTimeEventFloatingButton(
            enabled = isEnabledState,
            loading = isLoadingState,
            label = "Save",
            onClick = onFormSubmitted,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }

}

@Composable
private fun ClientAddress(
    businessTypeState: StateFlow<BusinessType>,
    onLocationRequested: () -> Unit,
    addressState: StateFlow<Address>,
    onCountryChange: (String) -> Unit,
    onGovernorateChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    optionalAddressState: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit
) {
    val businessType by businessTypeState.collectAsState()

    val isVisible = remember {
        MutableTransitionState(true)
    }

    LaunchedEffect(key1 = businessType) {
        isVisible.targetState = businessType == BusinessType.B
    }
    AnimatedVisibility(
        visibleState = isVisible,
//        enter = fadeIn(animationSpec = tween(1000)),
//        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.headlineSmall
                )

                IconButton(onClick = onLocationRequested) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Location"
                    )
                }
            }
            AddressComposable(
                addressState = addressState,
                onCountryChange = onCountryChange,
                onGovernorateChange = onGovernorateChange,
                onCityChange = onCityChange,
                onStreetChange = onStreetChange,
                onPostalCodeChange = onPostalCodeChange
            )

            Text(text = "Optional Address", style = MaterialTheme.typography.headlineSmall)
            OptionalAddressComposable(
                optionalAddressState = optionalAddressState,
                onOptionalAddressChange = onOptionalAddressChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientTaxStatusRow(
    taxStatusState: StateFlow<TaxStatus>,
    onTaxStatusChange: (TaxStatus) -> Unit
) {
    val taxStatus by taxStatusState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Tax Status", style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaxStatus.values().forEach {
                FilterChip(
                    onClick = { onTaxStatusChange(it) },
                    label = { Text(it.toString()) },
                    selected = taxStatus == it
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientBusinessTypeRow(
    businessTypeState: StateFlow<BusinessType>,
    onBusinessTypeChange: (BusinessType) -> Unit
) {
    val businessType by businessTypeState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Business Type", style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BusinessType.values().forEach {
                FilterChip(
                    onClick = { onBusinessTypeChange(it) },
                    label = { Text(it.asString()) },
                    selected = businessType == it
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ClientFormScreenPreview() {
    ClientFormScreenContent(
        nameState = MutableStateFlow(""),
        onNameChange = {},
        emailState = MutableStateFlow(""),
        onEmailChange = {},
        phoneState = MutableStateFlow(""),
        onPhoneChange = {},
        addressState = MutableStateFlow(Address(Locale.ENGLISH).apply {
            countryName = "Egypt"
            adminArea = "Cairo"
            subAdminArea = "Nasr City"
            thoroughfare = "El-Maadi"
            featureName = "El-Maadi"
            postalCode = "12345"
        }),
        optionalAddressState = MutableStateFlow(OptionalAddress("", "", "", "", "")),
        onOptionalAddressChange = {},
        businessTypeState = MutableStateFlow(BusinessType.B),
        onBusinessTypeChange = {},
        taxStatusState = MutableStateFlow(TaxStatus.Taxable),
        onTaxStatusChange = {},
        companiesState = MutableStateFlow(emptyList()),
        selectedCompanyState = MutableStateFlow(null),
        onCompanySelected = {},
        nameValidationResultState = MutableStateFlow(ValidationResult.Valid),
        emailValidationResultState = MutableStateFlow(ValidationResult.Valid),
        phoneValidationResultState = MutableStateFlow(ValidationResult.Valid),
        onLocationRequested = {},
        registrationNumberState = MutableStateFlow(""),
        onRegistrationNumberChange = {},
        registrationNumberValidationResultState = MutableStateFlow(ValidationResult.Valid),
        onFormSubmitted = {},
        isLoadingState = MutableStateFlow(false),
        isEnabledState = MutableStateFlow(true),
        onCountryChange = {},
        onGovernorateChange = {},
        onCityChange = {},
        onStreetChange = {},
        onPostalCodeChange = {},
        companyFilterCriteria = { _, _ -> true }
    )

}
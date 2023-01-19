package com.example.client.screens.form

import android.location.Address
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.*
import com.example.models.Company
import com.example.models.OptionalAddress
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@Composable
fun ClientFormScreen() {

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
    addressState: StateFlow<Address>,
    onAddressChange: (Address) -> Unit,
    optionalAddressState: StateFlow<OptionalAddress>,
    onOptionalAddressChange: (OptionalAddress) -> Unit,
    businessTypeState: StateFlow<BusinessType>,
    onBusinessTypeChange: (BusinessType) -> Unit,
    taxStatusState: StateFlow<TaxStatus>,
    onTaxStatusChange: (TaxStatus) -> Unit,
    companiesState: StateFlow<List<Company>>,
    selectedCompanyState: StateFlow<Company?>,
    onCompanySelected: (Company) -> Unit,
    onLocationRequested: () -> Unit,
) {
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
            onCompanyPicked = onCompanySelected
        )

        ValidationTextField(
            valueState = nameState,
            validationState = nameValidationResultState,
            label = "Name",
            onValueChange = onNameChange
        )

        ValidationTextField(
            valueState = emailState,
            validationState = emailValidationResultState,
            label = "Email",
            onValueChange = onEmailChange
        )

        ValidationTextField(
            valueState = phoneState,
            validationState = phoneValidationResultState,
            label = "Phone",
            onValueChange = onPhoneChange
        )
        ClientTaxStatusRow(
            taxStatusState = taxStatusState,
            onTaxStatusChange = onTaxStatusChange
        )

        ClientBusinessTypeRow(
            businessTypeState = businessTypeState,
            onBusinessTypeChange = onBusinessTypeChange
        )
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
        AddressComposable(addressState = addressState)
        Text(text = "Optional Address", style = MaterialTheme.typography.headlineSmall)
        OptionalAddressComposable(
            optionalAddressState = optionalAddressState,
            onOptionalAddressChange = onOptionalAddressChange
        )


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
        onAddressChange = {},
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
        onLocationRequested = {}
    )

}
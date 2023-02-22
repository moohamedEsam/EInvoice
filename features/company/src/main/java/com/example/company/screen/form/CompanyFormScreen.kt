package com.example.company.screen.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.OneTimeEventFloatingButton
import com.example.einvoicecomponents.textField.EInvoiceOutlinedTextField
import com.example.einvoicecomponents.textField.ValidationOutlinedTextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CompanyFormScreen(companyId: String) {
    val viewModel: CompanyFormViewModel by viewModel { parametersOf(companyId) }

    CompanyFormScreenContent(
        nameState = viewModel.name,
        onNameChange = viewModel::setName,
        nameValidation = viewModel.nameValidationResult,
        registrationNumberState = viewModel.registrationNumber,
        onRegistrationNumberChange = viewModel::setRegistrationNumber,
        registrationNumberValidation = viewModel.registrationNumberValidationResult,
        ceoState = viewModel.ceo,
        onCeoChange = viewModel::setCeo,
        websiteState = viewModel.website,
        onWebsiteChange = viewModel::setWebsite,
        websiteValidation = viewModel.websiteValidationResult,
        phoneNumberState = viewModel.phone,
        onPhoneNumberChange = viewModel::setPhone,
        clientIdState = viewModel.clientId,
        clientIdValidationResult = viewModel.clientIdValidationResult,
        onClientIdChange = viewModel::setClientId,
        clientSecretState = viewModel.clientSecret,
        clientSecretValidationResult = viewModel.clientSecretValidationResult,
        onClientSecretChange = viewModel::setClientSecret,
        tokenPinState = viewModel.tokenPin,
        tokenPinValidationResult = viewModel.tokenPinValidationResult,
        onTokenPinChange = viewModel::setTokenPin,
        taxActivityCodeState = viewModel.taxActivityCode,
        taxActivityCodeValidationResult = viewModel.taxActivityCodeValidationResult,
        onTaxPayerActivityCodeChange = viewModel::setTaxPayerActivityCode,
        isFormValid = viewModel.isFormValid,
        onCreateClick = viewModel::saveCompany,
    )
}


@Composable
private fun CompanyFormScreenContent(
    nameState: StateFlow<String>,
    onNameChange: (String) -> Unit,
    nameValidation: StateFlow<ValidationResult>,
    registrationNumberState: StateFlow<String>,
    onRegistrationNumberChange: (String) -> Unit,
    registrationNumberValidation: StateFlow<ValidationResult>,
    ceoState: StateFlow<String>,
    onCeoChange: (String) -> Unit,
    websiteState: StateFlow<String>,
    onWebsiteChange: (String) -> Unit,
    websiteValidation: StateFlow<ValidationResult>,
    phoneNumberState: StateFlow<String>,
    onPhoneNumberChange: (String) -> Unit,
    clientIdState: StateFlow<String>,
    clientIdValidationResult: StateFlow<ValidationResult>,
    onClientIdChange: (String) -> Unit,
    clientSecretState: StateFlow<String>,
    clientSecretValidationResult: StateFlow<ValidationResult>,
    onClientSecretChange: (String) -> Unit,
    tokenPinState: StateFlow<String>,
    tokenPinValidationResult: StateFlow<ValidationResult>,
    onTokenPinChange: (String) -> Unit,
    taxActivityCodeState: StateFlow<String>,
    taxActivityCodeValidationResult: StateFlow<ValidationResult>,
    onTaxPayerActivityCodeChange: (String) -> Unit,
    isFormValid: StateFlow<Boolean>,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Main Information", style = MaterialTheme.typography.headlineSmall)
            ValidationOutlinedTextField(
                valueState = nameState,
                validationState = nameValidation,
                label = "Name",
                onValueChange = onNameChange
            )

            ValidationOutlinedTextField(
                valueState = registrationNumberState,
                validationState = registrationNumberValidation,
                label = "Registration number",
                onValueChange = onRegistrationNumberChange
            )


            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)

            ValidationOutlinedTextField(
                valueState = clientIdState,
                validationState = clientIdValidationResult,
                label = "Client ID",
                onValueChange = onClientIdChange
            )

            ValidationOutlinedTextField(
                valueState = clientSecretState,
                validationState = clientSecretValidationResult,
                label = "Client secret",
                onValueChange = onClientSecretChange
            )

            ValidationOutlinedTextField(
                valueState = tokenPinState,
                validationState = tokenPinValidationResult,
                label = "Token pin",
                onValueChange = onTokenPinChange
            )

            ValidationOutlinedTextField(
                valueState = taxActivityCodeState,
                validationState = taxActivityCodeValidationResult,
                label = "Tax activity code",
                onValueChange = onTaxPayerActivityCodeChange
            )
            Text(text = "Optional", style = MaterialTheme.typography.headlineSmall)

            ValidationOutlinedTextField(
                valueState = websiteState,
                validationState = websiteValidation,
                label = "Website",
                onValueChange = onWebsiteChange
            )


            EInvoiceOutlinedTextField(
                valueState = ceoState,
                label = "CEO",
                onValueChange = onCeoChange,
                modifier = Modifier.fillMaxWidth()
            )

            EInvoiceOutlinedTextField(
                valueState = phoneNumberState,
                label = "Phone number",
                onValueChange = onPhoneNumberChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        OneTimeEventFloatingButton(
            enabled = isFormValid,
            loading = MutableStateFlow(false),
            label = "Save",
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateCompanyScreenPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CompanyFormScreenContent(
            nameState = MutableStateFlow(""),
            onNameChange = {},
            nameValidation = MutableStateFlow(ValidationResult.Valid),
            registrationNumberState = MutableStateFlow(""),
            onRegistrationNumberChange = {},
            registrationNumberValidation = MutableStateFlow(ValidationResult.Valid),
            ceoState = MutableStateFlow(""),
            onCeoChange = {},
            websiteState = MutableStateFlow(""),
            onWebsiteChange = {},
            websiteValidation = MutableStateFlow(ValidationResult.Valid),
            phoneNumberState = MutableStateFlow(""),
            onPhoneNumberChange = {},
            isFormValid = MutableStateFlow(true),
            onCreateClick = {},
            clientIdState = MutableStateFlow(""),
            onClientIdChange = {},
            clientSecretState = MutableStateFlow(""),
            onClientSecretChange = {},
            tokenPinState = MutableStateFlow(""),
            onTokenPinChange = {},
            taxActivityCodeState = MutableStateFlow(""),
            onTaxPayerActivityCodeChange = {},
            clientIdValidationResult = MutableStateFlow(ValidationResult.Valid),
            clientSecretValidationResult = MutableStateFlow(ValidationResult.Valid),
            tokenPinValidationResult = MutableStateFlow(ValidationResult.Valid),
            taxActivityCodeValidationResult = MutableStateFlow(ValidationResult.Valid),
        )

    }
}

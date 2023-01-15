package com.example.company.screen.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.ValidationOutlinedTextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CompanyFormScreenContent(
    nameState: StateFlow<String>,
    onNameChange: (String) -> Unit,
    nameValidation: StateFlow<ValidationResult>,
    registrationNumberState: StateFlow<String>,
    onRegistrationNumberChange: (String) -> Unit,
    registrationNumberValidation: StateFlow<ValidationResult>,
    ceoState: StateFlow<String>,
    onCeoChange: (String) -> Unit,
    ceoValidation: StateFlow<ValidationResult>,
    websiteState: StateFlow<String>,
    onWebsiteChange: (String) -> Unit,
    websiteValidation: StateFlow<ValidationResult>,
    phoneNumberState: StateFlow<String>,
    onPhoneNumberChange: (String) -> Unit,
    phoneNumberValidation: StateFlow<ValidationResult>,
    isFormValid: StateFlow<Boolean>,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

        ValidationOutlinedTextField(
            valueState = ceoState,
            validationState = ceoValidation,
            label = "CEO",
            onValueChange = onCeoChange
        )

        ValidationOutlinedTextField(
            valueState = websiteState,
            validationState = websiteValidation,
            label = "Website",
            onValueChange = onWebsiteChange
        )

        ValidationOutlinedTextField(
            valueState = phoneNumberState,
            validationState = phoneNumberValidation,
            label = "Phone number",
            onValueChange = onPhoneNumberChange
        )

        CreateCompanyButton(
            isFormValid = isFormValid,
            onCreateClick = onCreateClick,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun CreateCompanyButton(
    isFormValid: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit
) {
    val isEnabled by isFormValid.collectAsState()
    Button(
        onClick = onCreateClick,
        modifier = modifier,
        enabled = isEnabled
    ) {
        Text(text = "Save")
    }
}

@Preview(showBackground = true)
@Composable
fun CreateCompanyScreenPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Create company screen")
        Dialog(onDismissRequest = { }) {
            Card {
                CompanyFormScreenContent(
                    nameState = MutableStateFlow(""),
                    onNameChange = {},
                    nameValidation = MutableStateFlow(ValidationResult.Empty),
                    registrationNumberState = MutableStateFlow(""),
                    onRegistrationNumberChange = {},
                    registrationNumberValidation = MutableStateFlow(ValidationResult.Empty),
                    ceoState = MutableStateFlow(""),
                    onCeoChange = {},
                    ceoValidation = MutableStateFlow(ValidationResult.Empty),
                    websiteState = MutableStateFlow(""),
                    onWebsiteChange = {},
                    websiteValidation = MutableStateFlow(ValidationResult.Empty),
                    phoneNumberState = MutableStateFlow(""),
                    onPhoneNumberChange = {},
                    phoneNumberValidation = MutableStateFlow(ValidationResult.Empty),
                    isFormValid = MutableStateFlow(true),
                    onCreateClick = {}
                )
            }
        }
    }
}

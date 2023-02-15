package com.example.einvoicecomponents.textField

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.common.models.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationOutlinedTextField(
    valueState: StateFlow<String>,
    validationState: StateFlow<ValidationResult>,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    val value by valueState.collectAsState()
    val validation by validationState.collectAsState()
    ValidationTextFieldContainer(modifier = modifier, validation = validation) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("$label*") },
            isError = validation is ValidationResult.Invalid,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                ValidationTrailingIcon(validation)
            },
            leadingIcon = leadingIcon,
            colors = colors,
            keyboardOptions = keyboardOptions,
        )
    }
}

@Preview
@Composable
fun ValidationOutlinedTextFieldPreview() {
    ValidationOutlinedTextField(
        valueState = MutableStateFlow(""),
        validationState = MutableStateFlow(ValidationResult.Valid),
        label = "Email",
        onValueChange = {}
    )
}
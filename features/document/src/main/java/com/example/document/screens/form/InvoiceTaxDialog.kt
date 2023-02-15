package com.example.document.screens.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.BaseExposedDropDownMenu
import com.example.einvoicecomponents.textField.ValidationOutlinedTextField
import com.example.einvoicecomponents.textField.ValidationTextFieldContainer
import com.example.models.invoiceLine.SubTax
import com.example.models.invoiceLine.TaxView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InvoiceTaxDialog(
    taxViewState: StateFlow<TaxView?>,
    onTaxChange: (TaxView) -> Unit,
    subTaxState: StateFlow<SubTax?>,
    onSubTaxChange: (SubTax) -> Unit,
    taxRateState: StateFlow<String>,
    taxRateValidationResult: StateFlow<ValidationResult>,
    onTaxRateChange: (String) -> Unit,
    onSaveTax: () -> Unit,
    availableTaxes: StateFlow<List<TaxView>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier.animateContentSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Add Invoice Taxes", style = MaterialTheme.typography.headlineMedium)
                InvoiceTaxDialogContent(
                    availableTaxesState = availableTaxes,
                    onSaveTax = onSaveTax,
                    taxViewState = taxViewState,
                    onTaxChange = onTaxChange,
                    subTaxState = subTaxState,
                    onSubTaxChange = onSubTaxChange,
                    taxRateState = taxRateState,
                    onTaxRateChange = onTaxRateChange,
                    taxRateValidationResultState = taxRateValidationResult
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceTaxDialogContent(
    availableTaxesState: StateFlow<List<TaxView>>,
    taxViewState: StateFlow<TaxView?>,
    onTaxChange: (TaxView) -> Unit,
    subTaxState: StateFlow<SubTax?>,
    onSubTaxChange: (SubTax) -> Unit,
    taxRateState: StateFlow<String>,
    taxRateValidationResultState: StateFlow<ValidationResult>,
    onTaxRateChange: (String) -> Unit,
    onSaveTax: () -> Unit,
) {
    val newTax by taxViewState.collectAsState()
    val newTaxSubCode by subTaxState.collectAsState()

    Column {
        BaseExposedDropDownMenu(
            optionsState = availableTaxesState,
            selectedOptionState = MutableStateFlow(newTax),
            onOptionSelect = onTaxChange,
            textFieldValue = {
                if (it == null) ""
                else "${it.name} (${it.code})"
            },
            textFieldLabel = "Tax Code*",
            optionsLabel = { "${it.name} - ${it.code}" },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        BaseExposedDropDownMenu(
            optionsState = MutableStateFlow(newTax?.subTaxes ?: emptyList()),
            selectedOptionState = MutableStateFlow(newTaxSubCode),
            onOptionSelect = onSubTaxChange,
            textFieldValue = {
                if (it == null) ""
                else "${it.name} (${it.code})"
            },
            textFieldLabel = "Tax Sub Code*",
            optionsLabel = { "${it.name} - ${it.code}" },
            textStyle = MaterialTheme.typography.bodyMedium
        )
        ValidationOutlinedTextField(
            valueState = taxRateState,
            validationState = taxRateValidationResultState,
            label = "Tax Rate",
            onValueChange = onTaxRateChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Button(
            onClick = onSaveTax,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceTaxDialogPreview() {
    InvoiceTaxDialog(
        taxViewState = MutableStateFlow(null),
        onTaxChange = {},
        subTaxState = MutableStateFlow(null),
        onSubTaxChange = {},
        taxRateState = MutableStateFlow(""),
        taxRateValidationResult = MutableStateFlow(ValidationResult.Valid),
        onTaxRateChange = {},
        onSaveTax = {},
        availableTaxes = MutableStateFlow(emptyList()),
        onDismiss = {}
    )
}
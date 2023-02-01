package com.example.item.screens.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.EInvoiceOutlinedTextField
import com.example.einvoicecomponents.OneTimeEventButton
import com.example.einvoicecomponents.ValidationOutlinedTextField
import com.example.models.Branch
import com.example.models.item.UnitType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ItemFormScreenContent(
    name: StateFlow<String>,
    onNameChange: (String) -> Unit,
    nameValidationResult: StateFlow<ValidationResult>,
    internalCode: StateFlow<String>,
    onInternalCodeChange: (String) -> Unit,
    internalCodeValidationResult: StateFlow<ValidationResult>,
    description: StateFlow<String>,
    onDescriptionChange: (String) -> Unit,
    price: StateFlow<String>,
    onPriceChange: (String) -> Unit,
    priceValidationResult: StateFlow<ValidationResult>,
    status: StateFlow<TaxStatus>,
    onStatusChange: (TaxStatus) -> Unit,
    itemCode: StateFlow<String>,
    onItemCodeChange: (String) -> Unit,
    itemCodeValidationResult: StateFlow<ValidationResult>,
    unitTypes: StateFlow<List<UnitType>>,
    selectedUnitType: StateFlow<UnitType?>,
    onUnitTypeChange: (UnitType) -> Unit,
    branches: StateFlow<List<Branch>>,
    selectedBranch: StateFlow<Branch?>,
    onBranchChange: (Branch) -> Unit,
    isEnabled: StateFlow<Boolean>,
    isLoading: StateFlow<Boolean>,
    onFormSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        BranchesExposedDropDownMenu(
            branchesState = branches,
            selectedBranchState = selectedBranch,
            onBranchChange = onBranchChange
        )

        ValidationOutlinedTextField(
            valueState = name,
            validationState = nameValidationResult,
            label = "Name",
            onValueChange = onNameChange
        )

        ValidationOutlinedTextField(
            valueState = internalCode,
            label = "Internal Code",
            onValueChange = onInternalCodeChange,
            validationState = internalCodeValidationResult
        )

        EInvoiceOutlinedTextField(
            valueState = description,
            label = "Description",
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth()
        )

        ValidationOutlinedTextField(
            valueState = price,
            validationState = priceValidationResult,
            label = "Price",
            onValueChange = onPriceChange
        )


        ValidationOutlinedTextField(
            valueState = itemCode,
            validationState = itemCodeValidationResult,
            label = "Item Code",
            onValueChange = onItemCodeChange
        )

        UnitTypeExposedDropDownMenu(
            unitTypesState = unitTypes,
            selectedUnitTypeState = selectedUnitType,
            onUnitTypeChange = onUnitTypeChange
        )

        TaxStatusRow(statusState = status, onStatusChange = onStatusChange)

        OneTimeEventButton(
            enabled = isEnabled,
            loading = isLoading,
            label = "Save",
            onClick = onFormSubmit,
            modifier = Modifier.align(Alignment.End)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchesExposedDropDownMenu(
    branchesState: StateFlow<List<Branch>>,
    selectedBranchState: StateFlow<Branch?>,
    onBranchChange: (Branch) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val branches by branchesState.collectAsState()
    val selectedBranch by selectedBranchState.collectAsState()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedBranch?.name ?: "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            label = { Text(text = "Branch") }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    onClick = {
                        onBranchChange(branch)
                        expanded = false
                    },
                    text = { Text(text = branch.name) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitTypeExposedDropDownMenu(
    unitTypesState: StateFlow<List<UnitType>>,
    selectedUnitTypeState: StateFlow<UnitType?>,
    onUnitTypeChange: (UnitType) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val unitTypes by unitTypesState.collectAsState()
    val selectedUnitType by selectedUnitTypeState.collectAsState()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedUnitType?.name ?: "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            label = { Text(text = "Unit Type") }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            unitTypes.forEach { unitType ->
                DropdownMenuItem(
                    onClick = {
                        onUnitTypeChange(unitType)
                        expanded = false
                    },
                    text = { Text(text = unitType.name) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaxStatusRow(
    statusState: StateFlow<TaxStatus>,
    onStatusChange: (TaxStatus) -> Unit
) {
    val status by statusState.collectAsState()
    Text(text = "Tax Status", style = MaterialTheme.typography.headlineSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = status == TaxStatus.Taxable,
            onClick = { onStatusChange(TaxStatus.Taxable) },
            label = { Text(text = "Taxable") }
        )

        FilterChip(
            selected = status == TaxStatus.NonTaxable,
            onClick = { onStatusChange(TaxStatus.NonTaxable) },
            label = { Text(text = "Non-Taxable") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemFormScreenPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Dialog(onDismissRequest = { }) {
            Card {
                ItemFormScreenContent(
                    name = MutableStateFlow(""),
                    onNameChange = {},
                    nameValidationResult = MutableStateFlow(ValidationResult.Valid),
                    description = MutableStateFlow(""),
                    onDescriptionChange = {},
                    price = MutableStateFlow(""),
                    onPriceChange = {},
                    priceValidationResult = MutableStateFlow(ValidationResult.Valid),
                    status = MutableStateFlow(TaxStatus.Taxable),
                    onStatusChange = {},
                    itemCode = MutableStateFlow(""),
                    onItemCodeChange = {},
                    itemCodeValidationResult = MutableStateFlow(ValidationResult.Valid),
                    unitTypes = MutableStateFlow(listOf()),
                    selectedUnitType = MutableStateFlow(null),
                    onUnitTypeChange = {},
                    branches = MutableStateFlow(listOf()),
                    selectedBranch = MutableStateFlow(null),
                    onBranchChange = {},
                    onFormSubmit = {},
                    isEnabled = MutableStateFlow(true),
                    isLoading = MutableStateFlow(false),
                    internalCode = MutableStateFlow(""),
                    onInternalCodeChange = {},
                    internalCodeValidationResult = MutableStateFlow(ValidationResult.Valid),
                )
            }
        }
    }
}
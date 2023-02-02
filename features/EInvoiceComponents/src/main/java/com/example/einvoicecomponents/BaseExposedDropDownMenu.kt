package com.example.einvoicecomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BaseExposedDropDownMenu(
    optionsState: StateFlow<List<T>>,
    selectedOptionState: StateFlow<T?>,
    onOptionSelect: (T) -> Unit,
    textFieldValue: (T?) -> String,
    textFieldLabel: String,
    optionsLabel: (T) -> String,
    modifier: Modifier = Modifier
) {

    val options by optionsState.collectAsState()
    val selectedOption by selectedOptionState.collectAsState()
    var isExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textFieldValue(selectedOption),
            onValueChange = {},
            label = { Text(text = textFieldLabel) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            options.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onOptionSelect(item)
                    },
                    text = { Text(optionsLabel(item)) }
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun BaseExposedDropDownMenuPreview() {
    BaseExposedDropDownMenu(
        optionsState = MutableStateFlow(listOf("Option 1", "Option 2", "Option 3")),
        selectedOptionState = MutableStateFlow("Option 2"),
        textFieldValue = { it ?: "" },
        textFieldLabel = "Label",
        optionsLabel = { it },
        onOptionSelect = { }
    )
}
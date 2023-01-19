package com.example.einvoicecomponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.models.Company
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDropDownMenuBox(
    value: StateFlow<Company?>,
    companies: StateFlow<List<Company>>,
    modifier: Modifier = Modifier,
    onCompanyPicked: (Company) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val selectedCompany by value.collectAsState()
    val companiesList by companies.collectAsState()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        TextField(
            value = selectedCompany?.name ?: "",
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
            companiesList.forEach {
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
package com.example.einvoicecomponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.models.company.Company
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CompanyDropDownMenuBox(
    value: StateFlow<Company?>,
    companies: StateFlow<List<Company>>,
    modifier: Modifier = Modifier,
    filterCriteria: (Company, String) -> Boolean,
    onCompanyPicked: (Company) -> Unit
) {
    BaseExposedDropDownMenu(
        optionsState = companies,
        selectedOptionState = value,
        onOptionSelect = onCompanyPicked,
        textFieldValue = { it?.name ?: "" },
        textFieldLabel = "Company",
        optionsLabel = { it.name },
        modifier = modifier,
        filterCriteria = filterCriteria
    )
}
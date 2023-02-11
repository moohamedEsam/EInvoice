package com.example.document.screens.form

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.*
import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Composable
fun GeneralPage(
    companies: StateFlow<List<Company>>,
    selectedCompany: StateFlow<Company?>,
    onCompanySelected: (Company) -> Unit,
    branches: StateFlow<List<Branch>>,
    selectedBranch: StateFlow<Branch?>,
    onBranchSelected: (Branch) -> Unit,
    clients: StateFlow<List<Client>>,
    selectedClient: StateFlow<Client?>,
    onClientSelected: (Client) -> Unit,
    internalId: StateFlow<String>,
    onInternalIdChanged: (String) -> Unit,
    internalIdValidationResult: StateFlow<ValidationResult>,
    createDate: StateFlow<Date>,
    onCreateDateChanged: (Date) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompanyDropDownMenuBox(
            value = selectedCompany,
            companies = companies,
            onCompanyPicked = onCompanySelected,
            filterCriteria = { company, query -> company.name.contains(query, true) },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BranchDropDownMenuBox(
                branchesState = branches,
                selectedBranchState = selectedBranch,
                modifier = Modifier.weight(1f),
                onBranchSelected = onBranchSelected
            )

            ClientsDropDownMenuBox(
                clientsState = clients,
                selectedClientState = selectedClient,
                modifier = Modifier.weight(1f),
                onClientSelected = onClientSelected
            )
        }

        ValidationTextField(
            valueState = internalId,
            validationState = internalIdValidationResult,
            label = "Internal ID",
            onValueChange = onInternalIdChanged
        )

        DocumentCreateDate(
            createDateState = createDate,
            onCreateDateChanged = onCreateDateChanged
        )
    }
}

@Composable
private fun DocumentCreateDate(
    createDateState: StateFlow<Date>,
    onCreateDateChanged: (Date) -> Unit
) {
    val createDate by createDateState.collectAsState()
    val simpleDateFormat by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
    }
    val dialogState = rememberMaterialDialogState()
    TextButton(onClick = dialogState::show) {
        Text("Create Date: ${simpleDateFormat.format(createDate)}")
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("OK") {}
            negativeButton("Cancel"){}
        }
    ) {
        datepicker(
            waitForPositiveButton = true,
            initialDate = createDate.toLocalDate(),
            onDateChange = { localDate ->
                onCreateDateChanged(localDate.toDate())
            },
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                headerTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            allowedDateValidator = {
                it.isBefore(LocalDate.now().plusDays(1))
            }
        )
    }
}


@Composable
private fun BranchDropDownMenuBox(
    branchesState: StateFlow<List<Branch>>,
    selectedBranchState: StateFlow<Branch?>,
    modifier: Modifier = Modifier,
    onBranchSelected: (Branch) -> Unit
) {
    BaseExposedDropDownMenu(
        optionsState = branchesState,
        selectedOptionState = selectedBranchState,
        onOptionSelect = onBranchSelected,
        textFieldValue = { it?.name ?: "" },
        textFieldLabel = "Branch",
        optionsLabel = { it.name },
        modifier = modifier,
        filterCriteria = { branch, query -> branch.name.contains(query, true) }
    )
}

@Composable
private fun ClientsDropDownMenuBox(
    clientsState: StateFlow<List<Client>>,
    selectedClientState: StateFlow<Client?>,
    modifier: Modifier = Modifier,
    onClientSelected: (Client) -> Unit
) {
    BaseExposedDropDownMenu(
        optionsState = clientsState,
        selectedOptionState = selectedClientState,
        onOptionSelect = onClientSelected,
        textFieldValue = { it?.name ?: "" },
        textFieldLabel = "Client",
        optionsLabel = { it.name },
        modifier = modifier,
        filterCriteria = { client, query -> client.name.contains(query, true) }
    )
}
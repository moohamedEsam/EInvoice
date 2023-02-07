package com.example.document.screens.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.CompanyDropDownMenuBox
import com.example.einvoicecomponents.ValidationTextField
import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.company.Company
import kotlinx.coroutines.flow.StateFlow

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
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompanyDropDownMenuBox(
            value = selectedCompany,
            companies = companies,
            onCompanyPicked = onCompanySelected
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
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BranchDropDownMenuBox(
    branchesState: StateFlow<List<Branch>>,
    selectedBranchState: StateFlow<Branch?>,
    modifier: Modifier = Modifier,
    onBranchSelected: (Branch) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val selectedBranch by selectedBranchState.collectAsState()
    val branches by branchesState.collectAsState()
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        TextField(
            value = selectedBranch?.name ?: "",
            onValueChange = {},
            label = { Text("Branch") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onBranchSelected(branch)
                    },
                    text = { Text(branch.name) },
                    modifier = Modifier.padding(ExposedDropdownMenuDefaults.ItemContentPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientsDropDownMenuBox(
    clientsState: StateFlow<List<Client>>,
    selectedClientState: StateFlow<Client?>,
    modifier: Modifier = Modifier,
    onClientSelected: (Client) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val selectedClient by selectedClientState.collectAsState()
    val clients by clientsState.collectAsState()
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        TextField(
            value = selectedClient?.name ?: "",
            onValueChange = {},
            label = { Text("Client") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            clients.forEach { client ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onClientSelected(client)
                    },
                    text = { Text(client.name) },
                )
            }
        }
    }
}

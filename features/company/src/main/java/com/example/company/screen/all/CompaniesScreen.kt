package com.example.company.screen.all

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.company.screen.form.CompanyFormScreenContent
import com.example.einvoicecomponents.ListScreenContent
import com.example.models.Company
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel

@Composable
fun CompaniesScreen(
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onCompanyClick: (Company) -> Unit
) {
    val viewModel: CompaniesViewModel by viewModel()
    val companies by viewModel.companies.collectAsState()
    val showDialog by viewModel.showCreateDialog.collectAsState()
    CompaniesScreenContent(
        companies = companies,
        onCompanyClick = onCompanyClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery,
        onCreateNewCompanyClick = viewModel::toggleCreateDialog,
        onCompanyEditClick = { viewModel.editCompany(it.id) },
        onCompanyDeleteClick = {
            viewModel.deleteCompany(it) { result ->
                val event = if (result is Result.Success)
                    SnackBarEvent("Company deleted successfully", actionLabel = "Undo") {
                        viewModel.undoDeleteCompany(it)
                    }
                else
                    SnackBarEvent(
                        (result as? Result.Error)?.exception ?: "Error Deleting company",
                        actionLabel = "Retry"
                    ) {
                        viewModel.deleteCompany(it)
                    }
                onShowSnackBarEvent(event)
            }
        }
    )
    if (showDialog)
        CreateCompanyDialog(viewModel, onShowSnackBarEvent)

}

@Composable
private fun CreateCompanyDialog(
    viewModel: CompaniesViewModel,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    Dialog(onDismissRequest = viewModel::toggleCreateDialog) {
        Card {
            CompanyFormScreenContent(
                nameState = viewModel.name,
                onNameChange = viewModel::setName,
                nameValidation = viewModel.nameValidationResult,
                registrationNumberState = viewModel.registrationNumber,
                onRegistrationNumberChange = viewModel::setRegistrationNumber,
                registrationNumberValidation = viewModel.registrationNumberValidationResult,
                ceoState = viewModel.ceo,
                onCeoChange = viewModel::setCeo,
                ceoValidation = viewModel.ceoValidationResult,
                websiteState = viewModel.website,
                onWebsiteChange = viewModel::setWebsite,
                websiteValidation = viewModel.websiteValidationResult,
                phoneNumberState = viewModel.phone,
                onPhoneNumberChange = viewModel::setPhone,
                phoneNumberValidation = viewModel.phoneValidationResult,
                isFormValid = viewModel.isFormValid,
                onCreateClick = {
                    viewModel.saveCompany { result ->
                        val event = if (result is Result.Success)
                            SnackBarEvent("Company Saved successfully")
                        else
                            SnackBarEvent(
                                (result as? Result.Error)?.exception ?: "Error creating company",
                                actionLabel = "Retry"
                            ) {
                                viewModel.saveCompany()
                            }
                        onShowSnackBarEvent(event)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompaniesScreenContent(
    modifier: Modifier = Modifier,
    companies: List<Company>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onCompanyClick: (Company) -> Unit,
    onCompanyEditClick: (Company) -> Unit,
    onCompanyDeleteClick: (Company) -> Unit,
    onCreateNewCompanyClick: () -> Unit = {}
) {

    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChange,
        floatingButtonText = "Create New Company",
        adaptiveItemSize = 250.dp,
        onFloatingButtonClick = onCreateNewCompanyClick,
        modifier = modifier
    ){
        items(companies, key = { it.id }) { company ->
            CompanyItem(
                company = company,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onCompanyClick(company) },
                onEditClick = { onCompanyEditClick(company) },
                onDeleteClick = { onCompanyDeleteClick(company) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyItem(
    company: Company,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = company.name, style = MaterialTheme.typography.headlineSmall, maxLines = 1)
            Text(
                text = "Registration number: ${company.registrationNumber}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            if (company.website != null) {
                val context = LocalContext.current
                Text(
                    text = "Website: ${company.website}",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(company.website))
                        context.startActivity(intent)
                    }
                )
            }
            Text(
                text = "Phone: ${company.phone}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Row(modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit company")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete company")
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompaniesScreenPreview() {
    val companies = List(10) {
        Company(
            id = it.toString(),
            name = "Company 1",
            registrationNumber = "123456789",
            website = "https://www.google.com",
            phone = "123456789",
            ceo = "John Doe",
        )
    }

    CompaniesScreenContent(
        companies = companies,
        modifier = Modifier.fillMaxSize(),
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onCompanyClick = {},
        onCompanyEditClick = {},
        onCompanyDeleteClick = {}
    ) {}

}
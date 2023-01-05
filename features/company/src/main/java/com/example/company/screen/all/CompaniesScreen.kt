package com.example.company.screen.all

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.components.OutlinedSearchTextField
import com.example.company.screen.form.CompanyFormScreenContent
import com.example.models.Company
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel

@Composable
fun CompaniesScreen(
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
        onCreateNewCompanyClick = viewModel::toggleCreateDialog
    )
    if (showDialog)
        CreateCompanyDialog(viewModel)

}

@Composable
private fun CreateCompanyDialog(viewModel: CompaniesViewModel) {
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
                onCreateClick = viewModel::createCompany
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
    onCreateNewCompanyClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
        val nestedScrollConnection = object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                expanded = available.y < 0
                return super.onPostScroll(consumed, available, source)
            }
        }
        CompanySearchTextField(queryState = queryState, onQueryChange = onQueryChange)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .nestedScroll(nestedScrollConnection)
        ) {
            items(companies, key = { it.id }) { company ->
                CompanyItem(
                    company = company,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(),
                    onClick = { onCompanyClick(company) }
                )
            }
        }
        CreateNewCompanyFloatingButton(onCreateNewCompanyClick) {
            expanded
        }
    }
}

@Composable
private fun ColumnScope.CreateNewCompanyFloatingButton(
    onCreateNewCompanyClick: () -> Unit,
    isExpanded: () -> Boolean
) {
    ExtendedFloatingActionButton(
        onClick = onCreateNewCompanyClick,
        modifier = Modifier.align(Alignment.End),
        text = { Text("Create new company") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Create new company") },
        expanded = isExpanded()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyItem(
    company: Company,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
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
        }
    }
}

@Composable
private fun CompanySearchTextField(queryState: StateFlow<String>, onQueryChange: (String) -> Unit) {
    OutlinedSearchTextField(
        queryState = queryState,
        onQueryChange = onQueryChange,
        label = "Search",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun CompaniesScreenPreview() {
    val companies = listOf(
        Company(
            id = "1",
            name = "Company 1",
            registrationNumber = "123456789",
            website = "https://www.google.com",
            phone = "123456789",
            ceo = "John Doe",
        ),
        Company(
            id = "2",
            name = "Company 2",
            registrationNumber = "123456789",
            website = "https://www.google.com",
            phone = "123456789",
            ceo = "John Doe",
        )
    )

    CompaniesScreenContent(
        companies = companies,
        modifier = Modifier.fillMaxSize(),
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onCompanyClick = {}
    ) {}

}
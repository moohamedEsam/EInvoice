package com.example.company.screen.all

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.einvoicecomponents.ListScreenContent
import com.example.models.company.Company
import com.example.models.company.CompanySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel

@Composable
fun CompaniesScreen(
    onCompanyClick: (Company) -> Unit,
    onCreateNewCompanyClick: () -> Unit,
    onCompanyEditClick: (Company) -> Unit,
) {
    val viewModel: CompaniesViewModel by viewModel()
    val companies by viewModel.companies.collectAsState()
    CompaniesScreenContent(
        companies = companies,
        onCompanyClick = onCompanyClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery,
        onCreateNewCompanyClick = onCreateNewCompanyClick,
        onCompanyEditClick = onCompanyEditClick
    )

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
    onCreateNewCompanyClick: () -> Unit = {}
) {

    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChange,
        floatingButtonText = "Create New Company",
        adaptiveItemSize = 250.dp,
        onFloatingButtonClick = onCreateNewCompanyClick,
        modifier = modifier.testTag("CompaniesScreenList")
    ){
        items(companies) { company ->
            CompanyItem(
                company = company,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onCompanyClick(company) },
                onEditClick = { onCompanyEditClick(company) },
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
) {
    OutlinedCard(modifier = modifier.testTag("CompaniesScreenCompanyItem"), onClick = onClick) {
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
//                    modifier = Modifier.clickable {
//                        openWebsiteInBrowser(context, company.website!!)
//                    }
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
            }
        }
    }
}

fun openWebsiteInBrowser(
    context: Context,
    url: String
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
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
            settings = CompanySettings(
                clientId = "123456789",
                clientSecret = "123",
                tokenPin = "123456",
                taxActivityCode = "123456789"
            )
        )
    }

    CompaniesScreenContent(
        companies = companies,
        modifier = Modifier.fillMaxSize(),
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onCompanyClick = {},
        onCompanyEditClick = {},
    ) {}

}
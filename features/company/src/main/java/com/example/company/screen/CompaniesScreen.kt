package com.example.company.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.components.OutlinedSearchTextField
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

    CompaniesScreenContent(
        companies = companies,
        onCompanyClick = onCompanyClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompaniesScreenContent(
    modifier: Modifier = Modifier,
    companies: List<Company>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onCompanyClick: (Company) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompanySearchTextField(queryState = queryState, onQueryChange = onQueryChange)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    }
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
        onQueryChange = {}) {}

}
package com.example.company.screen.all

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.einvoicecomponents.ListScreenContent
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.company.empty
import com.example.models.document.Document
import com.example.models.document.empty
import com.example.models.empty
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import kotlin.random.Random

@Composable
fun CompaniesScreen(
    onCompanyClick: (Company) -> Unit,
    onCreateNewCompanyClick: () -> Unit,
    onCompanyEditClick: (Company) -> Unit,
) {
    val viewModel: CompaniesViewModel by viewModel()
    val companies = viewModel.pager.collectAsLazyPagingItems()
    CompaniesScreenContent(
        companies = companies,
        onCompanyClick = onCompanyClick,
        queryState = viewModel.query,
        onQueryChange = viewModel::setQuery,
        onCreateNewCompanyClick = onCreateNewCompanyClick,
        onCompanyEditClick = onCompanyEditClick
    )

}

@Composable
private fun CompaniesScreenContent(
    modifier: Modifier = Modifier,
    companies: LazyPagingItems<CompanyView>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onCompanyClick: (Company) -> Unit,
    onCompanyEditClick: (Company) -> Unit,
    onCreateNewCompanyClick: () -> Unit = {}
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        items(companies) { companyView ->
            CompanyItem(
                companyView = companyView!!,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onCompanyClick(companyView.company) },
                onEditClick = { onCompanyEditClick(companyView.company) },
            )
        }
    }

//    ListScreenContent(
//        queryState = queryState,
//        onQueryChange = onQueryChange,
//        floatingButtonText = "Create New Company",
//        adaptiveItemSize = 250.dp,
//        onFloatingButtonClick = onCreateNewCompanyClick,
//        modifier = modifier.testTag("CompaniesScreenList")
//    ) {
//        items(companies) { companyView ->
//            CompanyItem(
//                companyView = companyView,
//                modifier = Modifier.fillMaxWidth(),
//                onClick = { onCompanyClick(companyView.company) },
//                onEditClick = { onCompanyEditClick(companyView.company) },
//            )
//        }
//    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyItem(
    companyView: CompanyView,
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
            Text(
                text = companyView.company.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            Divider()
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisAlignment = MainAxisAlignment.SpaceBetween,
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
            ) {
                Text(
                    text = "${companyView.branches.size} branches",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = "${companyView.clients.size} clients",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
            }
            Divider()
            Text(
                text = "${companyView.documents.size} invoices",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )

            AssistChip(
                onClick = onEditClick,
                label = { Text(text = ("Edit")) },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Edit") },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompaniesScreenPreview() {
    val companies = List(10) {
        CompanyView.empty().copy(
            company = Company.empty().copy(name = "Company $it"),
            branches = List(Random.nextInt(0, 100)) { Branch.empty() },
            clients = List(Random.nextInt(0, 100)) { Client.empty() },
            documents = List(Random.nextInt(0, 100)) { Document.empty() },
        )
    }

//    CompaniesScreenContent(
//        companies = Pag,
//        modifier = Modifier.fillMaxSize(),
//        queryState = MutableStateFlow(""),
//        onQueryChange = {},
//        onCompanyClick = {},
//        onCompanyEditClick = {},
//    ) {}

}
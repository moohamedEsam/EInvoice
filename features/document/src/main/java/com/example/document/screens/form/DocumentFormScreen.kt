package com.example.document.screens.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.OneTimeEventButton
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.empty
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import com.example.models.item.empty
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.random.Random

@Composable
fun DocumentFormScreen(
    documentId: String,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit
) {
    val viewModel: DocumentFormViewModel by viewModel { parametersOf(documentId) }
    val showInvoiceDialog by viewModel.isInvoiceDialogVisible.collectAsState()
    val showTaxDialog by viewModel.isTaxDialogVisible.collectAsState()

    DocumentFormScreenContent(
        companies = viewModel.companies,
        selectedCompany = viewModel.selectedCompany,
        onCompanySelected = viewModel::setCompany,
        branches = viewModel.branches,
        selectedBranch = viewModel.selectedBranch,
        onBranchSelected = viewModel::setBranch,
        clients = viewModel.clients,
        selectedClient = viewModel.selectedClient,
        onClientSelected = viewModel::setClient,
        internalId = viewModel.internalId,
        onInternalIdChanged = viewModel::setInternalId,
        internalIdValidationResult = viewModel.internalIdValidationResult,
        invoices = viewModel.invoices,
        invoicesTaxMapper = viewModel::getInvoiceTaxNames,
        onInvoiceRemove = viewModel::removeInvoice,
        onInvoiceEdit = viewModel::onEditInvoice,
        isEnabled = viewModel.isEnabled,
        isLoading = viewModel.isLoading,
        onAddTaxClick = { viewModel.showTaxDialog(invoiceTax = null, invoiceLineView = it) },
        onEditTax = { invoiceLineView, invoiceTax ->
            viewModel.showTaxDialog(invoiceTax = invoiceTax, invoiceLineView = invoiceLineView)
        },
        onRemoveTax = { invoiceLineView, invoiceTax ->
            viewModel.removeTax(invoiceLineView, invoiceTax)
        },
        onFormSubmit = {

        }
    )

    if (showTaxDialog) {
        InvoiceTaxManager(
            taxViewState = viewModel.taxView,
            onTaxChange = viewModel::setTaxView,
            subTaxState = viewModel.subTax,
            onSubTaxChange = viewModel::setSubTax,
            taxRateState = viewModel.taxRate,
            onTaxRateChange = viewModel::setTaxRate,
            onSaveTax = viewModel::addTax,
            availableTaxes = viewModel.taxTypes,
            onDismiss = viewModel::dismissTaxDialog,
            taxRateValidationResult = viewModel.taxRateValidationResult
        )
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun DocumentFormScreenContent(
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
    invoices: StateFlow<List<InvoiceLineView>>,
    invoicesTaxMapper: (InvoiceTax) -> Pair<String, String>,
    onInvoiceRemove: (InvoiceLineView) -> Unit,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onAddTaxClick: (InvoiceLineView) -> Unit,
    onRemoveTax: (InvoiceLineView, InvoiceTax) -> Unit,
    onEditTax: (InvoiceLineView, InvoiceTax) -> Unit,
    isEnabled: StateFlow<Boolean>,
    isLoading: StateFlow<Boolean>,
    onFormSubmit: () -> Unit,
) {
    val pages = listOf("General", "Invoices", "Taxes")
    val state = rememberPagerState()
    var pageToScroll by remember {
        mutableStateOf(state.currentPage)
    }
    LaunchedEffect(key1 = pageToScroll) {
        state.animateScrollToPage(pageToScroll)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = state.currentPage
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = state.currentPage == index,
                    onClick = { pageToScroll = index },
                    text = { Text(page) }
                )
            }
        }
        HorizontalPager(
            count = pages.size,
            state = state,
            modifier = Modifier.weight(1f)
        ) { index ->
            when (index) {
                0 -> GeneralPage(
                    companies = companies,
                    selectedCompany = selectedCompany,
                    onCompanySelected = onCompanySelected,
                    branches = branches,
                    selectedBranch = selectedBranch,
                    onBranchSelected = onBranchSelected,
                    clients = clients,
                    selectedClient = selectedClient,
                    onClientSelected = onClientSelected,
                    internalId = internalId,
                    onInternalIdChanged = onInternalIdChanged,
                    internalIdValidationResult = internalIdValidationResult
                )

                1 -> DocumentInvoicesList(
                    invoicesState = invoices,
                    onInvoiceRemove = onInvoiceRemove,
                    onInvoiceEdit = onInvoiceEdit,
                    onAddClick = { },
                    onAddTaxClick = onAddTaxClick
                )

                else -> InvoicesTaxesPage(
                    invoicesState = invoices,
                    onRemoveTax = onRemoveTax,
                    onAddTax = onAddTaxClick,
                    onEditTax = onEditTax,
                    taxMapper = invoicesTaxMapper
                )
            }
        }

        DocumentSummery(invoicesState = invoices, modifier = Modifier.fillMaxWidth())

        OneTimeEventButton(
            enabled = isEnabled,
            loading = isLoading,
            label = "Save",
            onClick = onFormSubmit,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentFormScreenPreview() {
    val company = Company.empty().copy(name = "Company")
    val branch = Branch.empty().copy(name = "Branch")
    val client = Client.empty().copy(name = "Client")
    val invoiceLineView = InvoiceLineView.empty()
        .copy(
            item = Item.empty().copy(name = "Item"),
            taxes = List(10) { InvoiceTax(Random.nextInt(0, 99).toFloat(), "V001", "T1") },
            quantity = Random.nextInt(1, 2000).toFloat(),
            unitValue = UnitValue(
                Random.nextDouble(5000.0),
                ""
            ),
            discountRate = Random.nextInt(1, 100).toFloat()
        )

    DocumentFormScreenContent(
        companies = MutableStateFlow(listOf(company)),
        selectedCompany = MutableStateFlow(company),
        onCompanySelected = {},
        branches = MutableStateFlow(listOf(branch)),
        selectedBranch = MutableStateFlow(branch),
        onBranchSelected = {},
        clients = MutableStateFlow(listOf(client)),
        selectedClient = MutableStateFlow(client),
        onClientSelected = {},
        internalId = MutableStateFlow("123"),
        onInternalIdChanged = {},
        internalIdValidationResult = MutableStateFlow(ValidationResult.Valid),
        invoices = MutableStateFlow(List(10) { invoiceLineView }),
        onInvoiceRemove = {},
        onInvoiceEdit = {},
        isEnabled = MutableStateFlow(true),
        isLoading = MutableStateFlow(false),
        onAddTaxClick = {},
        onRemoveTax = { _, _ -> },
        onEditTax = { _, _ -> },
        invoicesTaxMapper = { Pair(it.taxTypeCode, it.taxSubTypeCode) },
    ) {}

}
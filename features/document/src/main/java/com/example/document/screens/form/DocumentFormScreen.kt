package com.example.document.screens.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.OneTimeEventButton
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
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
    onDocumentSaved: (String) -> Unit,
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
        invoicePageInvoices = viewModel.invoicesFilteredByInvoicePageQuery,
        invoicePageQuery = viewModel.invoicePageQuery,
        createDate = viewModel.createdAt,
        onCreateDateChanged = viewModel::setDate,
        onInvoicePageQueryChanged = viewModel::setInvoicePageQuery,
        taxPageInvoices = viewModel.invoicesFilteredByTaxPageQuery,
        taxPageQuery = viewModel.taxPageQuery,
        onTaxPageQueryChanged = viewModel::setTaxPageQuery,
        invoicesTaxMapper = viewModel::getInvoiceTaxNames,
        onInvoiceRemove = viewModel::removeInvoice,
        onInvoiceAdd = viewModel::showInvoiceDialog,
        onInvoiceEdit = { viewModel.showInvoiceDialog(it) },
        onAddTaxClick = { viewModel.showTaxDialog(invoiceTax = null, invoiceLineView = it) },
        onRemoveTax = { invoiceLineView, invoiceTax ->
            viewModel.removeTax(invoiceLineView, invoiceTax)
        },
        onEditTax = { invoiceLineView, invoiceTax ->
            viewModel.showTaxDialog(invoiceTax = invoiceTax, invoiceLineView = invoiceLineView)
        },
        isEnabled = viewModel.isEnabled,
        isLoading = viewModel.isLoading
    ) { viewModel.save(onDocumentSaved) }

    if (showTaxDialog) {
        InvoiceTaxDialog(
            taxViewState = viewModel.taxView,
            onTaxChange = viewModel::setTaxView,
            subTaxState = viewModel.subTax,
            onSubTaxChange = viewModel::setSubTax,
            taxRateState = viewModel.taxRate,
            onTaxRateChange = viewModel::setTaxRate,
            onSaveTax = viewModel::saveTax,
            availableTaxes = viewModel.taxTypes,
            onDismiss = viewModel::dismissTaxDialog,
            taxRateValidationResult = viewModel.taxRateValidationResult
        )
    }

    if (showInvoiceDialog) {
        InvoiceDialog(
            itemsState = viewModel.items,
            selectedItemState = viewModel.selectedItem,
            onItemSelect = viewModel::setItem,
            priceState = viewModel.price,
            onPriceChange = viewModel::setPrice,
            priceValidationResult = viewModel.priceValidationResult,
            quantityState = viewModel.quantity,
            onQuantityChange = viewModel::setQuantity,
            quantityValidationResult = viewModel.quantityValidationResult,
            discountState = viewModel.discount,
            onDiscountChange = viewModel::setDiscount,
            discountValidationResult = viewModel.discountValidationResult,
            onAddClick = viewModel::saveInvoice,
            onDismiss = viewModel::dismissInvoiceDialog,
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
    invoicePageInvoices: StateFlow<List<InvoiceLineView>>,
    invoicePageQuery: StateFlow<String>,
    onInvoicePageQueryChanged: (String) -> Unit,
    taxPageInvoices: StateFlow<List<InvoiceLineView>>,
    taxPageQuery: StateFlow<String>,
    onTaxPageQueryChanged: (String) -> Unit,
    invoicesTaxMapper: (InvoiceTax) -> Pair<String, String>,
    onInvoiceRemove: (InvoiceLineView) -> Unit,
    onInvoiceAdd: () -> Unit,
    onInvoiceEdit: (InvoiceLineView) -> Unit,
    onAddTaxClick: (InvoiceLineView) -> Unit,
    onRemoveTax: (InvoiceLineView, InvoiceTax) -> Unit,
    onEditTax: (InvoiceLineView, InvoiceTax) -> Unit,
    createDate: StateFlow<Date>,
    onCreateDateChanged: (Date) -> Unit,
    isEnabled: StateFlow<Boolean>,
    isLoading: StateFlow<Boolean>,
    onFormSubmit: () -> Unit,
) {
    val pages = listOf("General", "Invoices", "Taxes")
    val pagerState = rememberPagerState()
    var pageToScroll by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(pageToScroll) {
        pagerState.animateScrollToPage(pageToScroll)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("DocumentFormScreen")
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { pageToScroll = index },
                    text = { Text(page) }
                )
            }
        }
        HorizontalPager(
            count = pages.size,
            modifier = Modifier.weight(1f),
            state = pagerState,
            itemSpacing = 8.dp,
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
                    internalIdValidationResult = internalIdValidationResult,
                    createDate = createDate,
                    onCreateDateChanged = onCreateDateChanged,
                    invoices = invoicePageInvoices,
                )

                1 -> DocumentInvoicesList(
                    invoicesState = invoicePageInvoices,
                    queryState = invoicePageQuery,
                    onQueryChange = onInvoicePageQueryChanged,
                    onInvoiceRemove = onInvoiceRemove,
                    onInvoiceEdit = onInvoiceEdit,
                    onAddClick = onInvoiceAdd,
                    onAddTaxClick = onAddTaxClick,
                    onShowItemTaxes = {
                        pageToScroll = 2
                        onTaxPageQueryChanged(it.item.name)
                    }
                )


                else -> InvoicesTaxesPage(
                    invoicesState = taxPageInvoices,
                    onRemoveTax = onRemoveTax,
                    onAddTax = onAddTaxClick,
                    onEditTax = onEditTax,
                    taxMapper = invoicesTaxMapper,
                    queryState = taxPageQuery,
                    onQueryChange = onTaxPageQueryChanged
                )
            }
        }

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
        invoicePageInvoices = MutableStateFlow(List(10) { invoiceLineView.copy(id = it.toString()) }),
        invoicesTaxMapper = { Pair(it.taxTypeCode, it.taxSubTypeCode) },
        onInvoiceRemove = {},
        onInvoiceAdd = {},
        onInvoiceEdit = {},
        onAddTaxClick = {},
        onRemoveTax = { _, _ -> },
        onEditTax = { _, _ -> },
        isEnabled = MutableStateFlow(true),
        isLoading = MutableStateFlow(false),
        taxPageInvoices = MutableStateFlow(List(10) { invoiceLineView.copy(id = it.toString()) }),
        taxPageQuery = MutableStateFlow(""),
        onTaxPageQueryChanged = {},
        invoicePageQuery = MutableStateFlow(""),
        onInvoicePageQueryChanged = {},
        createDate = MutableStateFlow(Date()),
        onCreateDateChanged = {},
    ) {}

}
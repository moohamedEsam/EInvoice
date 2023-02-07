package com.example.document.screens.form

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.OneTimeEventButton
import com.example.models.branch.Branch
import com.example.models.Client
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.empty
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import com.example.models.item.empty
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.random.Random

private const val UNKNOWN_ERROR = "Unknown error"

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
        invoicePageInvoices = viewModel.invoicesFilteredByInvoicePageQuery,
        invoicePageQuery = viewModel.invoicePageQuery,
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
        isLoading = viewModel.isLoading,
        onFormSubmit = {
            viewModel.save { result ->
                val snackBarEvent = if (result is Result.Error)
                    SnackBarEvent(
                        message = result.exception ?: UNKNOWN_ERROR,
                        actionLabel = "Retry",
                        action = { viewModel.save {} }
                    )
                else
                    SnackBarEvent(message = "Saved successfully",)

                onShowSnackBarEvent(snackBarEvent)
            }
        }
    )

    if (showTaxDialog) {
        InvoiceTaxDialog(
            taxViewState = viewModel.taxView,
            onTaxChange = viewModel::setTaxView,
            subTaxState = viewModel.subTax,
            onSubTaxChange = viewModel::setSubTax,
            taxRateState = viewModel.taxRate,
            onTaxRateChange = viewModel::setTaxRate,
            onSaveTax = {
                viewModel.saveTax { result ->
                    if (result !is Result.Error) return@saveTax
                    val snackBarEvent = SnackBarEvent(
                        message = result.exception ?: UNKNOWN_ERROR,
                    )
                    onShowSnackBarEvent(snackBarEvent)
                }
            },
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
            onAddClick = {
                viewModel.saveInvoice { result ->
                    if (result !is Result.Error) return@saveInvoice
                    val snackBarEvent = SnackBarEvent(
                        message = result.exception ?: UNKNOWN_ERROR,
                    )
                    onShowSnackBarEvent(snackBarEvent)
                }
            },
            onDismiss = viewModel::dismissInvoiceDialog,
        )
    }

}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
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
    isEnabled: StateFlow<Boolean>,
    isLoading: StateFlow<Boolean>,
    onFormSubmit: () -> Unit,
) {
    val pages = listOf("General", "Invoices", "Taxes")
    var currentPage by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = currentPage
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = currentPage == index,
                    onClick = { currentPage = index },
                    text = { Text(page) }
                )
            }
        }
        AnimatedContent(
            targetState = currentPage,
            modifier = Modifier.weight(1f),
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
                    invoicesState = invoicePageInvoices,
                    queryState = invoicePageQuery,
                    onQueryChange = onInvoicePageQueryChanged,
                    onInvoiceRemove = onInvoiceRemove,
                    onInvoiceEdit = onInvoiceEdit,
                    onAddClick = onInvoiceAdd,
                    onAddTaxClick = onAddTaxClick,
                    onShowItemTaxes = {
                        currentPage = 2
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

        DocumentSummery(invoicesState = invoicePageInvoices, modifier = Modifier.fillMaxWidth())

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
    ) {}

}
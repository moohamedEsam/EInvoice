package com.example.document.screens.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.numberValidator
import com.example.common.validators.percentValidator
import com.example.domain.company.GetCompaniesViewsUseCase
import com.example.domain.document.CreateDocumentUseCase
import com.example.domain.document.GetDocumentsUseCase
import com.example.domain.document.UpdateDocumentUseCase
import com.example.domain.item.GetItemsUseCase
import com.example.domain.item.GetTaxTypesUseCase
import com.example.models.Branch
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.document.Document
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class DocumentFormViewModel(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val createDocumentUseCase: CreateDocumentUseCase,
    private val updateDocumentUseCase: UpdateDocumentUseCase,
    private val getCompaniesViewsUseCase: GetCompaniesViewsUseCase,
    private val getItemsUseCase: GetItemsUseCase,
    private val getTaxTypesUseCase: GetTaxTypesUseCase,
    private val documentId: String,
) : ViewModel() {
    private val isUpdate = documentId.isNotBlank()
    private val _companiesViews = MutableStateFlow(emptyList<CompanyView>())
    val companies = _companiesViews.map { companies ->
        companies.map { it.company }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _selectedCompany: MutableStateFlow<Company?> = MutableStateFlow(null)
    val selectedCompany = _selectedCompany.asStateFlow()

    val branches = combine(_companiesViews, selectedCompany) { companies, selectedCompany ->
        companies.find { it.company.id == selectedCompany?.id }?.branches ?: emptyList()

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val clients = combine(_companiesViews, selectedCompany) { companies, selectedCompany ->
        companies.find { it.company.id == selectedCompany?.id }?.clients ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _items = MutableStateFlow(emptyList<Item>())
    val items = _items.asStateFlow()

    private val _selectedItem: MutableStateFlow<Item?> = MutableStateFlow(null)
    val selectedItem = _selectedItem.asStateFlow()

    private val _quantity = MutableStateFlow("")
    val quantity = _quantity.asStateFlow()
    val quantityValidationResult = quantity.map(::numberValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()
    val priceValidationResult = price.map(::numberValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _discount = MutableStateFlow("")
    val discount = _discount.asStateFlow()
    val discountValidationResult = discount.map(::percentValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _taxView: MutableStateFlow<TaxView?> = MutableStateFlow(null)
    val taxView = _taxView.asStateFlow()

    private val _subTax: MutableStateFlow<SubTax?> = MutableStateFlow(null)
    val subTax = _subTax.asStateFlow()

    private val _taxRate = MutableStateFlow("")
    val taxRate = _taxRate.asStateFlow()
    val taxRateValidationResult = taxRate.map(::percentValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _taxTypes = MutableStateFlow(emptyList<TaxView>())
    val taxTypes = _taxTypes.asStateFlow()

    private val _selectedBranch: MutableStateFlow<Branch?> = MutableStateFlow(null)
    val selectedBranch = _selectedBranch.asStateFlow()

    private val _selectedClient: MutableStateFlow<Client?> = MutableStateFlow(null)
    val selectedClient = _selectedClient.asStateFlow()

    private val _internalId = MutableStateFlow("")
    val internalId = _internalId.asStateFlow()
    val internalIdValidationResult = internalId.map { internalId ->
        ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _invoices = MutableStateFlow(emptyList<InvoiceLineView>())
    val invoices = _invoices.asStateFlow()

    val isEnabled = combine(
        selectedBranch, selectedCompany, selectedClient, internalIdValidationResult
    ) { branch, company, client, validationResult ->
        branch != null && company != null && client != null && validationResult == ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isInvoiceDialogVisible = MutableStateFlow(false)
    val isInvoiceDialogVisible = _isInvoiceDialogVisible.asStateFlow()
    private val isUpdatingInvoice = false

    private val _isTaxDialogVisible = MutableStateFlow(false)
    val isTaxDialogVisible = _isTaxDialogVisible.asStateFlow()
    private val isUpdatingTax = false
    private var invoiceLineId: String? = null

    fun setInternalId(value: String) = viewModelScope.launch {
        _internalId.update { value }
    }

    fun setCompany(company: Company) = viewModelScope.launch {
        if (company == _selectedCompany.value) return@launch
        _selectedCompany.update { company }
        _selectedBranch.update { null }
        _selectedClient.update { null }
    }

    fun setBranch(branch: Branch) = viewModelScope.launch {
        _selectedBranch.update { branch }
    }

    fun setClient(client: Client) = viewModelScope.launch {
        _selectedClient.update { client }
    }

    fun setItem(item: Item) = viewModelScope.launch {
        _selectedItem.update { item }
        if (price.value.isBlank())
            _price.update { item.price.toString() }
    }

    fun addInvoice() = viewModelScope.launch {
        val invoiceLineView = InvoiceLineView(
            id = UUID.randomUUID().toString(),
            item = selectedItem.value ?: return@launch,
            quantity = quantity.value.toFloatOrNull() ?: return@launch,
            unitValue = UnitValue(
                currencyEgp = price.value.toDoubleOrNull() ?: return@launch,
                currencySold = "EGP"
            ),
            taxes = emptyList(),
            discountRate = 0.0f,
            documentId = "",
        )
        _invoices.update { invoices ->
            if (invoices.map { it.id }.contains(invoiceLineView.id))
                invoices
            else
                invoices + invoiceLineView
        }
    }

    fun removeInvoice(invoice: InvoiceLineView) = viewModelScope.launch {
        _invoices.update { invoices ->
            invoices - invoice
        }
    }

    fun updateInvoice(invoice: InvoiceLineView) = viewModelScope.launch {
        _invoices.update { invoices ->
            invoices.map {
                if (it.id == invoice.id) invoice
                else it
            }
        }
    }

    fun setQuantity(value: String) = viewModelScope.launch {
        _quantity.update { value }
    }

    fun setPrice(value: String) = viewModelScope.launch {
        _price.update { value }
    }

    fun addTax() = viewModelScope.launch {
        val tax = getCurrentInvoiceTax() ?: return@launch
        val invoiceLine = _invoices.value.find { it.id == invoiceLineId } ?: return@launch
        val newInvoiceLine = invoiceLine.copy(taxes = invoiceLine.taxes + tax)
        updateInvoice(newInvoiceLine)
        dismissTaxDialog()
    }


    private fun getCurrentInvoiceTax(): InvoiceTax? {
        return InvoiceTax(
            rate = taxRate.value.toFloatOrNull() ?: return null,
            taxSubTypeCode = subTax.value?.code ?: return null,
            taxTypeCode = taxView.value?.code ?: return null,
        )
    }

    fun setTaxView(taxView: TaxView) = viewModelScope.launch {
        _taxView.update { taxView }
    }

    fun setSubTax(subTax: SubTax) = viewModelScope.launch {
        _subTax.update { subTax }
    }

    fun setTaxRate(taxRate: String) = viewModelScope.launch {
        _taxRate.update { taxRate }
    }

    fun dismissInvoiceDialog() = viewModelScope.launch {
        _isInvoiceDialogVisible.update { false }
    }

    fun getInvoiceTaxNames(invoiceTax: InvoiceTax): Pair<String, String> {
        val taxView = taxTypes.value.find { it.code == invoiceTax.taxTypeCode }
            ?: return invoiceTax.taxTypeCode to invoiceTax.taxSubTypeCode

        val subTax = taxView.subTaxes.find { it.code == invoiceTax.taxSubTypeCode }
            ?: return invoiceTax.taxTypeCode to invoiceTax.taxSubTypeCode

        return taxView.name to subTax.name
    }

    fun showTaxDialog(
        invoiceTax: InvoiceTax? = null,
        invoiceLineView: InvoiceLineView
    ) = viewModelScope.launch {
        if (invoiceTax != null) {
            _taxView.update { _ -> taxTypes.value.find { it.code == invoiceTax.taxTypeCode } }
            _subTax.update { _ -> _taxView.value?.subTaxes?.find { it.code == invoiceTax.taxSubTypeCode } }
            _taxRate.update { _ -> invoiceTax.rate.toString() }
        }
        invoiceLineId = invoiceLineView.id
        _isTaxDialogVisible.update { true }
    }

    fun removeTax(invoiceLineView: InvoiceLineView, invoiceTax: InvoiceTax) {
        val newInvoiceLine = invoiceLineView.copy(taxes = invoiceLineView.taxes - invoiceTax)
        updateInvoice(newInvoiceLine)
    }

    fun dismissTaxDialog() = viewModelScope.launch {
        _taxView.update { null }
        _subTax.update { null }
        _taxRate.update { "" }
        _isTaxDialogVisible.update { false }
    }

    init {
        observeCompanies()
        observeDocuments()
        observeItems()
        observeTaxes()
    }

    private fun observeDocuments() {
        viewModelScope.launch {
            getDocumentsUseCase().collectLatest {
                if (isUpdate) {
                    val document = it.firstOrNull { document -> document.id == documentId }
                        ?: return@collectLatest
                    setForm(document)
                }
            }
        }
    }

    private fun observeCompanies() {
        viewModelScope.launch {
            getCompaniesViewsUseCase().collectLatest {
                _companiesViews.update { _ -> it }
            }
        }
    }

    private fun observeItems() {
        viewModelScope.launch {
            getItemsUseCase().collectLatest {
                _items.update { _ -> it }
            }
        }
    }

    private fun observeTaxes() {
        viewModelScope.launch {
            getTaxTypesUseCase().collectLatest {
                _taxTypes.update { _ -> it }
            }
        }
    }


    private fun setForm(document: DocumentView) {
        _selectedCompany.update { document.company }
        _selectedBranch.update { document.branch }
        _selectedClient.update { document.client }
        _internalId.update { document.internalId }
        _invoices.update { document.invoices }
    }

    fun onEditInvoice(invoice: InvoiceLineView) {
        _selectedItem.update { invoice.item }
        _quantity.update { invoice.quantity.toString() }
        _price.update { invoice.unitValue.currencyEgp.toString() }
    }

    fun save(onResult: (Result<Document>) -> Unit) {

    }
}
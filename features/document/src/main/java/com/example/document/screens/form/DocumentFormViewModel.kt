package com.example.document.screens.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.notBlankValidator
import com.example.common.validators.numberValidator
import com.example.common.validators.percentValidator
import com.example.domain.company.GetCompaniesViewsUseCase
import com.example.domain.document.CreateDocumentUseCase
import com.example.domain.document.GetDocumentUseCase
import com.example.domain.document.GetDocumentsInternalIdsByCompanyIdUseCase
import com.example.domain.document.UpdateDocumentUseCase
import com.example.domain.item.GetItemsByBranchUseCase
import com.example.domain.item.GetTaxTypesUseCase
import com.example.functions.SnackBarManager
import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.company.Company
import com.example.models.company.CompanyView
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.*
import com.example.models.item.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DocumentFormViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val getDocumentsInternalIdsByCompanyIdUseCase: GetDocumentsInternalIdsByCompanyIdUseCase,
    private val createDocumentUseCase: CreateDocumentUseCase,
    private val updateDocumentUseCase: UpdateDocumentUseCase,
    private val getCompaniesViewsUseCase: GetCompaniesViewsUseCase,
    private val getItemsByBranchUseCase: GetItemsByBranchUseCase,
    private val getTaxTypesUseCase: GetTaxTypesUseCase,
    documentId: String,
    private val snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
    private val isUpdatingDocument = documentId.isNotBlank()
    private val newDocumentId = if (isUpdatingDocument) documentId else UUID.randomUUID().toString()
    private val _alreadyUsedInternalIds = MutableStateFlow(emptyList<String>())
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
    val internalIdValidationResult =
        combine(_internalId, _alreadyUsedInternalIds) { internalId, otherIds ->
            val internalIdAlreadyExists = otherIds.contains(internalId)
            if (internalIdAlreadyExists) ValidationResult.Invalid("Internal ID already exists")
            else notBlankValidator(internalId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _createdAt = MutableStateFlow(Date())
    val createdAt = _createdAt.asStateFlow()

    private val _invoicePageQuery = MutableStateFlow("")
    val invoicePageQuery = _invoicePageQuery.asStateFlow()

    private val _taxPageQuery = MutableStateFlow("")
    val taxPageQuery = _taxPageQuery.asStateFlow()

    private val _invoices = MutableStateFlow(emptyList<InvoiceLineView>())
    val invoicesFilteredByInvoicePageQuery =
        combine(_invoices, invoicePageQuery) { invoices, query ->
            if (query.isBlank()) return@combine invoices
            invoices.filter { invoice -> invoice.item.name.contains(query, true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val invoicesFilteredByTaxPageQuery =
        combine(_invoices, taxPageQuery) { invoices, query ->
            invoices.filter { invoice ->
                if (query.isBlank()) return@combine invoices
                invoice.taxes.any { tax ->
                    tax.taxSubTypeCode.contains(query, true)
                            || tax.taxTypeCode.contains(query, true)
                } || invoice.item.name.contains(query, true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val isEnabled = combine(
        selectedBranch, selectedCompany, selectedClient, internalIdValidationResult, _invoices
    ) { branch, company, client, validationResult, invoices ->
        branch != null && company != null && client != null &&
                validationResult == ValidationResult.Valid && invoices.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isInvoiceDialogVisible = MutableStateFlow(false)
    val isInvoiceDialogVisible = _isInvoiceDialogVisible.asStateFlow()

    private val _isTaxDialogVisible = MutableStateFlow(false)
    val isTaxDialogVisible = _isTaxDialogVisible.asStateFlow()
    private var isUpdatingTax = false
    private var invoiceLineId: String? = null
    private var oldStatus = DocumentStatus.Initial

    fun setInternalId(value: String) = viewModelScope.launch {
        _internalId.update { value }
    }

    fun setInvoicePageQuery(value: String) = viewModelScope.launch {
        _invoicePageQuery.update { value }
    }

    fun setTaxPageQuery(value: String) = viewModelScope.launch {
        _taxPageQuery.update { value }
    }

    fun setCompany(company: Company) = viewModelScope.launch {
        if (company == _selectedCompany.value) return@launch
        _selectedCompany.update { company }
        _selectedBranch.update { null }
        _selectedClient.update { null }
    }

    fun setBranch(branch: Branch) = viewModelScope.launch {
        _selectedBranch.update { branch }
        _invoices.update {
            it.filter { invoice -> invoice.item.branchId == branch.id }
        }
    }

    fun setClient(client: Client) = viewModelScope.launch {
        _selectedClient.update { client }
    }

    fun setItem(item: Item) = viewModelScope.launch {
        _selectedItem.update { item }
        if (price.value.isBlank())
            _price.update { item.price.toString() }
    }

    fun saveInvoice() = viewModelScope.launch {
        val item = selectedItem.value ?: return@launch
        val itemAlreadyExists =
            _invoices.value.any { it.item.id == item.id && it.id != invoiceLineId }

        if (itemAlreadyExists) {
            val event = SnackBarEvent(message = "Item already exists")
            showSnackBarEvent(event)
            return@launch
        }

        val invoiceLineView = getCurrentInvoiceLine() ?: return@launch
        insertOrUpdateInvoiceLine(invoiceLineView)
        dismissInvoiceDialog()
    }

    private fun insertOrUpdateInvoiceLine(invoiceLineView: InvoiceLineView) {
        val oldInvoice = _invoices.value.find { it.id == invoiceLineView.id }
        _invoices.update { invoices ->
            if (oldInvoice == null)
                invoices + invoiceLineView
            else
                invoices - oldInvoice + invoiceLineView
        }
    }

    private fun getCurrentInvoiceLine(): InvoiceLineView? {
        val taxes = _invoices.value.find { it.id == invoiceLineId }?.taxes ?: emptyList()
        return InvoiceLineView(
            id = invoiceLineId ?: UUID.randomUUID().toString(),
            item = selectedItem.value ?: return null,
            quantity = quantity.value.toFloatOrNull() ?: return null,
            unitValue = UnitValue(
                currencyEgp = price.value.toDoubleOrNull() ?: return null,
                currencySold = "EGP"
            ),
            taxes = taxes,
            discountRate = _discount.value.toFloatOrNull() ?: 0f,
            documentId = newDocumentId,
        )
    }

    fun removeInvoice(invoice: InvoiceLineView) = viewModelScope.launch {
        _invoices.update { invoices ->
            invoices - invoice
        }
    }

    fun setQuantity(value: String) = viewModelScope.launch {
        _quantity.update { value }
    }

    fun setPrice(value: String) = viewModelScope.launch {
        _price.update { value }
    }

    fun setDiscount(value: String) = viewModelScope.launch {
        _discount.update { value }
    }

    fun setDate(date: Date) = viewModelScope.launch {
        _createdAt.update { date }
    }

    fun saveTax() = viewModelScope.launch {
        val tax = getCurrentInvoiceTax() ?: return@launch
        val invoiceLine = _invoices.value.find { it.id == invoiceLineId } ?: return@launch
        val taxAlreadyExists =
            invoiceLine.taxes.any { it.taxSubTypeCode == tax.taxSubTypeCode && !isUpdatingTax }
        if (taxAlreadyExists) {
            val event = SnackBarEvent(message = "Tax already exists")
            showSnackBarEvent(event)
            return@launch
        }

        val newInvoiceLine = invoiceLine.copy(
            taxes = if (isUpdatingTax)
                invoiceLine.taxes.map {
                    if (it.taxSubTypeCode == tax.taxSubTypeCode) tax
                    else it
                }
            else invoiceLine.taxes + tax
        )
        insertOrUpdateInvoiceLine(newInvoiceLine)
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
        invoiceLineId = null
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
            _taxRate.update { invoiceTax.rate.toString() }
            isUpdatingTax = true
        }
        invoiceLineId = invoiceLineView.id
        _isTaxDialogVisible.update { true }
    }

    fun removeTax(invoiceLineView: InvoiceLineView, invoiceTax: InvoiceTax) {
        val newInvoiceLine = invoiceLineView.copy(taxes = invoiceLineView.taxes - invoiceTax)
        insertOrUpdateInvoiceLine(newInvoiceLine)
    }

    fun dismissTaxDialog() = viewModelScope.launch {
        _taxView.update { null }
        _subTax.update { null }
        _taxRate.update { "" }
        _isTaxDialogVisible.update { false }
        invoiceLineId = null
        isUpdatingTax = false
    }

    init {
        if (isUpdatingDocument)
            observeDocument()
        observeCompanies()
        observeInternalIds()
        observeItems()
        observeTaxes()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeInternalIds() {
        viewModelScope.launch {
            _selectedCompany.filterNotNull().flatMapLatest {
                getDocumentsInternalIdsByCompanyIdUseCase(
                    GetDocumentsInternalIdsByCompanyIdUseCase.Params(
                        it.id,
                        newDocumentId
                    )
                )
            }.collectLatest(_alreadyUsedInternalIds::emit)
        }
    }

    private fun observeDocument() {
        viewModelScope.launch {
            getDocumentUseCase(newDocumentId).distinctUntilChanged().collectLatest(::setForm)
        }
    }

    private fun observeCompanies() {
        viewModelScope.launch {
            getCompaniesViewsUseCase().collectLatest(_companiesViews::emit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeItems() {
        viewModelScope.launch {
            _selectedBranch.filterNotNull().flatMapLatest {
                getItemsByBranchUseCase(it.id)
            }.collectLatest(_items::emit)
        }
    }

    private fun observeTaxes() {
        viewModelScope.launch {
            getTaxTypesUseCase().collectLatest(_taxTypes::emit)
        }
    }


    private fun setForm(document: DocumentView) {
        _selectedCompany.update { document.company }
        _selectedBranch.update { document.branch }
        _selectedClient.update { document.client }
        _internalId.update { document.internalId }
        _invoices.update { document.invoices }
        oldStatus = document.status
        _alreadyUsedInternalIds.update { it - document.internalId }
    }

    fun showInvoiceDialog(invoice: InvoiceLineView? = null) {
        if (invoice == null) {
            _selectedItem.update { null }
            _quantity.update { "" }
            _price.update { "" }
            _discount.update { "" }
            invoiceLineId = null
            _isInvoiceDialogVisible.update { true }
            return
        }
        _selectedItem.update { invoice.item }
        _quantity.update { invoice.quantity.toString() }
        _price.update { invoice.unitValue.currencyEgp.toString() }
        _discount.update { invoice.discountRate.toString() }
        invoiceLineId = invoice.id
        _isInvoiceDialogVisible.update { true }
    }

    fun save(onDocumentSaved: (String) -> Unit) {
        viewModelScope.launch {
            val document = getCurrentDocumentView() ?: return@launch
            val result = insertOrUpdateDocument(document)
            val event = result.getSnackBarEvent(
                successMessage = "Document Saved Successfully",
                errorActionLabel = "Retry",
                errorAction = { save(onDocumentSaved)}
            )
            result.ifSuccess { _internalId.update { "" } }
            showSnackBarEvent(event)
            result.ifSuccess { onDocumentSaved(document.id) }
        }
    }

    private fun getCurrentDocumentView(): DocumentView? {
        return DocumentView(
            id = newDocumentId,
            company = _selectedCompany.value ?: return null,
            branch = _selectedBranch.value ?: return null,
            client = _selectedClient.value ?: return null,
            internalId = _internalId.value,
            invoices = _invoices.value,
            date = _createdAt.value,
            documentType = "I",
            status = if (isUpdatingDocument && oldStatus in listOf(
                    DocumentStatus.SignError,
                    DocumentStatus.Invalid,
                    DocumentStatus.InvalidSent
                )
            )
                DocumentStatus.Updated
            else
                oldStatus
        )
    }

    private suspend fun insertOrUpdateDocument(document: DocumentView) = if (isUpdatingDocument)
        updateDocumentUseCase(document)
    else
        createDocumentUseCase(document)

}
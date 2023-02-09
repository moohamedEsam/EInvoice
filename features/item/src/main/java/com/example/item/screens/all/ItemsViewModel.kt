package com.example.item.screens.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.notBlankValidator
import com.example.domain.branch.GetBranchesUseCase
import com.example.domain.item.CreateItemUseCase
import com.example.domain.item.GetItemsUseCase
import com.example.domain.item.GetUnitTypesUseCase
import com.example.domain.item.UpdateItemUseCase
import com.example.models.branch.Branch
import com.example.models.item.Item
import com.example.models.item.UnitType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ItemsViewModel(
    private val getItemsUseCase: GetItemsUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getUnitTypesUseCase: GetUnitTypesUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase
) : ViewModel() {
    private var itemId: String? = null
    private val _items = MutableStateFlow(emptyList<Item>())
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val items = combine(query, _items) { query, items ->
        if (query.isBlank()) items
        else items.filter { it.name.contains(query, true) || it.description.contains(query, true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    val nameValidationResult = name.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Valid)
    private val _description = MutableStateFlow("")

    val description = _description.asStateFlow()
    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    val priceValidationResult = price.map {
        when {
            it.isEmpty() -> ValidationResult.Empty
            it.isBlank() -> ValidationResult.Invalid("Cannot be empty")
            it.toDoubleOrNull() == null -> ValidationResult.Invalid("Must be a number")
            else -> ValidationResult.Valid
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Valid)
    private val _status = MutableStateFlow(TaxStatus.Taxable)

    val status = _status.asStateFlow()
    private val _itemCode = MutableStateFlow("")
    val itemCode = _itemCode.asStateFlow()

    val itemCodeValidationResult = itemCode.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Valid)

    private val _selectedBranch = MutableStateFlow<Branch?>(null)
    val selectedBranch = _selectedBranch.asStateFlow()
    private val _internalCode = MutableStateFlow("")
    val internalCode = _internalCode.asStateFlow()


    val internalCodeValidationResult =
        combine(_items, internalCode, _selectedBranch) { items, internalCode, selectedBranch ->
            when {
                items.filter { it.branchId == selectedBranch?.id }
                    .any { it.internalCode == internalCode && it.id != itemId } ->
                    ValidationResult.Invalid("Internal code already exists")
                else -> notBlankValidator(internalCode)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Valid)
    private val _branches = MutableStateFlow(emptyList<Branch>())
    val branches = _branches.asStateFlow()
    private val _unitTypes = MutableStateFlow(emptyList<UnitType>())

    val unitTypes = _unitTypes.asStateFlow()
    private val _selectedUnitType = MutableStateFlow<UnitType?>(null)

    val selectedUnitType = _selectedUnitType.asStateFlow()
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    val isFormValid = combine(
        nameValidationResult,
        priceValidationResult,
        itemCodeValidationResult,
        selectedBranch,
        selectedUnitType,
        internalCodeValidationResult
    ) { nameValidationResult, priceValidationResult, itemCodeValidationResult, selectedBranch, selectedUnitType, internalCode ->
        nameValidationResult is ValidationResult.Valid &&
                priceValidationResult is ValidationResult.Valid &&
                itemCodeValidationResult is ValidationResult.Valid &&
                internalCode is ValidationResult.Valid &&
                selectedBranch != null &&
                selectedUnitType != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observerItems()
        observerBranches()
        observerUnitTypes()
    }

    private fun observerItems() {
        viewModelScope.launch {
            getItemsUseCase().collectLatest { items ->
                _items.value = items
            }
        }
    }

    private fun observerBranches() {
        viewModelScope.launch {
            getBranchesUseCase().collectLatest { branches ->
                _branches.value = branches
            }
        }
    }

    private fun observerUnitTypes() {
        viewModelScope.launch {
            getUnitTypesUseCase().collectLatest { unitTypes ->
                _unitTypes.value = unitTypes
            }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onItemClicked(item: Item) {
        _name.update { item.name }
        _description.update { item.description }
        _price.update { item.price.toString() }
        _status.update { item.status }
        _itemCode.update { item.itemCode }
        _selectedBranch.update { branches.value.firstOrNull { it.id == item.branchId } }
        _selectedUnitType.update { unitTypes.value.firstOrNull { it.code == item.unitTypeCode } }
        _internalCode.update { item.internalCode }
        itemId = item.id
        _showDialog.update { true }
    }

    fun onAddItemClicked() {
        _name.update { "" }
        _description.update { "" }
        _price.update { "" }
        _status.update { TaxStatus.Taxable }
        _itemCode.update { "" }
        _internalCode.update { "" }
        _selectedBranch.update { null }
        _selectedUnitType.update { null }
        itemId = null
        _showDialog.update { true }
    }

    fun setName(name: String) {
        _name.update { name }
    }

    fun setDescription(description: String) {
        _description.update { description }
    }

    fun setPrice(price: String) {
        _price.update { price }
    }

    fun setStatus(status: TaxStatus) {
        _status.update { status }
    }

    fun setItemCode(itemCode: String) {
        _itemCode.update { itemCode }
    }

    fun setBranch(branch: Branch) {
        _selectedBranch.update { branch }
    }

    fun setUnitType(unitType: UnitType) {
        _selectedUnitType.update { unitType }
    }

    fun setInternalCode(internalCode: String) {
        _internalCode.update { internalCode }
    }

    fun dismissDialog() {
        _showDialog.update { false }
    }

    fun saveItem(onResult: (Result<Item>) -> Unit) {
        viewModelScope.launch {
            _isLoading.update { true }
            val item = Item(
                id = itemId ?: UUID.randomUUID().toString(),
                name = name.value,
                description = description.value,
                price = price.value.toDouble(),
                status = status.value,
                itemCode = itemCode.value,
                internalCode = internalCode.value,
                branchId = selectedBranch.value?.id ?: return@launch,
                unitTypeCode = selectedUnitType.value?.code ?: return@launch
            )
            val result = if (itemId == null)
                createItemUseCase(item)
            else
                updateItemUseCase(item)

            onResult(result)
            _isLoading.update { false }
            _showDialog.update { false }
        }
    }

    private fun <T1, T2, T3, T4, T5, T6, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        transform: suspend (T1, T2, T3, T4, T5, T6) -> R
    ): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
        transform(
            args[0] as T1,
            args[1] as T2,
            args[2] as T3,
            args[3] as T4,
            args[4] as T5,
            args[5] as T6
        )
    }

}

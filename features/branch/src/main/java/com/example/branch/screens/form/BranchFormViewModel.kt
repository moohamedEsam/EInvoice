package com.example.branch.screens.form

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.notBlankValidator
import com.example.common.validators.validateUsername
import com.example.domain.branch.*
import com.example.domain.company.GetCompaniesUseCase
import com.example.functions.SnackBarManager
import com.example.models.branch.Branch
import com.example.models.OptionalAddress
import com.example.models.branch.BranchView
import com.example.models.company.Company
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BranchFormViewModel(
    private val getBranchesUseCase: GetBranchesByCompanyUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val createBranchUseCase: CreateBranchUseCase,
    private val updateBranchUseCase: UpdateBranchUseCase,
    private val getBranchViewUseCase: GetBranchViewUseCase,
    private val branchId: String,
    private val snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
    private val _otherBranchesInternalIds = MutableStateFlow(emptyList<String>())
    private val isUpdating = branchId.isNotBlank()
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany = _selectedCompany.asStateFlow()

    private val _internalId = MutableStateFlow("")
    val internalId = _internalId.asStateFlow()
    val internalIdValidationResult =
        combine(
            internalId,
            _otherBranchesInternalIds
        ) { internalId, branchesInternalIds ->
            when {
                internalId.isEmpty() -> ValidationResult.Empty
                internalId.any { char -> !char.isDigit() } -> ValidationResult.Invalid("Must be a number")
                branchesInternalIds.contains(internalId) -> ValidationResult.Invalid("Internal ID already exists")
                else -> ValidationResult.Valid
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)
    private val _street = MutableStateFlow("")


    private val streetValidationResult = _street.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _country = MutableStateFlow("")
    private val _governate = MutableStateFlow("")

    private val governateValidationResult = _governate.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)


    private val _postalCode = MutableStateFlow("")
    private val _regionCity = MutableStateFlow("")

    private val regionCityValidationResult = _regionCity.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)
    private val _companies = MutableStateFlow(emptyList<Company>())

    val companies = _companies.asStateFlow()


    private val _optionalAddress = MutableStateFlow(OptionalAddress("", "", "", "", ""))
    val optionalAddress = _optionalAddress.asStateFlow()

    val address = combine(
        _street,
        _regionCity,
        _country,
        _governate,
        _postalCode
    ) { street, regionCity, country, governate, postalCode ->
        Address(Locale.getDefault()).apply {
            this.thoroughfare = street
            this.subAdminArea = regionCity
            this.countryName = country
            this.adminArea = governate
            this.postalCode = postalCode
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Address(Locale.getDefault()))

    val isFormValid = combine(
        nameValidationResult,
        internalIdValidationResult,
        streetValidationResult,
        governateValidationResult,
        regionCityValidationResult
    ) { validation ->
        validation.all { it is ValidationResult.Valid }
    }.combine(_selectedCompany) { isFormValid, selectedCompany ->
        isFormValid && selectedCompany != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observerCompanies()
        observeBranches()
        if (isUpdating) {
            observeBranch()
        }
    }

    private fun observeBranch() {
        viewModelScope.launch {
            getBranchViewUseCase(branchId).collectLatest { branch ->
                fillForm(branch)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBranches() {
        viewModelScope.launch {
            _selectedCompany.filterNotNull()
                .flatMapLatest { company -> getBranchesUseCase(company.id) }
                .collectLatest { branches ->
                    _otherBranchesInternalIds.update {
                        branches.filter { it.id != branchId }.map { it.internalId }
                    }
                }
        }
    }


    private fun fillForm(branchView: BranchView) {
        _name.update { branchView.branch.name }
        _internalId.update { branchView.branch.internalId }
        _street.update { branchView.branch.street }
        _country.update { branchView.branch.country }
        _governate.update { branchView.branch.governate }
        _postalCode.update { branchView.branch.postalCode }
        _regionCity.update { branchView.branch.regionCity }
        _optionalAddress.update {
            OptionalAddress(
                branchView.branch.buildingNumber,
                branchView.branch.floor,
                branchView.branch.floor,
                branchView.branch.landmark,
                branchView.branch.additionalInformation
            )
        }
        _selectedCompany.update { branchView.company }
    }

    private fun observerCompanies() {
        viewModelScope.launch {
            getCompaniesUseCase().collectLatest {
                _companies.update { _ -> it }
            }
        }
    }

    fun setAddress(address: Address) {
        address.let {
            _street.value = it.locality ?: it.getAddressLine(0) ?: ""
            _country.value = it.countryCode ?: ""
            _governate.value = it.adminArea ?: ""
            _postalCode.value = it.postalCode ?: ""
            _regionCity.value = it.subAdminArea ?: ""
        }
    }


    fun setName(name: String) {
        _name.update { name }
    }

    fun setInternalId(internalId: String) {
        _internalId.update { internalId }
    }

    fun setStreet(street: String) {
        _street.update { street }
    }

    fun setCountry(country: String) {
        _country.update { country }
    }

    fun setGovernate(governate: String) {
        _governate.update { governate }
    }

    fun setPostalCode(postalCode: String) {
        _postalCode.update { postalCode }
    }

    fun setRegionCity(regionCity: String) {
        _regionCity.update { regionCity }
    }

    fun setOptionalAddress(optionalAddress: OptionalAddress) {
        _optionalAddress.update { optionalAddress }
    }

    fun setSelectedCompany(company: Company) {
        _selectedCompany.update { company }
    }

    fun saveBranch() {
        viewModelScope.launch {
            _isLoading.update { true }
            val branch = Branch(
                name = name.value,
                internalId = internalId.value,
                street = _street.value,
                country = _country.value,
                governate = _governate.value,
                postalCode = _postalCode.value,
                regionCity = _regionCity.value,
                buildingNumber = optionalAddress.value.buildingNumber,
                floor = optionalAddress.value.floor,
                landmark = optionalAddress.value.landmark,
                additionalInformation = optionalAddress.value.additionalInformation,
                companyId = selectedCompany.value?.id ?: "",
                room = optionalAddress.value.room
            )
            val result = if (isUpdating)
                updateBranchUseCase(branch.copy(id = branchId))
            else
                createBranchUseCase(branch)

            _isLoading.update { false }
            result.ifFailure {
                val event = SnackBarEvent(
                    message = it ?: "Error",
                    actionLabel = "Retry",
                    action = ::saveBranch
                )
                showSnackBarEvent(event)
            }
        }
    }

}
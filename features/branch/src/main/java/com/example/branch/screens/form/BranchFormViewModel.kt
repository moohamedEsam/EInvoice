package com.example.branch.screens.form

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.OptionalAddress
import com.example.common.models.ValidationResult
import com.example.common.validators.validateUsername
import com.example.domain.branch.CreateBranchUseCase
import com.example.domain.branch.GetBranchUseCase
import com.example.domain.branch.UpdateBranchUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.models.Branch
import com.example.models.company.Company
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BranchFormViewModel(
    private val getBranchUseCase: GetBranchUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val createBranchUseCase: CreateBranchUseCase,
    private val updateBranchUseCase: UpdateBranchUseCase,
    private val branchId: String,
) : ViewModel() {
    private val isUpdating = branchId.isNotBlank()
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _internalId = MutableStateFlow("")
    val internalId = _internalId.asStateFlow()
    val internalIdValidationResult =
        internalId.map {
            when {
                it.isEmpty() -> ValidationResult.Empty
                it.any { char -> !char.isDigit() } -> ValidationResult.Invalid("Must be a number")
                else -> ValidationResult.Valid
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)


    private val _street = MutableStateFlow("")
    val streetValidationResult = _street.map {
        if (it.isBlank()) ValidationResult.Invalid("Street is required")
        else ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)


    private val _country = MutableStateFlow("")

    private val _governate = MutableStateFlow("")
    val governateValidationResult = _governate.map {
        if (it.isBlank()) ValidationResult.Invalid("Governate is required")
        else ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _postalCode = MutableStateFlow("")
    val postalCode = _postalCode.asStateFlow()


    private val _regionCity = MutableStateFlow("")
    val regionCityValidationResult = _regionCity.map {
        if (it.isBlank()) ValidationResult.Invalid("Region/City is required")
        else ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _companies = MutableStateFlow(emptyList<Company>())
    val companies = _companies.asStateFlow()

    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany = _selectedCompany.asStateFlow()


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
        regionCityValidationResult,
        _selectedCompany
    ) { nameValidationResult, internalIdValidationResult, streetValidationResult, governateValidationResult, regionCityValidationResult, selectedCompany ->
        nameValidationResult is ValidationResult.Valid &&
                internalIdValidationResult is ValidationResult.Valid &&
                streetValidationResult is ValidationResult.Valid &&
                governateValidationResult is ValidationResult.Valid &&
                regionCityValidationResult is ValidationResult.Valid &&
                selectedCompany != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observerCompanies()
        if (isUpdating) {
            observerBranch()
        }
    }

    private fun observerBranch() {
        viewModelScope.launch {
            getBranchUseCase(branchId).collectLatest(::fillForm)
        }
    }

    private fun fillForm(branch: Branch) {
        _name.update { branch.name }
        _internalId.update { branch.internalId }
        _street.update { branch.street }
        _country.update { branch.country }
        _governate.update { branch.governate }
        _postalCode.update { branch.postalCode }
        _regionCity.update { branch.regionCity }
        _optionalAddress.update {
            OptionalAddress(
                branch.buildingNumber,
                branch.floor,
                branch.floor,
                branch.landmark,
                branch.additionalInformation
            )
        }
        val selectedCompany =
            _companies.value.find { it.id == branch.companyId } ?: return
        _selectedCompany.update { selectedCompany }
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

    fun saveBranch(onResult: (com.example.common.models.Result<Branch>) -> Unit) {
        viewModelScope.launch {
            _isLoading.update { true }
            val branch = Branch(
                name = name.value,
                internalId = internalId.value,
                street = _street.value,
                country = _country.value,
                governate = _governate.value,
                postalCode = postalCode.value,
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
            onResult(result)
        }
    }

    fun <T1, T2, T3, T4, T5, T6, R> combine(
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
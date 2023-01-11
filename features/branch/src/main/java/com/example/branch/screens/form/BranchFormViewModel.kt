package com.example.branch.screens.form

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.branch.models.OptionalAddress
import com.example.common.models.ValidationResult
import com.example.common.validators.validateUsername
import com.example.domain.branch.CreateBranchUseCase
import com.example.domain.branch.GetBranchUseCase
import com.example.domain.branch.UpdateBranchUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.models.Company
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BranchFormViewModel(
    private val getBranchUseCase: GetBranchUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val createBranchUseCase: CreateBranchUseCase,
    private val updateBranchUseCase: UpdateBranchUseCase
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _internalId = MutableStateFlow("")
    val internalId = _internalId.asStateFlow()
    val internalIdValidationResult =
        internalId.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _street = MutableStateFlow("")
    val street = _street.asStateFlow()
    val streetValidationResult = street.map {
        if (it.isBlank()) ValidationResult.Invalid("Street is required")
        else ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _city = MutableStateFlow("")
    val city = _city.asStateFlow()
    val cityValidationResult = city.map {
        if (it.isBlank()) ValidationResult.Invalid("City is required")
        else ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _country = MutableStateFlow("")
    val country = _country.asStateFlow()

    private val _governate = MutableStateFlow("")
    val governate = _governate.asStateFlow()

    private val _postalCode = MutableStateFlow("")
    val postalCode = _postalCode.asStateFlow()

    private val _regionCity = MutableStateFlow("")
    val regionCity = _regionCity.asStateFlow()

    var companies = emptyList<Company>()
        private set

    private val _optionalAddress = MutableStateFlow(OptionalAddress("", "", "", "", ""))
    val optionalAddress = _optionalAddress.asStateFlow()

    val address = combine(
        street,
        city,
        country,
        governate,
    ) { street, city, country, governate ->
        Address(Locale.getDefault()).apply {
            this.thoroughfare = street
            this.locality = city
            this.countryName = country
            this.adminArea = governate
            this.postalCode = postalCode
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Address(Locale.getDefault()))

    init {
        viewModelScope.launch {
            getCompaniesUseCase().collectLatest { companies = it }
        }
    }

    fun setAddressUsingLatLng(address: Address) {
        address.let {
            _street.value = it.getAddressLine(0)
            _city.value = it.locality
            _country.value = it.countryName
            _governate.value = it.adminArea
            _postalCode.value = it.postalCode
            _regionCity.value = it.subAdminArea
        }
    }
}
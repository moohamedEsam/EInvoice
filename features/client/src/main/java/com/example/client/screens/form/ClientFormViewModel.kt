package com.example.client.screens.form

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.notBlankValidator
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePhone
import com.example.common.validators.validateUsername
import com.example.domain.client.CreateClientUseCase
import com.example.domain.client.GetClientViewUseCase
import com.example.domain.client.UpdateClientUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.models.Client
import com.example.models.ClientView
import com.example.models.company.Company
import com.example.models.OptionalAddress
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ClientFormViewModel(
    private val createClientUseCase: CreateClientUseCase,
    private val updateClientUseCase: UpdateClientUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getClientViewUseCase: GetClientViewUseCase,
    private val clientId: String
) : ViewModel() {
    private val isUpdating = clientId.isNotBlank()
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    val emailValidationResult =
        email.map(::validateEmail)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()
    val phoneValidationResult =
        phone.map(::validatePhone)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _businessType = MutableStateFlow(BusinessType.B)
    val businessType = _businessType.asStateFlow()

    private val _registrationNumber = MutableStateFlow("")
    val registrationNumber = _registrationNumber.asStateFlow()

    val registrationNumberValidationResult =
        combine(registrationNumber, businessType) { registrationNumber, businessType ->
            when {
                registrationNumber.isBlank() -> ValidationResult.Empty
                registrationNumber.any { char -> !char.isDigit() } -> ValidationResult.Invalid("registrationNumber must be digits")

                businessType == BusinessType.B && registrationNumber.length != 9 ->
                    ValidationResult.Invalid("registrationNumber must be 9 digits")

                businessType == BusinessType.P && registrationNumber.length != 14 ->
                    ValidationResult.Invalid("registrationNumber must be 14 digits")

                else -> ValidationResult.Valid
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)
    private val _street = MutableStateFlow("")


    private val streetValidationResult = _street.map(::notBlankValidator)
        .combineWithBusinessType()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _country = MutableStateFlow("")

    private val _governate = MutableStateFlow("")
    private val governateValidationResult = _governate.map(::notBlankValidator)
        .combineWithBusinessType()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _postalCode = MutableStateFlow("")


    private val _regionCity = MutableStateFlow("")

    private val regionCityValidationResult = _regionCity.map(::notBlankValidator)
        .combineWithBusinessType()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _companies = MutableStateFlow(emptyList<Company>())

    val companies = _companies.asStateFlow()

    private val _selectedCompany = MutableStateFlow<Company?>(null)
    val selectedCompany = _selectedCompany.asStateFlow()


    private val _optionalAddress = MutableStateFlow(OptionalAddress("", "", "", "", ""))
    val optionalAddress = _optionalAddress.asStateFlow()

    private val _taxStatus = MutableStateFlow(TaxStatus.Taxable)

    val taxStatus = _taxStatus.asStateFlow()
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
        emailValidationResult,
        streetValidationResult,
        governateValidationResult,
        registrationNumberValidationResult,
        phoneValidationResult,
        emailValidationResult,
        regionCityValidationResult,
    ) { validations ->
        validations.all { it is ValidationResult.Valid }
    }.combine(_selectedCompany) { isValid, company ->
        isValid && company != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)

    val isLoading = _isLoading.asStateFlow()
    init {
        observerCompanies()
        if (isUpdating) {
            observerClient()
        }
    }

    private fun observerClient() {
        viewModelScope.launch {
            getClientViewUseCase(clientId).collectLatest(::fillForm)
        }
    }

    private fun fillForm(clientView: ClientView) {
        _name.update { clientView.client.name }
        _email.update { clientView.client.email }
        _phone.update { clientView.client.phone }
        _registrationNumber.update { clientView.client.registrationNumber }
        _businessType.update { clientView.client.businessType }
        _taxStatus.update { clientView.client.status }
        _street.update { clientView.client.address?.street ?: "" }
        _country.update { clientView.client.address?.country ?: "" }
        _governate.update { clientView.client.address?.governate ?: "" }
        _postalCode.update { clientView.client.address?.postalCode ?: "" }
        _regionCity.update { clientView.client.address?.regionCity ?: "" }
        _optionalAddress.update {
            OptionalAddress(
                buildingNumber = clientView.client.address?.buildingNumber ?: "",
                floor = clientView.client.address?.floor ?: "",
                room = clientView.client.address?.floor ?: "",
                landmark = clientView.client.address?.landmark ?: "",
                additionalInformation = clientView.client.address?.additionalInformation ?: ""
            )
        }
        _selectedCompany.update { clientView.company }
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
            _country.value = it.countryName ?: ""
            _governate.value = it.adminArea ?: ""
            _postalCode.value = it.postalCode ?: ""
            _regionCity.value = it.subAdminArea ?: ""
        }
    }

    fun setName(name: String) {
        _name.update { name }
    }


    fun setEmail(email: String) {
        _email.update { email }
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

    fun setBusinessType(businessType: BusinessType) {
        _businessType.update { businessType }
    }

    fun setTaxStatus(taxStatus: TaxStatus) {
        _taxStatus.update { taxStatus }
    }

    fun setPhone(phone: String) {
        _phone.update { phone }
    }

    fun setRegistrationNumber(registrationNumber: String) {
        _registrationNumber.update { registrationNumber }
    }

    fun saveClient(onResult: (Result<Client>) -> Unit) {
        viewModelScope.launch {
            _isLoading.update { true }
            val client = Client(
                name = name.value,
                email = email.value,
                address = if (_businessType.value == BusinessType.B) com.example.models.utils.Address(
                    street = _street.value,
                    country = _country.value,
                    governate = _governate.value,
                    postalCode = _postalCode.value,
                    regionCity = _regionCity.value,
                    buildingNumber = optionalAddress.value.buildingNumber,
                    floor = optionalAddress.value.floor,
                    landmark = optionalAddress.value.landmark,
                    additionalInformation = optionalAddress.value.additionalInformation,
                    room = optionalAddress.value.room
                ) else null,
                registrationNumber = _registrationNumber.value,
                phone = _phone.value,
                companyId = _selectedCompany.value?.id ?: "",
                businessType = _businessType.value,
                status = _taxStatus.value
            )
            val result = if (isUpdating)
                updateClientUseCase(client.copy(id = clientId))
            else
                createClientUseCase(client)

            _isLoading.update { false }
            onResult(result)
        }
    }

    private fun Flow<ValidationResult>.combineWithBusinessType(): Flow<ValidationResult> =
        combine(businessType) { validationResult, businessType ->
            if (businessType == BusinessType.P)
                ValidationResult.Valid
            else
                validationResult

        }
}

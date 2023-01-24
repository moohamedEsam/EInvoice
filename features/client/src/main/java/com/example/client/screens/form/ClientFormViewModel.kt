package com.example.client.screens.form

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePhone
import com.example.common.validators.validateUsername
import com.example.domain.client.CreateClientUseCase
import com.example.domain.client.GetClientUseCase
import com.example.domain.client.UpdateClientUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.models.Client
import com.example.models.company.Company
import com.example.models.OptionalAddress
import com.example.models.utils.BusinessType
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ClientFormViewModel(
    private val getClientUseCase: GetClientUseCase,
    private val createClientUseCase: CreateClientUseCase,
    private val updateClientUseCase: UpdateClientUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
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

    private val _registrationNumber = MutableStateFlow("")
    val registrationNumber = _registrationNumber.asStateFlow()
    val registrationNumberValidationResult =
        registrationNumber.map {
            when {
                it.isBlank() -> ValidationResult.Empty
                it.any { char -> !char.isDigit() } -> ValidationResult.Invalid("registrationNumber must be digits")
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

    private val _businessType = MutableStateFlow(BusinessType.B)
    val businessType = _businessType.asStateFlow()

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
        flow = nameValidationResult,
        flow2 = emailValidationResult,
        flow3 = streetValidationResult,
        flow4 = governateValidationResult,
        flow5 = registrationNumberValidationResult,
        flow6 = _selectedCompany,
        flow7 = phoneValidationResult,
        flow8 = emailValidationResult,
        flow9 = regionCityValidationResult,
        flow10 = businessType
    ) { nameValidationResult, emailValidationResult,
        streetValidationResult, governateValidationResult,
        registrationNumber, company, phone,
        email, regionCity, businessType ->
        val isValidMainInfo = nameValidationResult is ValidationResult.Valid &&
                emailValidationResult is ValidationResult.Valid &&
                registrationNumber is ValidationResult.Valid &&
                phone is ValidationResult.Valid &&
                email is ValidationResult.Valid &&
                company != null

        if (businessType == BusinessType.B)
            isValidMainInfo && regionCity is ValidationResult.Valid &&
                    streetValidationResult is ValidationResult.Valid &&
                    governateValidationResult is ValidationResult.Valid
        else
            isValidMainInfo
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
            getClientUseCase(clientId).collectLatest(::fillForm)
        }
    }

    private fun fillForm(client: Client) {
        _name.update { client.name }
        _email.update { client.email }
        _phone.update { client.phone }
        _registrationNumber.update { client.registrationNumber }
        _businessType.update { client.businessType }
        _taxStatus.update { client.status }
        _street.update { client.address?.street ?: "" }
        _country.update { client.address?.country ?: "" }
        _governate.update { client.address?.governate ?: "" }
        _postalCode.update { client.address?.postalCode ?: "" }
        _regionCity.update { client.address?.regionCity ?: "" }
        _optionalAddress.update {
            OptionalAddress(
                buildingNumber = client.address?.buildingNumber ?: "",
                floor = client.address?.floor ?: "",
                room = client.address?.floor ?: "",
                landmark = client.address?.landmark ?: "",
                additionalInformation = client.address?.additionalInformation ?: ""
            )
        }
        val selectedCompany =
            _companies.value.find { it.id == client.companyId } ?: return
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
                    postalCode = postalCode.value,
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

    fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        flow9: Flow<T9>,
        flow10: Flow<T10>,
        transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
    ): Flow<R> =
        combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6,
            flow7,
            flow8,
            flow9,
            flow10
        ) { args: Array<*> ->
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
                args[7] as T8,
                args[8] as T9,
                args[9] as T10
            )
        }

}
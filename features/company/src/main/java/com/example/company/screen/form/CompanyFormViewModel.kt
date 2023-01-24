package com.example.company.screen.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.validateUsername
import com.example.common.validators.validateWebsite
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UpdateCompanyUseCase
import com.example.models.company.Company
import com.example.models.company.CompanySettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompanyFormViewModel(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val companyId: String
) : ViewModel() {
    private val isUpdating = companyId.isNotBlank()
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _registrationNumber = MutableStateFlow("")
    val registrationNumber = _registrationNumber.asStateFlow()
    val registrationNumberValidationResult =
        registrationNumber.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _ceo = MutableStateFlow("")
    val ceo = _ceo.asStateFlow()


    private val _website = MutableStateFlow("")
    val website = _website.asStateFlow()
    val websiteValidationResult = website.map {
        if (it.isEmpty()) ValidationResult.Valid
        else validateWebsite(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _clientId = MutableStateFlow("")
    val clientId = _clientId.asStateFlow()
    val clientIdValidationResult = clientId.map {
        when {
            it.isBlank() -> ValidationResult.Invalid("Client ID cannot be blank")
            else -> ValidationResult.Valid
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _clientSecret = MutableStateFlow("")
    val clientSecret = _clientSecret.asStateFlow()
    val clientSecretValidationResult = clientSecret.map {
        when {
            it.isBlank() -> ValidationResult.Invalid("Client Secret cannot be blank")
            else -> ValidationResult.Valid
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _tokenPin = MutableStateFlow("")
    val tokenPin = _tokenPin.asStateFlow()
    val tokenPinValidationResult = tokenPin.map {
        when {
            it.isBlank() -> ValidationResult.Invalid("Token PIN cannot be blank")
            else -> ValidationResult.Valid
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _taxActivityCode = MutableStateFlow("")
    val taxActivityCode = _taxActivityCode.asStateFlow()
    val taxActivityCodeValidationResult = taxActivityCode.map {
        when {
            it.isBlank() -> ValidationResult.Invalid("Tax Activity Code cannot be blank")
            else -> ValidationResult.Valid
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val isFormValid = combine(
        nameValidationResult,
        registrationNumberValidationResult,
        websiteValidationResult,
        clientIdValidationResult,
        clientSecretValidationResult,
        tokenPinValidationResult,
        taxActivityCodeValidationResult
    ) {
        it.all { validationResult -> validationResult == ValidationResult.Valid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


    init {
        if (isUpdating) {
            viewModelScope.launch {
                getCompanyUseCase(companyId).collectLatest {
                    _name.value = it.name
                    _registrationNumber.value = it.registrationNumber
                    _ceo.value = it.ceo
                    _website.value = it.website ?: ""
                    _phone.value = it.phone
                    _clientId.value = it.settings.clientId
                    _clientSecret.value = it.settings.clientSecret
                    _tokenPin.value = it.settings.tokenPin
                    _taxActivityCode.value = it.settings.taxActivityCode

                }
            }
        }
    }

    fun setName(name: String) {
        _name.update { name }
    }

    fun setRegistrationNumber(registrationNumber: String) {
        _registrationNumber.update { registrationNumber }
    }

    fun setCeo(ceo: String) {
        _ceo.update { ceo }
    }

    fun setWebsite(website: String) {
        _website.update { website }
    }

    fun setPhone(phone: String) {
        _phone.update { phone }
    }

    fun setClientId(clientId: String) {
        _clientId.update { clientId }
    }

    fun setClientSecret(clientSecret: String) {
        _clientSecret.update { clientSecret }
    }

    fun setTokenPin(tokenPin: String) {
        _tokenPin.update { tokenPin }
    }

    fun setTaxPayerActivityCode(taxPayerActivityCode: String) {
        _taxActivityCode.update { taxPayerActivityCode }
    }


    fun saveCompany(onResult: (result: Result<Company>) -> Unit = {}) {
        viewModelScope.launch {
            val company = Company(
                name = name.value,
                registrationNumber = registrationNumber.value,
                ceo = ceo.value,
                website = website.value,
                phone = phone.value,
                settings = CompanySettings(
                    clientId = clientId.value,
                    clientSecret = clientSecret.value,
                    tokenPin = tokenPin.value,
                    taxActivityCode = taxActivityCode.value,
                )
            )
            val result = if (isUpdating)
                updateCompanyUseCase(company.copy(id = companyId))
            else
                createCompanyUseCase(company)
            onResult(result)

        }
    }
}
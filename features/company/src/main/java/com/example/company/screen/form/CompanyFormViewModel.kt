package com.example.company.screen.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.ValidationResult
import com.example.common.validators.notBlankValidator
import com.example.common.validators.validateUsername
import com.example.common.validators.validateWebsite
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UpdateCompanyUseCase
import com.example.functions.BaseSnackBarManager
import com.example.functions.SnackBarManager
import com.example.models.company.Company
import com.example.models.company.CompanySettings
import com.example.models.company.CompanyView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompanyFormViewModel(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val companyId: String,
    private val snackBarManager: BaseSnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
    private val isUpdating = companyId.isNotBlank()
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _registrationNumber = MutableStateFlow("")
    val registrationNumber = _registrationNumber.asStateFlow()
    val registrationNumberValidationResult =
        registrationNumber.map {
            when {
                it.isEmpty() -> ValidationResult.Empty
                it.isBlank() -> ValidationResult.Invalid("Registration number is required")
                it.any { char -> !char.isDigit() } -> ValidationResult.Invalid("Registration number must be a number")
                it.length != 9 -> ValidationResult.Invalid("Registration number must be 9 digits long")
                else -> ValidationResult.Valid
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

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
    val clientIdValidationResult = clientId.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _clientSecret = MutableStateFlow("")
    val clientSecret = _clientSecret.asStateFlow()
    val clientSecretValidationResult = clientSecret.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _tokenPin = MutableStateFlow("")
    val tokenPin = _tokenPin.asStateFlow()
    val tokenPinValidationResult = tokenPin.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _taxActivityCode = MutableStateFlow("")
    val taxActivityCode = _taxActivityCode.asStateFlow()
    val taxActivityCodeValidationResult = taxActivityCode.map(::notBlankValidator)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

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
                getCompanyUseCase(companyId).collectLatest(::fillForm)
            }
        }
    }

    private fun fillForm(it: CompanyView) {
        _name.value = it.company.name
        _registrationNumber.value = it.company.registrationNumber
        _ceo.value = it.company.ceo
        _website.value = it.company.website ?: ""
        _phone.value = it.company.phone
        _clientId.value = it.company.settings.clientId
        _clientSecret.value = it.company.settings.clientSecret
        _tokenPin.value = it.company.settings.tokenPin
        _taxActivityCode.value = it.company.settings.taxActivityCode
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


    fun saveCompany() {
        viewModelScope.launch {
            val company = Company(
                name = name.value,
                registrationNumber = registrationNumber.value,
                ceo = ceo.value,
                website = website.value.ifBlank { null },
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

            val event = result.getSnackBarEvent(
                successMessage = "Company saved successfully",
                errorActionLabel = "Retry",
                errorAction = ::saveCompany
            )

            snackBarManager.showSnackBarEvent(event)
        }
    }
}
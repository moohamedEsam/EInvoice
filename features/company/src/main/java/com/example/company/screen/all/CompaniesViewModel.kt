package com.example.company.screen.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.ValidationResult
import com.example.common.validators.validateUsername
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.GetCompaniesUseCase
import com.example.domain.company.UpdateCompanyUseCase
import com.example.models.Company
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompaniesViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
) : ViewModel() {
    private val _companies = MutableStateFlow(emptyList<Company>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val companies = combine(_companies, _query){ companies, query ->
        if(query.isBlank()) return@combine companies
        companies.filter { company ->
            company.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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
    val ceoValidationResult = ceo.map(::validateUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _website = MutableStateFlow("")
    val website = _website.asStateFlow()
    val websiteValidationResult = website.map(::validateUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()
    val phoneValidationResult = phone.map(::validateUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val isFormValid = combine(
        nameValidationResult,
        ceoValidationResult
    ) { nameValidationResult, ceoValidationResult ->
        nameValidationResult is ValidationResult.Valid && ceoValidationResult is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog = _showCreateDialog.asStateFlow()

    init {
        viewModelScope.launch {
            getCompaniesUseCase().collectLatest {
                _companies.update { _ -> it }
            }
        }
    }

    fun setQuery(query: String) {
        _query.update { query }
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

    fun toggleCreateDialog() {
        _showCreateDialog.update { !it }
    }

    fun createCompany() {
        viewModelScope.launch {
            createCompanyUseCase(
                Company(
                    name = name.value,
                    registrationNumber = registrationNumber.value,
                    ceo = ceo.value,
                    website = website.value,
                    phone = phone.value
                )
            )
        }
    }
}
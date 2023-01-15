package com.example.company.screen.all

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.validatePhone
import com.example.common.validators.validateUsername
import com.example.common.validators.validateWebsite
import com.example.domain.company.*
import com.example.models.Company
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompaniesViewModel(
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val deleteCompanyUseCase: DeleteCompanyUseCase,
    private val undoDeleteCompanyUseCase: UndoDeleteCompanyUseCase
) : ViewModel() {
    private val _companies = MutableStateFlow(emptyList<Company>())
    private val _query = MutableStateFlow("")

    val query = _query.asStateFlow()

    val companies = combine(_companies, _query) { companies, query ->
        if (query.isBlank()) return@combine companies
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
    val websiteValidationResult = website.map {
        if (it.isEmpty()) ValidationResult.Valid
        else validateWebsite(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()
    val phoneValidationResult = phone.map(::validatePhone)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val isFormValid = combine(
        nameValidationResult,
        registrationNumberValidationResult,
        ceoValidationResult,
        websiteValidationResult,
        phoneValidationResult
    ) { name, registrationNumber, ceo, website, phone ->
        name is ValidationResult.Valid &&
                registrationNumber is ValidationResult.Valid &&
                ceo is ValidationResult.Valid &&
                website is ValidationResult.Valid &&
                phone is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog = _showCreateDialog.asStateFlow()
    private var companyId: String? = null

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

    fun editCompany(companyId: String) {
        val company = _companies.value.first { it.id == companyId }
        _name.update { company.name }
        _registrationNumber.update { company.registrationNumber }
        _ceo.update { company.ceo }
        _website.update { company.website ?: "" }
        _phone.update { company.phone }
        this.companyId = companyId
        toggleCreateDialog()
    }

    fun toggleCreateDialog() {
        _showCreateDialog.update { !it }
        if (!showCreateDialog.value)
            clearForm()
    }

    private fun clearForm() {
        _name.update { "" }
        _registrationNumber.update { "" }
        _ceo.update { "" }
        _website.update { "" }
        _phone.update { "" }
        _showCreateDialog.update { false }
        companyId = null
    }

    fun saveCompany(onResult: (result: Result<Company>) -> Unit = {}) {
        viewModelScope.launch {
            val result = if (companyId == null)
                createCompanyUseCase(
                    Company(
                        name = name.value,
                        registrationNumber = registrationNumber.value,
                        ceo = ceo.value,
                        website = website.value,
                        phone = phone.value
                    )
                )
            else
                updateCompanyUseCase(
                    Company(
                        name = name.value,
                        registrationNumber = registrationNumber.value,
                        ceo = ceo.value,
                        website = website.value,
                        phone = phone.value,
                        id = companyId!!
                    )
                )
            onResult(result)
            result.ifSuccess {
                toggleCreateDialog()
            }
        }
    }

    fun deleteCompany(company: Company, onResult: (result: Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            val result = deleteCompanyUseCase(company.id)
            onResult(result)
        }
    }

    fun undoDeleteCompany(company: Company) {
        viewModelScope.launch {
            undoDeleteCompanyUseCase(company.id)
        }
    }
}
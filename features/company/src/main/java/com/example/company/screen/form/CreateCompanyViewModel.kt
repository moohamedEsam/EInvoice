package com.example.company.screen.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.ValidationResult
import com.example.common.validators.validateUsername
import com.example.domain.company.CreateCompanyUseCase
import com.example.domain.company.GetCompanyUseCase
import com.example.domain.company.UpdateCompanyUseCase
import kotlinx.coroutines.flow.*

class CreateCompanyViewModel(
    private val createCompanyUseCase: CreateCompanyUseCase,
    private val updateCompanyUseCase: UpdateCompanyUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val companyId: String
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    val nameValidationResult =
        name.map(::validateUsername)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _registrationNumber = MutableStateFlow("")
    val registrationNumber = _registrationNumber.asStateFlow()

    private val _ceo = MutableStateFlow("")
    val ceo = _ceo.asStateFlow()
    val ceoValidationResult = ceo.map(::validateUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _website = MutableStateFlow("")
    val website = _website.asStateFlow()

    val isFormValid = combine(
        nameValidationResult,
        ceoValidationResult
    ) { nameValidationResult, ceoValidationResult ->
        nameValidationResult is ValidationResult.Valid && ceoValidationResult is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


}
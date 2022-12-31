package com.example.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import kotlinx.coroutines.flow.*

class LoginViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    val emailValidationResult = email.map { it.validateEmail() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    val passwordValidationResult = password.map { it.validatePassword() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val enableLogin = combine(emailValidationResult, passwordValidationResult) { email, password ->
        email is ValidationResult.Valid && password is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)


    fun setEmail(value: String) {
        _email.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

}
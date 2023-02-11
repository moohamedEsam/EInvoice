package com.example.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import com.example.domain.auth.LoginUseCase
import com.example.models.auth.Token
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    val emailValidationResult = email.map(::validateEmail)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    val passwordValidationResult = password.map(::validatePassword)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val enableLogin = combine(emailValidationResult, passwordValidationResult) { email, password ->
        email is ValidationResult.Valid && password is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun setEmail(value: String) {
        _email.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun login(onResult: (Result<Token>) -> Unit): Job = viewModelScope.launch {
        _isLoading.update { true }
        val result = loginUseCase(com.example.models.auth.Credentials(email.value, password.value))
        _isLoading.update { false }
        onResult(result)
    }

}
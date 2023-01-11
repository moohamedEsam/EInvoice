package com.example.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import com.example.domain.auth.LoginUseCase
import com.example.functions.SnackBarManager
import com.example.models.auth.Credentials
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
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

    fun login(onLoggedIn: () -> Unit): Job = viewModelScope.launch {
        _isLoading.update { true }
        val result = loginUseCase(Credentials(email.value, password.value))
        _isLoading.update { false }
        result.ifFailure {
            val event = SnackBarEvent(
                message = it ?: "Unknown error",
                action = { login(onLoggedIn) },
                actionLabel = "Retry"
            )
            showSnackBarEvent(event)
        }
        result.ifSuccess { onLoggedIn() }
    }

}
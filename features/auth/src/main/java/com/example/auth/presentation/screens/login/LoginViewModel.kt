package com.example.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.LoginUseCase
import com.example.auth.models.Credentials
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val snackBarChannel = Channel<SnackBarEvent>()
    val snackBar = snackBarChannel.receiveAsFlow()

    fun setEmail(value: String) {
        _email.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun login() = viewModelScope.launch {
        _isLoading.update { true }
        val result = loginUseCase(Credentials(email.value, password.value))
        result.ifFailure {
            val event = SnackBarEvent(it ?: "Unknown error", "Retry", ::login)
            snackBarChannel.send(event)
        }
        _isLoading.update { false }
    }

}
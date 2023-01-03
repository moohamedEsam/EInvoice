package com.example.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.domain.LoginUseCase
import com.example.auth.models.Credentials
import com.example.auth.models.Token
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
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

    private val _snackBarChannel = Channel<SnackBarEvent>()
    val snackBarChannel = _snackBarChannel.receiveAsFlow()

    fun setEmail(value: String) {
        _email.update { value }
    }

    fun setPassword(value: String) {
        _password.update { value }
    }

    fun login(onSuccess: (Token) -> Unit): Job = viewModelScope.launch {
        _isLoading.update { true }
        val result = loginUseCase(Credentials(email.value, password.value))
        _isLoading.update { false }
        result.ifFailure {
            val event = SnackBarEvent(it ?: "Unknown error", "Retry") { login(onSuccess) }
            _snackBarChannel.send(event)
        }
        result.ifSuccess(onSuccess)
    }

}
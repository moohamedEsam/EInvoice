package com.example.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import com.example.common.validators.validateEmail
import com.example.common.validators.validatePassword
import com.example.common.validators.validateUsername
import com.example.domain.auth.RegisterUseCase
import com.example.functions.SnackBarManager
import com.example.models.auth.Register
import com.example.models.auth.Token
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
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

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()
    val confirmPasswordValidationResult =
        combine(confirmPassword, password) { confirmPassword, password ->
            if (confirmPassword != password) ValidationResult.Invalid("Passwords do not match")
            else ValidationResult.Valid
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    val usernameValidationResult = username.map(::validateUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ValidationResult.Empty)

    val enableRegister = combine(
        emailValidationResult,
        passwordValidationResult,
        usernameValidationResult,
        confirmPasswordValidationResult
    ) { email, password, username, confirmPassword ->
        email is ValidationResult.Valid && password is ValidationResult.Valid
                && username is ValidationResult.Valid && confirmPassword is ValidationResult.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun setEmail(email: String) {
        _email.update { email }
    }

    fun setPassword(password: String) {
        _password.update { password }
    }

    fun setUsername(username: String) {
        _username.update { username }
    }

    fun setConfirmPassword(confirmPassword: String) {
        _confirmPassword.update { confirmPassword }
    }

    fun register(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.update { true }
            val result = registerUseCase(
                Register(
                    email.value,
                    password.value,
                    username.value
                )
            )
            _isLoading.update { false }
            result.ifFailure {
                val event = SnackBarEvent(
                    message = it ?: "Unknown error",
                    actionLabel = "Retry",
                    action = { register(onRegisterSuccess) }
                )
                showSnackBarEvent(event)
            }
            result.ifSuccess { onRegisterSuccess() }
        }
    }
}
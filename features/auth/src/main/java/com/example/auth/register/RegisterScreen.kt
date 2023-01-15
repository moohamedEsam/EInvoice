package com.example.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.common.components.OneTimeEventButton
import com.example.common.components.ValidationPasswordTextField
import com.example.common.components.ValidationTextField
import com.example.common.functions.handleSnackBarEvent
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.get
import org.koin.androidx.compose.viewModel

@Composable
fun RegisterScreen(
    logo: Any,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit,
    imageLoader: ImageLoader = get()
) {
    val viewModel: RegisterViewModel by viewModel()

    RegisterScreenContent(
        logo = logo,
        email = viewModel.email,
        emailValidation = viewModel.emailValidationResult,
        onEmailValueChange = viewModel::setEmail,
        password = viewModel.password,
        passwordValidation = viewModel.passwordValidationResult,
        onPasswordValueChange = viewModel::setPassword,
        confirmPassword = viewModel.confirmPassword,
        confirmPasswordValidation = viewModel.confirmPasswordValidationResult,
        onConfirmPasswordValueChange = viewModel::setConfirmPassword,
        registerButtonEnable = viewModel.enableRegister,
        loading = viewModel.isLoading,
        onRegisterButtonClick = {
            viewModel.register { result ->
                when (result) {
                    is com.example.common.models.Result.Success -> onRegistered()
                    is com.example.common.models.Result.Error -> onShowSnackBarEvent(
                        SnackBarEvent(
                            result.exception ?: "Unknown error"
                        )
                    )
                    else -> Unit
                }
            }
        },
        onLoginClick = onLoginClick,
        username = viewModel.username,
        usernameValidation = viewModel.usernameValidationResult,
        onUsernameValueChange = viewModel::setUsername,
        imageLoader = imageLoader
    )
}

@Composable
private fun RegisterScreenContent(
    logo: Any,
    username: StateFlow<String>,
    usernameValidation: StateFlow<ValidationResult>,
    onUsernameValueChange: (String) -> Unit,
    email: StateFlow<String>,
    emailValidation: StateFlow<ValidationResult>,
    onEmailValueChange: (String) -> Unit,
    password: StateFlow<String>,
    passwordValidation: StateFlow<ValidationResult>,
    onPasswordValueChange: (String) -> Unit,
    confirmPassword: StateFlow<String>,
    confirmPasswordValidation: StateFlow<ValidationResult>,
    onConfirmPasswordValueChange: (String) -> Unit,
    registerButtonEnable: StateFlow<Boolean>,
    loading: StateFlow<Boolean>,
    onRegisterButtonClick: () -> Unit,
    onLoginClick: () -> Unit,
    imageLoader: ImageLoader
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = logo,
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth(),
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            ValidationTextField(
                valueState = username,
                validationState = usernameValidation,
                label = "Username",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onUsernameValueChange
            )

            ValidationTextField(
                valueState = email,
                validationState = emailValidation,
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onEmailValueChange
            )

            ValidationPasswordTextField(
                valueState = password,
                validationState = passwordValidation,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onPasswordValueChange
            )

            ValidationPasswordTextField(
                valueState = confirmPassword,
                validationState = confirmPasswordValidation,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onConfirmPasswordValueChange,
                label = "Confirm Password"
            )

            OneTimeEventButton(
                enabled = registerButtonEnable,
                loading = loading,
                label = "Register",
                onClick = onRegisterButtonClick,
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(onClick = onLoginClick) {
                Text("Already have an account? Login")
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(
        logo = "",
        username = MutableStateFlow("mohamed"),
        usernameValidation = MutableStateFlow(ValidationResult.Empty),
        onUsernameValueChange = {},
        email = MutableStateFlow(""),
        emailValidation = MutableStateFlow(ValidationResult.Empty),
        onEmailValueChange = {},
        password = MutableStateFlow(""),
        passwordValidation = MutableStateFlow(ValidationResult.Empty),
        onPasswordValueChange = {},
        confirmPassword = MutableStateFlow(""),
        confirmPasswordValidation = MutableStateFlow(ValidationResult.Empty),
        onConfirmPasswordValueChange = {},
        registerButtonEnable = MutableStateFlow(true),
        loading = MutableStateFlow(false),
        onRegisterButtonClick = {},
        onLoginClick = {},
        imageLoader = ImageLoader(LocalContext.current)
    )
}
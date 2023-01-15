package com.example.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.common.models.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.get
import org.koin.androidx.compose.viewModel

@Composable
fun LoginScreen(
    logo: Any,
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    onShowSnackBarEvent: (SnackBarEvent) -> Unit,
    imageLoader: ImageLoader = get()
) {
    val viewModel: LoginViewModel by viewModel()

    LoginScreenContent(
        logo = logo,
        email = viewModel.email,
        emailValidation = viewModel.emailValidationResult,
        onEmailValueChange = viewModel::setEmail,
        password = viewModel.password,
        passwordValidation = viewModel.passwordValidationResult,
        onPasswordValueChange = viewModel::setPassword,
        loginButtonEnable = viewModel.enableLogin,
        loading = viewModel.isLoading,
        onLoginButtonClick = {
            viewModel.login { result ->
                if (result is Result.Success)
                    onLoggedIn()
                else if (result is Result.Error)
                    onShowSnackBarEvent(SnackBarEvent(result.exception ?: "Unknown error"))
            }
        },
        onRegisterClick = onRegisterClick,
        imageLoader = imageLoader
    )
}

@Composable
private fun LoginScreenContent(
    logo: Any,
    email: StateFlow<String>,
    emailValidation: StateFlow<ValidationResult>,
    onEmailValueChange: (String) -> Unit,
    password: StateFlow<String>,
    passwordValidation: StateFlow<ValidationResult>,
    onPasswordValueChange: (String) -> Unit,
    loginButtonEnable: StateFlow<Boolean>,
    loading: StateFlow<Boolean>,
    onLoginButtonClick: () -> Unit,
    onRegisterClick: () -> Unit,
    imageLoader: ImageLoader
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
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

            OneTimeEventButton(
                enabled = loginButtonEnable,
                loading = loading,
                modifier = Modifier.fillMaxWidth(),
                onClick = onLoginButtonClick,
                label = "Login"
            )

            TextButton(onClick = onRegisterClick) {
                Text("Don't have an account? Register")
            }
        }
    }
}


@Preview
@Composable
fun LoginScreenContentPreview() {
    //comment the image loader to see the preview
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoginScreenContent(
            logo = "",
            email = MutableStateFlow(""),
            emailValidation = MutableStateFlow(ValidationResult.Empty),
            onEmailValueChange = {},
            password = MutableStateFlow(""),
            passwordValidation = MutableStateFlow(ValidationResult.Empty),
            onPasswordValueChange = {},
            loginButtonEnable = MutableStateFlow(true),
            onLoginButtonClick = {},
            onRegisterClick = {},
            imageLoader = ImageLoader(LocalContext.current),
            loading = MutableStateFlow(false)
        )
    }
}
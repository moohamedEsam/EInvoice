package com.example.auth.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
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
fun LoginScreen(
    logo: Any,
    snackbarHostState: SnackbarHostState,
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit
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
        onLoginButtonClick = viewModel::login,
        onRegisterClick = onRegisterClick
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.snackBar.collectLatest {
            snackbarHostState.handleSnackBarEvent(it)
        }
    }
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
    imageLoader: ImageLoader = get()
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            LoginButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = loginButtonEnable,
                onClick = onLoginButtonClick,
                loading = loading
            )

            TextButton(onClick = onRegisterClick) {
                Text("Don't have an account? Register")
            }
        }
    }
}

@Composable
private fun ColumnScope.LoginButton(
    enabled: StateFlow<Boolean>,
    loading: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isEnabled by enabled.collectAsState()
    val isLoading by loading.collectAsState()
    if (isLoading)
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    else
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = modifier,
            shape = MaterialTheme.shapes.small
        ) {
            Text("Login", textAlign = TextAlign.Center)
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
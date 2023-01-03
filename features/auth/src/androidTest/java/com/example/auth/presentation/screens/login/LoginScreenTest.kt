package com.example.auth.presentation.screens.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil.ImageLoader
import com.example.auth.presentation.di.authModule
import com.example.network.di.networkModule
import com.google.common.truth.Truth.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private var loggedIn = false
    @Before
    fun setUp() {
        startKoin {
            modules(listOf(authModule, networkModule))
            androidContext(ApplicationProvider.getApplicationContext())
        }
        loggedIn = false
        composeTestRule.setContent {
            LoginScreen(
                logo = Any(),
                snackbarHostState = SnackbarHostState(),
                onLoggedIn = { loggedIn = true },
                onRegisterClick = {},
                imageLoader = ImageLoader(ApplicationProvider.getApplicationContext())
            )
        }
    }

    @Test
    fun login_validInput_LoginButtonEnabled() {
        composeTestRule.onNodeWithText("Email").performTextInput("system@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("system")
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }

    @Test
    fun login_invalidInput_LoginButtonDisabled() {
        composeTestRule.onNodeWithText("Email").performTextInput("system")
        composeTestRule.onNodeWithText("Password").performTextInput("123")
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

}
package com.example.einvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.MainActivityViewModel
import com.example.auth.login.LoginScreenRoute
import com.example.company.screen.CompaniesScreenRoute
import com.example.company.screen.all.CompaniesScreen
import com.example.einvoice.presentation.shared.EInvoiceLayout
import com.example.einvoice.ui.theme.EInvoiceTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.userLoggedIn.value == null
            }
        }
        super.onCreate(savedInstanceState)
        setContent {
            EInvoiceTheme {
                val userLoggedIn by viewModel.userLoggedIn.collectAsState()
                val startScreen by remember {
                    derivedStateOf {
                        if (userLoggedIn == true)
                            CompaniesScreenRoute
                        else
                            LoginScreenRoute

                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EInvoiceLayout(startScreen)
                }
            }
        }
    }
}
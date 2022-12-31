package com.example.auth.presentation.di

import com.example.auth.data.AuthRepository
import com.example.auth.data.KtorAuthRepository
import com.example.auth.domain.LoginUseCase
import com.example.auth.domain.LogoutUseCase
import com.example.auth.domain.RegisterUseCase
import com.example.auth.presentation.screens.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { parametersHolder -> KtorAuthRepository(parametersHolder[0]) }
    single { LoginUseCase(get<AuthRepository>()::login) }
    single { LogoutUseCase(get<AuthRepository>()::logout) }
    single { RegisterUseCase(get<AuthRepository>()::register) }
    viewModel { LoginViewModel() }
}
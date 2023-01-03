package com.example.domain.di

import com.example.data.AuthRepository
import com.example.domain.auth.LoginUseCase
import com.example.domain.auth.LogoutUseCase
import com.example.domain.auth.RegisterUseCase
import org.koin.dsl.module

val domainModule = module {
    single { LoginUseCase(get<AuthRepository>()::login) }
    single { LogoutUseCase(get<AuthRepository>()::logout) }
    single { RegisterUseCase(get<AuthRepository>()::register) }
}
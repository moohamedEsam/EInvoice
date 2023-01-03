package com.example.data.di

import com.example.data.AuthRepository
import com.example.data.KtorAuthRepository
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { KtorAuthRepository(get()) }
}
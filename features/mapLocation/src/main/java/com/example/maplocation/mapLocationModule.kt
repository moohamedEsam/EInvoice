package com.example.maplocation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mapLocationModule = module {
    viewModel { MapViewModel() }
}
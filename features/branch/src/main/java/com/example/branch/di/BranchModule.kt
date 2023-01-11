package com.example.branch.di

import com.example.branch.screens.form.BranchFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val branchModule = module {
    viewModel { BranchFormViewModel(get(), get(), get(), get()) }
}
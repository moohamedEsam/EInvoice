package com.example.branch.di

import com.example.branch.screens.all.BranchesViewModel
import com.example.branch.screens.form.BranchFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val branchModule = module {
    viewModel { params -> BranchFormViewModel(get(), get(), get(), get(), params[0]) }
    viewModel { BranchesViewModel(get()) }
}
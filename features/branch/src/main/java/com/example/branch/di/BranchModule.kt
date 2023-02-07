package com.example.branch.di

import com.example.branch.screens.all.BranchesViewModel
import com.example.branch.screens.dashboard.BranchDashboardViewModel
import com.example.branch.screens.form.BranchFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val branchModule = module {
    viewModel { params ->
        BranchFormViewModel(
            getBranchesUseCase = get(),
            getCompaniesUseCase = get(),
            createBranchUseCase = get(),
            updateBranchUseCase = get(),
            branchId = params[0]
        )
    }
    viewModel { (branchId: String) ->
        BranchDashboardViewModel(
            branchId = branchId,
            getBranchViewUseCase = get(),
            getDocumentsByBranchUseCase = get(),
            deleteBranchUseCase = get(),
            undoDeleteBranchUseCase = get()
        )
    }
    viewModel { BranchesViewModel(get()) }
}
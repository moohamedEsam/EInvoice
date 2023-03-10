package com.example.document.di

import com.example.document.screens.all.DocumentsViewModel
import com.example.document.screens.details.DocumentDetailsViewModel
import com.example.document.screens.form.DocumentFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val documentsModule = module {
    viewModel {
        DocumentsViewModel(
            getDocumentsPagingSourceUseCase = get(),
            networkObserver = get(),
            cancelDocumentUseCase = get(),
            syncDocumentsStatusUseCase = get(),
            sendDocumentUseCase = get(),
            getCompaniesUseCase = get(),
            getBranchesPagingSourceUseCase = get(),
            getClientsPagingSourceUseCase = get(),
        )
    }
    viewModel { params ->
        DocumentFormViewModel(
            createDocumentUseCase = get(),
            updateDocumentUseCase = get(),
            getCompaniesViewsUseCase = get(),
            getItemsByBranchUseCase = get(),
            getTaxTypesUseCase = get(),
            documentId = params[0],
            getDocumentUseCase = get(),
            getDocumentsInternalIdsByCompanyIdUseCase = get(),
            snackBarManager = get(),
        )
    }

    viewModel { (documentId: String) ->
        DocumentDetailsViewModel(
            getDocumentUseCase = get(),
            deleteDocumentUseCase = get(),
            undoDeleteDocumentUseCase = get(),
            documentId = documentId,
            snackBarManager = get(),
        )
    }
}
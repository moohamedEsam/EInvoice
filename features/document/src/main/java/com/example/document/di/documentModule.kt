package com.example.document.di

import com.example.document.screens.all.DocumentsViewModel
import com.example.document.screens.details.DocumentDetailsViewModel
import com.example.document.screens.form.DocumentFormViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val documentsModule = module {
    viewModel { DocumentsViewModel(get()) }
    viewModel { params ->
        DocumentFormViewModel(
            getDocumentsUseCase = get(),
            createDocumentUseCase = get(),
            updateDocumentUseCase = get(),
            getCompaniesViewsUseCase = get(),
            getItemsUseCase = get(),
            getTaxTypesUseCase = get(),
            documentId = params[0]
        )
    }

    viewModel { (documentId: String) ->
        DocumentDetailsViewModel(
            getDocumentUseCase = get(),
            deleteDocumentUseCase = get(),
            undoDeleteDocumentUseCase = get(),
            documentId = documentId
        )
    }
}
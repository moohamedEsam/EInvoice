package com.example.document.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.document.DeleteDocumentUseCase
import com.example.domain.document.GetDocumentUseCase
import com.example.domain.document.UndoDeleteDocumentUseCase
import com.example.functions.SnackBarManager
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.empty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DocumentDetailsViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val undoDeleteDocumentUseCase: UndoDeleteDocumentUseCase,
    private val documentId: String,
    private val snackBarManager: SnackBarManager
) : ViewModel(), SnackBarManager by snackBarManager {
    private val _document = MutableStateFlow(DocumentView.empty())
    val document = _document.asStateFlow()

    val isEditEnabled = _document.map {
        it.status != DocumentStatus.Submitted && it.status != DocumentStatus.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _showTaxDialog = MutableStateFlow(false)
    val showTaxDialog = _showTaxDialog.asStateFlow()
    private val _invoiceLine = MutableStateFlow(InvoiceLineView.empty())
    val invoiceLine = _invoiceLine.asStateFlow()

    fun deleteDocument() {
        viewModelScope.launch {
            val result = deleteDocumentUseCase(documentId)
            val event = result.getSnackBarEvent(
                successMessage = "Document deleted",
                successActionLabel = "Undo",
                successAction = { undoDeleteDocumentUseCase(documentId) },
                errorActionLabel = "Retry",
                errorAction = ::deleteDocument
            )
            showSnackBarEvent(event)
        }
    }

    init {
        viewModelScope.launch {
            getDocumentUseCase(documentId).collect {
                _document.value = it
            }
        }
    }

    fun onShowTaxesClick(invoiceLineView: InvoiceLineView) {
        _invoiceLine.value = invoiceLineView
        _showTaxDialog.value = true
    }

    fun onTaxDialogDismiss() {
        _showTaxDialog.value = false
    }
}
package com.example.document.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.models.Result
import com.example.domain.document.DeleteDocumentUseCase
import com.example.domain.document.GetDocumentUseCase
import com.example.domain.document.UndoDeleteDocumentUseCase
import com.example.models.document.DocumentStatus
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.InvoiceTax
import com.example.models.invoiceLine.empty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DocumentDetailsViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    private val undoDeleteDocumentUseCase: UndoDeleteDocumentUseCase,
    private val documentId: String,
) : ViewModel() {
    private val _document = MutableStateFlow(DocumentView.empty())
    val document = _document.asStateFlow()

    val isEditEnabled = _document.map {
        it.status != DocumentStatus.Submitted && it.status != DocumentStatus.Valid
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private val _showTaxDialog = MutableStateFlow(false)
    val showTaxDialog = _showTaxDialog.asStateFlow()
    private val _invoiceLine = MutableStateFlow(InvoiceLineView.empty())
    val invoiceLine = _invoiceLine.asStateFlow()

    fun deleteDocument(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteDocumentUseCase(documentId)
            onResult(result)
        }
    }

    fun undoDeleteDocument(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = undoDeleteDocumentUseCase(documentId)
            onResult(result)
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
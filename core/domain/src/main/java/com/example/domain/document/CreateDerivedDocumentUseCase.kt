package com.example.domain.document

import com.example.common.models.Result
import com.example.models.document.Document
import com.example.models.invoiceLine.InvoiceLine

fun interface CreateDerivedDocumentUseCase : suspend (String, List<InvoiceLine>) -> Result<Document>
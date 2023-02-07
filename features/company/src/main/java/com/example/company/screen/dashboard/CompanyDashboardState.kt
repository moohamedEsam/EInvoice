package com.example.company.screen.dashboard

import com.example.models.company.CompanyView
import com.example.models.document.DocumentView
import com.example.models.invoiceLine.InvoiceLineView
import java.util.*

data class CompanyDashboardState(
    val companyView: CompanyView,
    val invoices: List<InvoiceLineView>,
    val documents: List<DocumentView>,
    val isDeleteEnabled: Boolean,
    val pickedDate: Date,
)

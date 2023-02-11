package com.example.branch.screens.dashboard

import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.BranchView
import com.example.models.branch.empty
import com.example.models.company.Company
import com.example.models.company.empty
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.UnitValue
import com.example.models.invoiceLine.empty
import com.example.models.item.Item
import com.example.models.item.empty
import java.util.Date
import kotlin.random.Random

data class BranchDashboardState(
    val branchView: BranchView,
    val documents: List<DocumentView>,
    val pickedDate: Date,
    val isDeleteEnabled: Boolean = false,
){
    companion object
}

fun BranchDashboardState.Companion.random() = BranchDashboardState(
    branchView = BranchView(
        branch = Branch.empty().copy(name = "Branch name"),
        items = List(Random.nextInt(0, 100)) { Item.empty() },
        company = Company.empty(),
    ),
    documents = List(Random.nextInt(0, 100)) {
        DocumentView.empty().copy(
            invoices = List(Random.nextInt(0, 20)) {
                InvoiceLineView.empty().copy(
                    quantity = Random.nextInt(0, 20).toFloat(),
                    unitValue = UnitValue(
                        currencySold = "EGP",
                        currencyEgp = 15.0,
                    )
                )
            },
            branch = Branch.empty().copy(name = "Branch $it"),
            client = Client.empty().copy(name = "Client $it"),
        )
    },
    pickedDate = Date(),
    isDeleteEnabled = false,
)
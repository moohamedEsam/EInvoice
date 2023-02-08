package com.example.client.screens.dashboard

import com.example.models.Client
import com.example.models.branch.Branch
import com.example.models.branch.empty
import com.example.models.document.DocumentView
import com.example.models.document.empty
import com.example.models.empty
import com.example.models.invoiceLine.InvoiceLineView
import com.example.models.invoiceLine.UnitValue
import com.example.models.invoiceLine.empty
import java.util.*
import kotlin.random.Random

data class ClientDashBoardState(
    val client: Client,
    val documents: List<DocumentView>,
    val endDate: Date,
    val isDeleteEnabled: Boolean,
){
    companion object
}

fun ClientDashBoardState.Companion.random() = ClientDashBoardState(
    client = Client.empty().copy(name = "Client Name"),
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
    endDate = Date(),
    isDeleteEnabled = false,
)

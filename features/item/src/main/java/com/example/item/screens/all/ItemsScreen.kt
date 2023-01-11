package com.example.item.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.einvoicecomponents.ItemCard
import com.example.einvoicecomponents.ListScreenContent
import com.example.item.screens.form.ItemFormScreenContent
import com.example.models.item.Item
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel


@Composable
fun ItemsScreen() {
    val viewModel: ItemsViewModel by viewModel()
    val items by viewModel.items.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    ItemsScreenContent(
        items = items,
        queryState = viewModel.query,
        onQueryChange = viewModel::onQueryChange,
        onItemCreate = viewModel::onAddItemClicked,
        onItemClick = { viewModel.onItemClicked(it) }
    )
    if (showDialog)
        ItemFormDialog(viewModel)

}

@Composable
private fun ItemFormDialog(
    viewModel: ItemsViewModel
) {
    Dialog(onDismissRequest = viewModel::dismissDialog) {
        Card {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemFormScreenContent(
                    name = viewModel.name,
                    onNameChange = viewModel::setName,
                    nameValidationResult = viewModel.nameValidationResult,
                    internalCode = viewModel.internalCode,
                    onInternalCodeChange = viewModel::setInternalCode,
                    internalCodeValidationResult = viewModel.internalCodeValidationResult,
                    description = viewModel.description,
                    onDescriptionChange = viewModel::setDescription,
                    price = viewModel.price,
                    onPriceChange = viewModel::setPrice,
                    priceValidationResult = viewModel.priceValidationResult,
                    status = viewModel.status,
                    onStatusChange = viewModel::setStatus,
                    itemCode = viewModel.itemCode,
                    onItemCodeChange = viewModel::setItemCode,
                    itemCodeValidationResult = viewModel.itemCodeValidationResult,
                    unitTypes = viewModel.unitTypes,
                    selectedUnitType = viewModel.selectedUnitType,
                    onUnitTypeChange = viewModel::setUnitType,
                    branches = viewModel.branches,
                    selectedBranch = viewModel.selectedBranch,
                    onBranchChange = viewModel::setBranch,
                    isEnabled = viewModel.isFormValid,
                    isLoading = viewModel.isLoading,
                    onFormSubmit = {
                        viewModel.saveItem()
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemsScreenContent(
    items: List<Item>,
    queryState: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onItemCreate: () -> Unit,
    onItemClick: (Item) -> Unit
) {
    ListScreenContent(
        queryState = queryState,
        onQueryChange = onQueryChange,
        floatingButtonText = "Create New Item",
        onFloatingButtonClick = onItemCreate
    ) {
        this.items(items) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenPreview() {
    val items = List(10) {
        Item(
            id = it.toString(),
            name = "Item $it",
            description = "Description $it",
            price = 10.0,
            status = TaxStatus.Taxable,
            itemCode = "itemCode",
            unitTypeCode = "unitTypeCode",
            branchId = "branchId",
            internalCode = "internalCode"
        )
    }

    ItemsScreenContent(
        items = items,
        queryState = MutableStateFlow(""),
        onQueryChange = {},
        onItemCreate = {},
        onItemClick = {}
    )
}




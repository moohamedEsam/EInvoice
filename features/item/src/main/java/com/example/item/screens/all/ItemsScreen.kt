package com.example.item.screens.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.Result
import com.example.common.models.SnackBarEvent
import com.example.einvoicecomponents.ListScreenContent
import com.example.item.screens.form.ItemFormScreenContent
import com.example.models.item.Item
import com.example.models.item.empty
import com.example.models.utils.TaxStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.viewModel


@Composable
fun ItemsScreen(
    onShowSnackbarEvent: (SnackBarEvent) -> Unit,
) {
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
        ItemFormDialog(viewModel, onShowSnackbarEvent)

}

@Composable
private fun ItemFormDialog(
    viewModel: ItemsViewModel,
    onShowSnackbarEvent: (SnackBarEvent) -> Unit
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
                        viewModel.saveItem { result ->
                            val snackBarEvent = if (result is Result.Success)
                                SnackBarEvent("Item saved successfully")
                            else
                                SnackBarEvent(
                                    (result as? Result.Error)?.exception ?: "Error saving item",
                                    actionLabel = "Retry",
                                    action = { viewModel.saveItem {} }
                                )
                            onShowSnackbarEvent(snackBarEvent)
                        }
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
        adaptiveItemSize = 200.dp,
        onFloatingButtonClick = onItemCreate,
        listContent = {
            this.items(items) { item ->
                ItemCard(item = item, onClick = { onItemClick(item) })
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemCard(
    item: Item,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedCard(modifier = modifier, onClick = onClick) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = item.description, maxLines = 2)
            Text(text = "price: ${item.price}")
            Text(text = "taxStatus: ${item.status}")
            Text(text = "itemCode: ${item.itemCode}")
            Text(text = "unitTypeCode: ${item.unitTypeCode}")
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
            branchId = "branchId"
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




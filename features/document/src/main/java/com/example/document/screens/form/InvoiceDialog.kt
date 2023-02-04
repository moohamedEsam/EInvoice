package com.example.document.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.common.models.ValidationResult
import com.example.einvoicecomponents.BaseExposedDropDownMenu
import com.example.einvoicecomponents.ValidationOutlinedTextField
import com.example.models.item.Item
import com.example.models.item.empty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun InvoiceDialog(
    itemsState: StateFlow<List<Item>>,
    selectedItemState: StateFlow<Item?>,
    onItemSelect: (Item) -> Unit,
    priceState: StateFlow<String>,
    onPriceChange: (String) -> Unit,
    priceValidationResult: StateFlow<ValidationResult>,
    quantityState: StateFlow<String>,
    onQuantityChange: (String) -> Unit,
    quantityValidationResult: StateFlow<ValidationResult>,
    discountState: StateFlow<String>,
    onDiscountChange: (String) -> Unit,
    discountValidationResult: StateFlow<ValidationResult>,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss){
        Card {
            InvoiceDialogContent(
                itemsState = itemsState,
                selectedItemState = selectedItemState,
                onItemSelect = onItemSelect,
                priceState = priceState,
                onPriceChange = onPriceChange,
                priceValidationResult = priceValidationResult,
                quantityState = quantityState,
                onQuantityChange = onQuantityChange,
                quantityValidationResult = quantityValidationResult,
                discountState = discountState,
                onDiscountChange = onDiscountChange,
                discountValidationResult = discountValidationResult,
                onAddClick = onAddClick,
            )
        }
    }
}

@Composable
private fun InvoiceDialogContent(
    itemsState: StateFlow<List<Item>>,
    selectedItemState: StateFlow<Item?>,
    onItemSelect: (Item) -> Unit,
    priceState: StateFlow<String>,
    onPriceChange: (String) -> Unit,
    priceValidationResult: StateFlow<ValidationResult>,
    quantityState: StateFlow<String>,
    onQuantityChange: (String) -> Unit,
    quantityValidationResult: StateFlow<ValidationResult>,
    discountState: StateFlow<String>,
    onDiscountChange: (String) -> Unit,
    discountValidationResult: StateFlow<ValidationResult>,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseExposedDropDownMenu(
            optionsState = itemsState,
            selectedOptionState = selectedItemState,
            onOptionSelect = onItemSelect,
            textFieldValue = { it?.name ?: "" },
            textFieldLabel = "Item",
            optionsLabel = { it.name }
        )

        ValidationOutlinedTextField(
            valueState = priceState,
            validationState = priceValidationResult,
            label = "Price",
            onValueChange = onPriceChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ValidationOutlinedTextField(
            valueState = quantityState,
            validationState = quantityValidationResult,
            label = "Quantity",
            onValueChange = onQuantityChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        ValidationOutlinedTextField(
            valueState = discountState,
            validationState = discountValidationResult,
            label = "Discount",
            onValueChange = onDiscountChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = onAddClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InvoiceDialogPreview() {
    InvoiceDialogContent(
        itemsState = MutableStateFlow(listOf(Item.empty())),
        selectedItemState = MutableStateFlow(Item.empty()),
        onItemSelect = {},
        priceState = MutableStateFlow(""),
        onPriceChange = {},
        priceValidationResult = MutableStateFlow(ValidationResult.Valid),
        quantityState = MutableStateFlow(""),
        onQuantityChange = {},
        quantityValidationResult = MutableStateFlow(ValidationResult.Valid),
        discountState = MutableStateFlow(""),
        onDiscountChange = {},
        discountValidationResult = MutableStateFlow(ValidationResult.Valid),
        onAddClick = {},
    )
}
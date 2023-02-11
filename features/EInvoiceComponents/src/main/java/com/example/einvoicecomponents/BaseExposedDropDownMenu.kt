package com.example.einvoicecomponents

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> BaseExposedDropDownMenu(
    optionsState: StateFlow<List<T>>,
    selectedOptionState: StateFlow<T?>,
    onOptionSelect: (T) -> Unit,
    textFieldValue: (T?) -> String,
    textFieldLabel: String,
    optionsLabel: (T) -> String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    filterCriteria: (T, String) -> Boolean = { _, _ -> true },
    textFieldType: DropDownMenuTextFieldType = DropDownMenuTextFieldType.Normal,
) {

    val options by optionsState.collectAsState()
    val selectedOption by selectedOptionState.collectAsState()
    var query by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = selectedOption) {
        query = textFieldValue(selectedOption)
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val filteredItems by remember {
        derivedStateOf {
            options.filter { filterCriteria(it, query) }
        }
    }
    Column(
        modifier = modifier
    ) {
        BoxTextField(
            textFieldType = textFieldType,
            query = query,
            isExpanded = isExpanded,
            onValueChange = { query = it },
            onCheckedChange = { isExpanded = it },
            textFieldLabel = textFieldLabel
        )

        LazyColumn(
            modifier = Modifier
                .heightIn(max = (LocalConfiguration.current.screenHeightDp / 5).dp)
                .animateContentSize()
        ) {
            if (isExpanded)
                items(filteredItems) {
                    Text(
                        text = optionsLabel(it),
                        style = textStyle,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelect(it)
                                isExpanded = false
                            }
                    )
                    Divider()
                }
        }
    }

}

@Composable
private fun BoxTextField(
    textFieldType: DropDownMenuTextFieldType,
    query: String,
    isExpanded: Boolean,
    onValueChange: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    textFieldLabel: String
) {
    when (textFieldType) {
        DropDownMenuTextFieldType.Outlined -> BoxOutlinedTextField(
            query = query,
            onValueChange = onValueChange,
            textFieldLabel = textFieldLabel,
            isExpanded = isExpanded,
            onCheckedChange = onCheckedChange
        )
        else -> BoxNormalTextField(
            query = query,
            onValueChange = onValueChange,
            textFieldLabel = textFieldLabel,
            isExpanded = isExpanded,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxOutlinedTextField(
    query: String,
    onValueChange: (String) -> Unit,
    textFieldLabel: String,
    isExpanded: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onValueChange,
        label = { Text(text = textFieldLabel) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconToggleButton(
                checked = isExpanded,
                onCheckedChange = onCheckedChange
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ArrowDropUp
                    else Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxNormalTextField(
    query: String,
    onValueChange: (String) -> Unit,
    textFieldLabel: String,
    isExpanded: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onValueChange,
        label = { Text(text = textFieldLabel) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconToggleButton(
                checked = isExpanded,
                onCheckedChange = onCheckedChange
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ArrowDropUp
                    else Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand"
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BaseExposedDropDownMenuPreview() {
    BaseExposedDropDownMenu(
        optionsState = MutableStateFlow(listOf("Option 1", "Option 2", "Option 3")),
        selectedOptionState = MutableStateFlow("Option 2"),
        textFieldValue = { it ?: "" },
        textFieldLabel = "Label",
        optionsLabel = { it },
        onOptionSelect = { },
    )
}

enum class DropDownMenuTextFieldType {
    Outlined,
    Normal
}
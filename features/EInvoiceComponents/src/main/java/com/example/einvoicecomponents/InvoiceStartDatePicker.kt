package com.example.einvoicecomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InvoiceStartDatePicker(
    pickedDate: Date,
    simpleDateFormatter: SimpleDateFormat,
    onDatePicked: (Date) -> Unit,
    minDate: Date,
    maxDate: Date,
    modifier: Modifier = Modifier
) {
    val dialogState = rememberMaterialDialogState()
    AssistChip(
        onClick = dialogState::show,
        label = { Text(simpleDateFormatter.format(pickedDate)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = "Delete"
            )
        },
        modifier = modifier
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DatePickerDialog(
            dialogState = dialogState,
            yearRange = minDate.toLocalDate().year..maxDate.toLocalDate().year,
            onDatePicked = onDatePicked,
            currentDate = pickedDate,
            minDate = minDate.toLocalDate()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DatePickerDialog(
    dialogState: MaterialDialogState,
    yearRange: IntRange,
    minDate: LocalDate,
    currentDate: Date = Date(),
    onDatePicked: (Date) -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("OK") {}
            negativeButton("Cancel") {}
        }
    ) {
        datepicker(
            initialDate = currentDate.toLocalDate(),
            yearRange = yearRange,
            allowedDateValidator = {
                it.isBefore(LocalDate.now().plusDays(1)) &&
                        it.isAfter(minDate)
            },
            colors = datePickerColors(),
            waitForPositiveButton = true,
        ) {
            onDatePicked(it.toDate())
        }
    }
}

@Composable
fun datePickerColors() = DatePickerDefaults.colors(
    headerBackgroundColor = MaterialTheme.colorScheme.primary,
    headerTextColor = MaterialTheme.colorScheme.onPrimary,
    calendarHeaderTextColor = MaterialTheme.colorScheme.onPrimary,
    dateActiveBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
    dateActiveTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
)

@RequiresApi(Build.VERSION_CODES.O)
fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().apply { time = this@toLocalDate }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toDate(): Date {
    val instant = atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
    return Date.from(instant)
}
package com.olio.trainsembleapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import kotlinx.datetime.LocalDate
import java.text.SimpleDateFormat
import java.util.SortedMap

interface FormField {
    val label: String
    val isValid: Boolean
    @Composable
    fun Render(modifier: Modifier = Modifier)
}

class TextFormField(
    override val label: String,
    val textValue: String,
    val onChange: (String) -> Unit,
    val validator: (String) -> Boolean = {it.isNotBlank()}
): FormField {
    override val isValid: Boolean get() = validator(textValue)

    @Composable
    override fun Render(modifier: Modifier) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onChange,
            label = { Text(label) },
            isError = !isValid,
            modifier = modifier
        )
    }
}


class TextFormFieldWithImage(
    override val label: String,
    val textValue: String,
    val onChange: (String) -> Unit,
    val validator: (String) -> Boolean = {it.isNotBlank()}
): FormField {
    override val isValid: Boolean get() = validator(textValue)

    @Composable
    override fun Render(modifier: Modifier) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onChange,
            label = { Text(label) },
            isError = !isValid,
            modifier = modifier
        )
        AsyncImage(model = textValue, contentDescription = textValue)
    }
}


class NumberFormField(
    override val label: String,
    val textValue: String,
    val onChange: (String) -> Unit,
    val validator: (String) -> Boolean = {it.isNotBlank()}
): FormField {
    override val isValid: Boolean get() = validator(textValue)

    @Composable
    override fun Render(modifier: Modifier) {
        OutlinedTextField(
            value = textValue,
            onValueChange = onChange,
            label = { Text(label) },
            isError = !isValid,
            modifier = modifier,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

class SelectInput(
    override val label: String,
    val items: Map<String, String>,
    val selection: Pair<String, String>,
    val expanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
    val onChange: (Pair<String, String>) -> Unit,
    val validator: (String) -> Boolean = {it.isNotBlank()}
): FormField {
    override val isValid: Boolean get() = validator(selection.first)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Render(modifier: Modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = modifier,
        ) {
            /*OutlinedTextField(
                value = selection.second,
                onValueChange = {  },
                label = { Text(label) },
                isError = !isValid,
                modifier = modifier.fillMaxWidth()
            )*/
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                    .fillMaxWidth(),
                readOnly = true,
                value = selection.second,
                onValueChange = {},
                label = { Text(label) },
                isError = !isValid,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {},

            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text= {
                            Text(item.value)
                        },
                        onClick = {
                            onChange(item.toPair())
                        },
                        modifier = modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}



class DateFormField(
    override val label: String,
    val dateValue: Long?,
    val onChange: (Long?) -> Unit,
    val datePickerOpen: Boolean,
    val datePickerOpenChange: (Boolean)-> Unit,
    val validator: (Long?) -> Boolean = {true}
): FormField {
    override val isValid: Boolean get() = validator(dateValue)

    @Composable
    override fun Render(modifier: Modifier) {

        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
        if (datePickerOpen) {

            DatePickerDialog(
                onDismissRequest = { datePickerOpenChange(false) },
                confirmButton = {
                    TextButton(onClick = {
                        onChange(datePickerState.selectedDateMillis)
                        datePickerOpenChange(false)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { datePickerOpenChange(false)  }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                readOnly = true,
                value = LocalDate.fromEpochDays((dateValue?:0L)/86_400_000L).toString(),
                onValueChange = {},
                label = { Text(label) },
                isError = !isValid,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {datePickerOpenChange(true) }) {
                Text("Pick")
            }
        }
    }
}






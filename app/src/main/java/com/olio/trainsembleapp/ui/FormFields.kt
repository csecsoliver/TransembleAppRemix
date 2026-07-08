package com.olio.trainsembleapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
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
            modifier = modifier.fillMaxWidth()
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
            modifier = modifier.fillMaxWidth()
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
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

class SelectInput(
    override val label: String,
    val items: SortedMap<String, String>,
    val selection: Pair<String, String>,
    val expanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
    val onChange: (Pair<String, String>) -> Unit,
    val validator: (String) -> Boolean = {it.isNotBlank()}
): FormField {
    override val isValid: Boolean get() = validator(selection.second)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Render(modifier: Modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = modifier.fillMaxWidth(),
        ) {
            /*OutlinedTextField(
                value = selection.second,
                onValueChange = {  },
                label = { Text(label) },
                isError = !isValid,
                modifier = modifier.fillMaxWidth()
            )*/
            OutlinedTextField(
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
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
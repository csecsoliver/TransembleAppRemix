package com.olio.trainsembleapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
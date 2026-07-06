package com.olio.trainsembleapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.Colors
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.olio.trainsembleapp.a_generic.EditOperationState

@Composable
fun EditDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: ()->Unit,
    onDelete: (()-> Unit)?,
    fields: List<FormField>,
    editState: EditOperationState = EditOperationState.Idle,
    onClearError: () -> Unit = {}
) {
    val isLoading = editState is EditOperationState.Loading

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card (
        ) {
            Column (
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
                for (i in fields) {
                    i.Render()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    if (onDelete != null)
                        TextButton(
                            onClick = onDelete,
                            enabled = !isLoading,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Dismiss")
                    }
                    Button(
                        onClick = onSave,
                        enabled = !isLoading
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    when (editState) {
        is EditOperationState.Loading -> {
            Dialog(onDismissRequest = {}) {


                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = Color(android.graphics.Color.LTGRAY))
                        Text(editState.message ?: "Saving...", color = Color(android.graphics.Color.LTGRAY))
                    }

            }
        }
        is EditOperationState.Error -> {
            Dialog(onDismissRequest = onClearError) {
                Card {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error: ${editState.exception.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onClearError) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
        else -> { }
    }
}

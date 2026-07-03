package com.olio.trainsembleapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EditDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: ()->Unit,
    onDelete: (()-> Unit)?,
    fields: List<FormField>
) {

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
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Dismiss")
                    }
                    Button(
                        onClick = onSave
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
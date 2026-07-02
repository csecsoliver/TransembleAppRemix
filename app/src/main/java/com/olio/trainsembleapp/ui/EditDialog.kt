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
    title: String, onDismiss: () -> Unit,
    onSave: (fields: Map<String, String>)->Unit,
    onDelete: (fields: Map<String, String>)-> Unit,
    fields: Map<String, String>
) {
    val inputStates = remember {stringToStates(fields)}

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
                for (state in inputStates) {
                    OutlinedTextField(
                        state = state.value,
                        label = {
                            Text(state.key)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(
                        onClick = { onDelete(statesToString(inputStates)) },
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
                        onClick = { onSave(statesToString(inputStates)) }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
fun statesToString(inputStates: Map<String, TextFieldState>): Map<String, String> {
    val mut = mutableMapOf<String, String>()
    for (i in inputStates) {
        if (i.key != "id")
            mut[i.key] = i.value.text.toString()
    }
    return mut.toMap()
}
fun stringToStates(fields: Map<String, String>):  Map<String, TextFieldState> {
    val mut = mutableMapOf<String, TextFieldState>()
    for (i in fields) {
        if (i.key != "id")
            mut[i.key] = TextFieldState(i.value)
    }
    return mut.toMap()
}
package com.olio.trainsembleapp.a_generic

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T, TDto>GenericReloadButtonUltraProMaxPlus(viewModel: GenericViewModel<T, TDto>) {
    Button(
        onClick = {viewModel.loadItems(true)}
        , modifier = Modifier.padding(16.dp)
    ) {
        Text("GenericReloadButtonUltraProMaxPlus")
    }
}
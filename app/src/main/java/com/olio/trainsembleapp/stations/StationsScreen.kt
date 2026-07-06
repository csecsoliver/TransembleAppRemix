package com.olio.trainsembleapp.stations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.olio.trainsembleapp.generic.GenericList
import com.olio.trainsembleapp.generic.GenericViewModel
import com.olio.trainsembleapp.generic.UiState
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.TextFormField
import com.olio.trainsembleapp.ui.TextFormFieldWithImage

@Composable
fun StationsScreen(repository: StationsRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit) {
    val viewModel = viewModel { GenericViewModel(repository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }
    when (val state = uiState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
                Text(state.message?:"Loading...")
            }
        }

        is UiState.Error -> {
            Text(
                text = "Error: ${state.exception.message}",
                modifier = Modifier.padding(innerPadding)
            )
        }

        is UiState.Success -> {

            val editDialogOpen = remember { mutableStateOf<Station?>(null) }
            if (createDialogOpen) {
                val carCreateState = remember { mutableStateOf(StationDto(
                    ""
                )) }
                EditDialog(
                    title = "Create car",
                    onDismiss = {
                        onCreateDialogClose()
                    },
                    onSave = {
                        viewModel.createItem(carCreateState.value)
                        onCreateDialogClose()
                    },
                    onDelete = null,
                    fields = listOf(
                        TextFormField(
                            "Name",
                            carCreateState.value.name,
                            {
                                carCreateState.value = carCreateState.value.copy(name = it)
                            }
                        ),
                    )
                )
            } else if (editDialogOpen.value != null) {
                val carId = remember { editDialogOpen.value!!.id!! }
                val carEditState = remember { mutableStateOf(editDialogOpen.value!!.toDto()) }
                EditDialog(carId,
                    onDismiss = {
                        editDialogOpen.value = null
                    },
                    onSave = {
                        viewModel.editItem(carId, carEditState.value)
                        editDialogOpen.value = null
                    },
                    onDelete = {
                        viewModel.deleteItem(carId)
                        editDialogOpen.value = null
                    },
                    fields = listOf(
                        TextFormField(
                            "Name",
                            carEditState.value.name,
                            {
                                carEditState.value = carEditState.value.copy(name = it)
                            }
                        ),
                    )
                )
            }


            GenericList(
                state.data,
                innerPadding,
                onSelect = {
                    editDialogOpen.value = it
                },
                getId = { it.id!!},
                headlineContent = {
                    Text(it.name)
                },
                overlineContent = {

                },
                supportingContent = {

                },
            )
        }
    }
}


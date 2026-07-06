package com.olio.trainsembleapp.stations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olio.trainsembleapp.a_generic.EditOperationState
import com.olio.trainsembleapp.a_generic.GenericList
import com.olio.trainsembleapp.a_generic.StationsViewModel
import com.olio.trainsembleapp.a_generic.UiState
import com.olio.trainsembleapp.a_generic.isError
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.TextFormField

@Composable
fun StationsScreen(repository: StationsRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit, setErrorState: (Boolean)->Unit) {
    val viewModel = viewModel { StationsViewModel(repository) }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val editState = viewModel.editState.collectAsStateWithLifecycle()
    setErrorState(uiState.value.isError)
    when (val state = uiState.value) {
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
                val operationTriggered = remember { mutableStateOf(false) }
                LaunchedEffect(editState.value) {
                    if (editState.value is EditOperationState.Loading) operationTriggered.value = true
                    if (operationTriggered.value && editState.value is EditOperationState.Success) {
                        onCreateDialogClose()
                    }
                }
                EditDialog(
                    title = "Create car",
                    onDismiss = {
                        if (editState.value !is EditOperationState.Loading) {
                            viewModel.clearEditError()
                            onCreateDialogClose()
                        }
                    },
                    onSave = {
                        viewModel.createItem(carCreateState.value)
                    },
                    onDelete = null,
                    editState = editState.value,
                    onClearError = { viewModel.clearEditError() },
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
                val operationTriggered = remember { mutableStateOf(false) }
                LaunchedEffect(editState.value) {
                    if (editState.value is EditOperationState.Loading) operationTriggered.value = true
                    if (operationTriggered.value && editState.value is EditOperationState.Success) {
                        editDialogOpen.value = null
                    }
                }
                EditDialog(carId,
                    onDismiss = {
                        if (editState.value !is EditOperationState.Loading) {
                            viewModel.clearEditError()
                            editDialogOpen.value = null
                        }
                    },
                    onSave = {
                        viewModel.editItem(carId, carEditState.value)
                    },
                    onDelete = {
                        viewModel.deleteItem(carId)
                    },
                    editState = editState.value,
                    onClearError = { viewModel.clearEditError() },
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


package com.olio.trainsembleapp.routes

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
import com.olio.trainsembleapp.a_generic.RoutesViewModel
import com.olio.trainsembleapp.a_generic.UiState
import com.olio.trainsembleapp.a_generic.isError
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.NumberFormField
import com.olio.trainsembleapp.ui.TextFormField

@Composable
fun RoutesScreen(repository: RoutesRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit, setErrorState: (Boolean)->Unit) {
    val viewModel = viewModel { RoutesViewModel(repository) }
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

            val editDialogOpen = remember { mutableStateOf<Route?>(null) }
            if (createDialogOpen) {
                val carCreateState = remember { mutableStateOf(RouteUiType(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
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
                        NumberFormField(
                            "Number",
                            carCreateState.value.number,
                            {
                                    carCreateState.value = carCreateState.value.copy(number = it)
                            }
                        ),
                        TextFormField(
                            "Type",
                            carCreateState.value.type,
                            {
                                carCreateState.value = carCreateState.value.copy(type = it)
                            }
                        ),
                        TextFormField(
                            "Nick",
                            carCreateState.value.nick?:"",
                            {
                                carCreateState.value = carCreateState.value.copy(nick = it)
                            }
                        ),
                        TextFormField(
                            "Departure",
                            carCreateState.value. departure,
                            {
                                carCreateState.value = carCreateState.value.copy(departure = it)
                            }
                        ),
                        TextFormField(
                            "Arrival",
                            carCreateState.value.arrival,
                            {
                                carCreateState.value = carCreateState.value.copy(arrival = it)
                            }
                        ),
                        TextFormField(
                            "Origin",
                            carCreateState.value.origin,
                            {
                                carCreateState.value = carCreateState.value.copy(origin = it)
                            }
                        ),
                        TextFormField(
                            "Destination",
                            carCreateState.value. destination,
                            {
                                carCreateState.value = carCreateState.value.copy(destination = it)
                            }
                        ),
                    )
                )
            } else if (editDialogOpen.value != null) {
                val carId = remember { editDialogOpen.value!!.id!! }
                val carEditState = remember { mutableStateOf(editDialogOpen.value!!.toUi())}
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
                        NumberFormField(
                            "Number",
                            carEditState.value.number,
                            {
                                    carEditState.value = carEditState.value.copy(number = it)
                            },

                        ),
                        TextFormField(
                            "Type",
                            carEditState.value.type,
                            {
                                carEditState.value = carEditState.value.copy(type = it)
                            }
                        ),
                        TextFormField(
                            "Nick",
                            carEditState.value.nick?:"",
                            {
                                carEditState.value = carEditState.value.copy(nick = it)
                            }
                        ),
                        TextFormField(
                            "Departure",
                            carEditState.value. departure,
                            {
                                carEditState.value = carEditState.value.copy(departure = it)
                            }
                        ),
                        TextFormField(
                            "Arrival",
                            carEditState.value.arrival,
                            {
                                carEditState.value = carEditState.value.copy(arrival = it)
                            }
                        ),
                        TextFormField(
                            "Origin",
                            carEditState.value.origin,
                            {
                                carEditState.value = carEditState.value.copy(origin = it)
                            }
                        ),
                        TextFormField(
                            "Destination",
                            carEditState.value. destination,
                            {
                                carEditState.value = carEditState.value.copy(destination = it)
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
                getId = { it.id ?: it.number},
                headlineContent = {
                    Text("${it.origin} - ${it.destination}")
                },
                overlineContent = {
                    Text("${it.departure} - ${it.arrival}")
                },
                supportingContent = {

                },
            )
        }
    }
}


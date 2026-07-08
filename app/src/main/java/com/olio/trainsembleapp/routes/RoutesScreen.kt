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
import com.olio.trainsembleapp.stations.Station
import com.olio.trainsembleapp.stations.StationsRepository
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.NumberFormField
import com.olio.trainsembleapp.ui.SelectInput
import com.olio.trainsembleapp.ui.TextFormField

@Composable
fun RoutesScreen(repository: RoutesRepository,stationsRepository: StationsRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit, setErrorState: (Boolean)->Unit) {
    val viewModel = viewModel { RoutesViewModel(repository, stationsRepository) }
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
                val stationMap = remember {
                    val asd = (viewModel.stationsUiState.value as UiState.Success<Station>)
                    asd.data.associate {
                        it.id!! to it.name
                    }.toSortedMap()
                }
                val expandedOrigin = remember { mutableStateOf(false) }
                val onExpandedChangeOrigin: (Boolean) -> Unit = {
                    expandedOrigin.value = it
                }
                val expandedDestination = remember { mutableStateOf(false) }
                val onExpandedChangeDestination: (Boolean) -> Unit = {
                    expandedDestination.value = it
                }
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
                        onExpandedChangeOrigin(false)
                        onExpandedChangeDestination(false)
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
                        SelectInput(
                            "Origin",
                            selection = (
                                    carCreateState.value.origin
                                            to
                                            (stationMap[carCreateState.value.origin]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeOrigin,
                            expanded = expandedOrigin.value,
                            onChange = {
                                carCreateState.value = carCreateState.value.copy(origin = it.first)
                                onExpandedChangeOrigin(false)
                            }
                        ),
                        TextFormField(
                            "Origin",
                            carCreateState.value.origin,
                            {
                                carCreateState.value = carCreateState.value.copy(origin = it)
                            }
                        ),
                        SelectInput(
                            "Destination",
                            selection = (
                                    carCreateState.value.destination
                                            to
                                            (stationMap[carCreateState.value.destination]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeDestination,
                            expanded = expandedDestination.value,
                            onChange = {
                                carCreateState.value = carCreateState.value.copy(destination = it.first)
                                onExpandedChangeDestination(false)
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
                val stationMap = remember {
                    val asd = (viewModel.stationsUiState.value as UiState.Success<Station>)
                    asd.data.associate {
                        it.id!! to it.name
                    }.toSortedMap()
                }
                val expandedOrigin = remember { mutableStateOf(false) }
                val onExpandedChangeOrigin: (Boolean) -> Unit = {
                    expandedOrigin.value = it
                }
                val expandedDestination = remember { mutableStateOf(false) }
                val onExpandedChangeDestination: (Boolean) -> Unit = {
                    expandedDestination.value = it
                }
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
                        onExpandedChangeOrigin(false)
                        onExpandedChangeDestination(false)
                    },
                    onDelete = {
                        viewModel.deleteItem(carId)
                        onExpandedChangeOrigin(false)
                        onExpandedChangeDestination(false)
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
                        SelectInput(
                            "Origin",
                            selection = (
                                    carEditState.value.origin
                                            to
                                            (stationMap[carEditState.value.origin]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeOrigin,
                            expanded = expandedOrigin.value,
                            onChange = {
                                carEditState.value = carEditState.value.copy(origin = it.first)
                                onExpandedChangeOrigin(false)
                            }
                        ),
                        TextFormField(
                            "Origin",
                            carEditState.value.origin,
                            {
                                carEditState.value = carEditState.value.copy(origin = it)
                            }
                        ),
                        SelectInput(
                            "Destination",
                            selection = (
                                    carEditState.value.destination
                                            to
                                            (stationMap[carEditState.value.destination]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeDestination,
                            expanded = expandedDestination.value,
                            onChange = {
                                carEditState.value = carEditState.value.copy(destination = it.first)
                                onExpandedChangeDestination(false)
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
                    Text("${it.expand!!["origin"]!!.name} - ${it.expand!!["destination"]!!.name}")
                },
                overlineContent = {
                    Text("${it.departure} - ${it.arrival}")
                },
                supportingContent = {
                    val asd = if (it.nick != null && it.nick != "") {
                        "(${it.nick} ${it.type} ${it.number})"
                    } else {
                        "(${it.type} ${it.number})"
                    }
                    Text(asd)
                },
            )
        }
    }
}


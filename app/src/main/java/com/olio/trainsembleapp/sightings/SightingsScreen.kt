package com.olio.trainsembleapp.sightings

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.olio.trainsembleapp.a_generic.EditOperationState
import com.olio.trainsembleapp.a_generic.GenericList
import com.olio.trainsembleapp.a_generic.SightingsViewModel
import com.olio.trainsembleapp.a_generic.UiState
import com.olio.trainsembleapp.a_generic.isError
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.routes.Route
import com.olio.trainsembleapp.routes.RoutesRepository
import com.olio.trainsembleapp.stations.Sighting
import com.olio.trainsembleapp.stations.SightingDto
import com.olio.trainsembleapp.stations.Station
import com.olio.trainsembleapp.stations.StationsRepository
import com.olio.trainsembleapp.ui.DateFormField
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.FormField
import com.olio.trainsembleapp.ui.NumberFormField
import com.olio.trainsembleapp.ui.SelectInput
import com.olio.trainsembleapp.ui.TextFormField
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.collections.associate

@Composable
fun SightingsScreen(repository: SightingsRepository, routesRepository: RoutesRepository, stationsRepository: StationsRepository, carsRepository: CarsRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit, setErrorState: (Boolean)->Unit) {
    val viewModel = viewModel {
        SightingsViewModel(
            repository,
            routesRepository,
            stationsRepository,
            carsRepository
        )
    }
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

            val editDialogOpen = remember { mutableStateOf<Sighting?>(null) }
            if (createDialogOpen) {
                val sightingCreateState = remember { mutableStateOf(SightingDto(
                    "",
                    null,
                    "",
                    emptyList(),
                    "",
                )) }
                val imagesState = remember { mutableStateOf<List<Uri>>( emptyList() ) }
                val operationTriggered = remember { mutableStateOf(false) }

                val routeMap = remember {
                    val asd = (viewModel.routesUiState.value as UiState.Success<Route>)
                    asd.data.sortedBy { "${it.nick?:""}${it.type}${it.number}" }.associate {
                        it.id!! to "${it.nick?:""} ${it.type} ${it.number} ${it.expand!!["origin"]!!.name} - ${it.expand!!["destination"]!!.name}"
                    }
                }
                val expandedRoute = remember { mutableStateOf(false) }
                val onExpandedChangeRoute: (Boolean) -> Unit = {
                    expandedRoute.value = it
                }


                val stationMap = remember {
                    val asd = (viewModel.stationsUiState.value as UiState.Success<Station>)
                    asd.data.sortedBy { it.name }.associate {
                        it.id!! to it.name
                    }
                }
                val expandedStation = remember { mutableStateOf(false) }
                val onExpandedChangeStation: (Boolean) -> Unit = {
                    expandedStation.value = it
                }


                val carMap = remember {
                    val asd = (viewModel.carsUiState.value as UiState.Success<Car>)
                    asd.data.sortedBy { (it.nick?:"")+it.agency+it.type+it.uic }.associate {
                        it.id!! to (it.nick?:"")+it.agency+it.type+it.uic
                    }
                }
                data class TrainCar(
                    val id: String = java.util.UUID.randomUUID().toString(),
                    val type: String
                )

                val trainComposition = remember { mutableStateListOf<TrainCar>() }
                val onAddCar: (String)->Unit = {
                    trainComposition.add(TrainCar(type=it))
                }
                val onCarRemove: (String)-> Unit ={ id ->
                    val index = trainComposition.indexOfFirst { it.id ==  id}
                    trainComposition.removeAt(index)
                }

                val expandedCar = remember { mutableStateOf<Boolean>(false) }
                val onExpandedChangeCar: (Boolean) -> Unit = {
                    expandedCar.value = it
                }

                val openDate = remember { mutableStateOf(false) }
                val onOpenDateChange: (Boolean) -> Unit = {
                    openDate.value = it
                }
                val onMove: (Int, Int)->Unit = {from, to ->
                    trainComposition.add(to, trainComposition.removeAt(from))
                }
                val listState = rememberLazyListState()
                val reorderableState = rememberReorderableLazyListState(
                    listState = listState,
                    onMove = { from, to -> onMove(from.index, to.index) }
                )


                LaunchedEffect(editState.value) {
                    if (editState.value is EditOperationState.Loading) operationTriggered.value = true
                    if (operationTriggered.value && editState.value is EditOperationState.Success) {
                        onCreateDialogClose()
                    }
                }
                EditDialog(
                    title = "Record Sighting",
                    onDismiss = {
                        if (editState.value !is EditOperationState.Loading) {
                            viewModel.clearEditError()
                            onCreateDialogClose()
                        }
                    },
                    onSave = {
                        viewModel.createItem(sightingCreateState.value)
                        onExpandedChangeRoute(false)
                        onExpandedChangeStation(false)
                    },
                    onDelete = null,
                    editState = editState.value,
                    onClearError = { viewModel.clearEditError() },
                    fields = listOf(
                        SelectInput(
                            "Route",
                            selection = (
                                    sightingCreateState.value.route
                                            to
                                            (routeMap[sightingCreateState.value.route]?:
                                            "No such route")) ,
                            items = routeMap,
                            onExpandedChange = onExpandedChangeRoute,
                            expanded = expandedRoute.value,
                            onChange = {
                                sightingCreateState.value = sightingCreateState.value.copy(route = it.first)
                                onExpandedChangeRoute(false)
                            },
                            validator = {routeMap[it] != null}
                        ),
                        DateFormField(
                            "Date",
                            dateValue = sightingCreateState.value.date,
                            onChange = {
                                sightingCreateState.value = sightingCreateState.value.copy(date = it)
                            },
                            datePickerOpen = openDate.value,
                            datePickerOpenChange = onOpenDateChange,

                        ),

                        SelectInput(
                            "Destination",
                            selection = (
                                    sightingCreateState.value.station
                                            to
                                            (stationMap[sightingCreateState.value.station]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeStation,
                            expanded = expandedStation.value,
                            onChange = {
                                sightingCreateState.value = sightingCreateState.value.copy(station = it.first)
                                onExpandedChangeStation(false)
                            },
                            validator = {stationMap[it] != null}
                        ),

                        SelectInput(
                            "Add car",
                            selection = ("" to "Select Car"),
                            items = carMap,
                            onExpandedChange = onExpandedChangeCar,
                            expanded = expandedStation.value,
                            onChange = {
                                onAddCar(it.first)
                                onExpandedChangeStation(false)
                            },
                            validator = {true}
                        ),
                        object: FormField {
                            override val label = "Cars"
                            @Composable
                            override fun Render(modifier: Modifier) {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.reorderable(reorderableState)
                                ) {
                                    items(
                                        items = trainComposition,
                                        key = { car -> car.id }
                                    ) { car ->
                                        ReorderableItem(
                                            reorderableState = reorderableState,
                                            key = car.id
                                        ) { isDragging ->

                                            Card(
                                                elevation = CardDefaults.cardElevation(if (isDragging) 8.dp else 0.dp),
                                                modifier = Modifier.detectReorderAfterLongPress(reorderableState)
                                            ) {

                                            }
                                        }
                                    }
                                }
                            }
                            override val isValid = true
                        },

                        TextFormField(
                            "Notes",
                            sightingCreateState.value.notes?:"",
                            {
                                sightingCreateState.value = sightingCreateState.value.copy(notes = it)
                            },
                            validator = {true}
                        )
                    )
                )
            } else if (editDialogOpen.value != null) {
                val carId = remember { editDialogOpen.value!!.id!! }
                val carEditState = remember { mutableStateOf(editDialogOpen.value!!.toUi())}
                val operationTriggered = remember { mutableStateOf(false) }
                val stationMap = remember {
                    val asd = (viewModel.stationsUiState.value as UiState.Success<Station>)
                    asd.data.sortedBy { it.name }.associate {
                        it.id!! to it.name
                    }
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
                            },
                            validator = {true}
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
                            },
                            validator = {stationMap[it] != null}
                        ),
                        TextFormField(
                            "Origin",
                            carEditState.value.origin,
                            {
                                carEditState.value = carEditState.value.copy(origin = it)
                            },
                            validator = {stationMap[it] != null}
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
                            },
                            validator = {stationMap[it] != null}
                        ),
                        TextFormField(
                            "Destination",
                            carEditState.value. destination,
                            {
                                carEditState.value = carEditState.value.copy(destination = it)
                            },
                            validator = {stationMap[it] != null}
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


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
import com.olio.trainsembleapp.stations.toDto
import com.olio.trainsembleapp.ui.DateFormField
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.FormField
import com.olio.trainsembleapp.ui.NumberFormField
import com.olio.trainsembleapp.ui.SelectInput
import com.olio.trainsembleapp.ui.TextFormField
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
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

            val routeMap = remember {
                val asd = (viewModel.routesUiState.value as UiState.Success<Route>)
                asd.data.sortedBy { "${it.nick?:""}${it.type}${it.number}" }.associate {
                    it.id!! to "${it.nick?:""} ${it.type} ${it.number} ${it.expand!!["origin"]!!.name} - ${it.expand!!["destination"]!!.name}"
                }
            }
            val stationMap = remember {
                val asd = (viewModel.stationsUiState.value as UiState.Success<Station>)
                asd.data.sortedBy { it.name }.associate {
                    it.id!! to it.name
                }
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

                val expandedRoute = remember { mutableStateOf(false) }
                val onExpandedChangeRoute: (Boolean) -> Unit = {
                    expandedRoute.value = it
                }

                val expandedStation = remember { mutableStateOf(false) }
                val onExpandedChangeStation: (Boolean) -> Unit = {
                    expandedStation.value = it
                }



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
                    lazyListState = listState,
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

                        sightingCreateState.value = sightingCreateState.value.copy(cars = trainComposition.map { it.id })
                        viewModel.createItem(sightingCreateState.value)
                        onExpandedChangeRoute(false)
                        onExpandedChangeStation(false)
                        onExpandedChangeCar(false)
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
                            expanded = expandedCar.value,
                            onChange = {
                                onAddCar(it.first)
                                onExpandedChangeCar(false)
                            },
                            validator = {true}
                        ),
                        object: FormField {
                            override val label = "Cars"
                            @Composable
                            override fun Render(modifier: Modifier) {
                                LazyColumn(
                                    state = listState
                                ) {
                                    items(
                                        items = trainComposition,
                                        key = { car -> car.id }
                                    ) { car ->
                                        ReorderableItem(
                                            reorderableState,
                                            key = car.id
                                        ) { isDragging ->

                                            Card(
                                                elevation = CardDefaults.cardElevation(if (isDragging) 8.dp else 0.dp),
                                                modifier = Modifier.longPressDraggableHandle()
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
            }
            else if (editDialogOpen.value != null) {
                val sightingId = remember { editDialogOpen.value!!.id!! }
                val sightingEditState = remember { mutableStateOf(editDialogOpen.value!!.toDto())}
                val operationTriggered = remember { mutableStateOf(false) }

                val expandedRoute = remember { mutableStateOf(false) }
                val onExpandedChangeRoute: (Boolean) -> Unit = {
                    expandedRoute.value = it
                }

                val expandedStation = remember { mutableStateOf(false) }
                val onExpandedChangeStation: (Boolean) -> Unit = {
                    expandedStation.value = it
                }


                val trainComposition = remember { mutableStateListOf<TrainCar>().apply {
                    editDialogOpen.value!!.cars.forEach { add(TrainCar(type = it)) }
                }}
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
                    lazyListState = listState,
                    onMove = { from, to -> onMove(from.index, to.index) }
                )

                LaunchedEffect(editState.value) {
                    if (editState.value is EditOperationState.Loading) operationTriggered.value = true
                    if (operationTriggered.value && editState.value is EditOperationState.Success) {
                        editDialogOpen.value = null
                    }
                }
                EditDialog(sightingId,
                    onDismiss = {
                        if (editState.value !is EditOperationState.Loading) {
                            viewModel.clearEditError()
                            editDialogOpen.value = null
                        }
                    },
                    onSave = {
                        viewModel.editItem(sightingId, sightingEditState.value)
                        onExpandedChangeRoute(false)
                        onExpandedChangeStation(false)
                    },
                    onDelete = {
                        viewModel.deleteItem(sightingId)
                        onExpandedChangeRoute(false)
                        onExpandedChangeStation(false)
                    },
                    editState = editState.value,
                    onClearError = { viewModel.clearEditError() },
                    fields = listOf(
                        SelectInput(
                            "Route",
                            selection = (
                                    sightingEditState.value.route
                                            to
                                            (routeMap[sightingEditState.value.route]?:
                                            "No such route")) ,
                            items = routeMap,
                            onExpandedChange = onExpandedChangeRoute,
                            expanded = expandedRoute.value,
                            onChange = {
                                sightingEditState.value = sightingEditState.value.copy(route = it.first)
                                onExpandedChangeRoute(false)
                            },
                            validator = {routeMap[it] != null}
                        ),
                        DateFormField(
                            "Date",
                            dateValue = sightingEditState.value.date,
                            onChange = {
                                sightingEditState.value = sightingEditState.value.copy(date = it)
                            },
                            datePickerOpen = openDate.value,
                            datePickerOpenChange = onOpenDateChange,

                        ),
                        SelectInput(
                            "Destination",
                            selection = (
                                    sightingEditState.value.station
                                            to
                                            (stationMap[sightingEditState.value.station]?:
                                            "No such station")) ,
                            items = stationMap,
                            onExpandedChange = onExpandedChangeStation,
                            expanded = expandedStation.value,
                            onChange = {
                                sightingEditState.value = sightingEditState.value.copy(station = it.first)
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
                                    state = listState
                                ) {
                                    items(
                                        items = trainComposition,
                                        key = { car -> car.id }
                                    ) { car ->
                                        ReorderableItem(
                                            reorderableState,
                                            key = car.id
                                        ) { isDragging ->

                                            Card(
                                                elevation = CardDefaults.cardElevation(if (isDragging) 8.dp else 0.dp),
                                                modifier = Modifier.longPressDraggableHandle()
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
                            sightingEditState.value.notes?:"",
                            {
                                sightingEditState.value = sightingEditState.value.copy(notes = it)
                            },
                            validator = {true}
                        )
                    )
                )
            }


            GenericList(
                state.data,
                innerPadding,
                onSelect = {
                    editDialogOpen.value = it
                },
                getId = { it.id!! },
                headlineContent = {
                    Text("${it.date} along ${routeMap[it.route]}")
                },
                overlineContent = {
                    Text(it.station)
                },
                supportingContent = {
                     if (it.notes != null && it.notes != "") {
                        Text(it.notes)
                    }

                },
            )
        }
    }
}


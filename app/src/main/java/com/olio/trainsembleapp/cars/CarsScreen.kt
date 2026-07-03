package com.olio.trainsembleapp.cars

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
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.FormField
import com.olio.trainsembleapp.ui.TextFormField

@Composable
fun CarsScreen(repository: CarsRepository, innerPadding: PaddingValues) {
    val viewModel = viewModel { CarsViewModel(repository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadCars()
    }
    when (val state = uiState) {
        is CarsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
                Text(state.message?:"Loading...")
            }
        }

        is CarsUiState.Error -> {
            Text(
                text = "Error: ${state.exception.message}",
                modifier = Modifier.padding(innerPadding)
            )
        }

        is CarsUiState.Success -> {

            val dialogOpen = remember { mutableStateOf<Car?>(null) }
            if (dialogOpen.value != null) {
                val carId = remember { dialogOpen.value!!.id!! }
                val carEditState = remember { mutableStateOf<CarDto>(dialogOpen.value!!.toDto()) }
                EditDialog(carId,
                    onDismiss = {
                        dialogOpen.value = null
                    },
                    onSave = {

                        viewModel.editCar(carId, carEditState.value)
                    },
                    onDelete = {
                        viewModel.deleteCar(carId)
                    },
                    fields = listOf(
                        TextFormField(
                            "UIC",
                            carEditState.value.uic,
                            {
                                carEditState.value = carEditState.value.copy(uic = it)
                            }
                        ),
                        TextFormField(
                            "Agency",
                            carEditState.value.agency,
                            {
                                carEditState.value = carEditState.value.copy(agency = it)
                            }
                        ),
                        TextFormField(
                            "Type",
                            carEditState.value.type,
                            {
                                carEditState.value = carEditState.value.copy(type = it)
                            }
                        ),
                        TextFormField(
                            "Nickname",
                            carEditState.value.nick?:"",
                            {
                                carEditState.value = carEditState.value.copy(nick = it)
                            }
                        ),
                        TextFormField(
                            "vagonweb",
                            carEditState.value.vagonweb,
                            {
                                carEditState.value = carEditState.value.copy(vagonweb = it)
                            }
                        ),
                    )
                )
            }


            CarList(
                state.data,
                innerPadding,
                onSelect = {
                    dialogOpen.value = it
                }
            )
        }
    }
}

@Composable
fun CarList(
    cars: List<Car>, innerPadding: PaddingValues,
    onSelect: (Car) -> Unit,
    ) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
    ) {

        items(
            items = cars,
            key = {car ->
                car.id ?: car.uic
            }
        ) { car ->
            ListItem(
                headlineContent = {
                    Text(car.uic)
                },
                overlineContent = {
                    Text("${  car.type } ${car.nick}" )
                },
                supportingContent = {
                    AsyncImage(model = car.vagonweb, contentDescription = car.vagonweb)
                },
                modifier = Modifier.clickable {
                    onSelect(car)
                }


            )

        }
    }
}
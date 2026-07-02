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
            CarList(viewModel, state.data, innerPadding)
        }
    }
}

@Composable
fun CarList(viewModel: CarsViewModel, cars: List<Car>, innerPadding: PaddingValues) {
    val dialogOpen = remember { mutableStateOf<Car?>(null) }
    if (dialogOpen.value != null) {
        val car = dialogOpen.value!!
        EditDialog(dialogOpen.value!!.id!!,
            onDismiss = {
                dialogOpen.value = null
            },
            onSave = { fields ->
                val entry = CarDto(
                    uic = fields["uic"]!!,
                    agency = fields["agency"]!!,
                    type = fields["type"]!!,
                    nick = fields["nick"]!!,
                    vagonweb = fields["vagonweb"]!!,
                )
                viewModel.editCar(car.id!!, entry)
            },
            onDelete = { fields ->
                viewModel.deleteCar(car.id!!)
            },
            fields = mapOf(
                Pair("id", car.id!!),
                Pair("uic", car.uic),
                Pair("agency", car.agency),
                Pair("type", car.type),
                Pair("nick", car.nick?:""),
                Pair("vagonweb", car.vagonweb)
            ),
        )
    }
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
                    dialogOpen.value = car
                }


            )

        }
    }
}
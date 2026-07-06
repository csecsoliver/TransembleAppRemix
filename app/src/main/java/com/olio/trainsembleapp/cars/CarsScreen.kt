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
import com.olio.trainsembleapp.generic.CarsViewModel
import com.olio.trainsembleapp.generic.GenericList
import com.olio.trainsembleapp.generic.UiState
import com.olio.trainsembleapp.ui.EditDialog
import com.olio.trainsembleapp.ui.TextFormField
import com.olio.trainsembleapp.ui.TextFormFieldWithImage

@Composable
fun CarsScreen(repository: CarsRepository, innerPadding: PaddingValues, createDialogOpen: Boolean, onCreateDialogClose: ()->Unit) {
    val viewModel = viewModel { CarsViewModel(repository) }
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

            val editDialogOpen = remember { mutableStateOf<Car?>(null) }
            if (createDialogOpen) {
                val carCreateState = remember { mutableStateOf(CarDto(
                    "",
                    "",
                    "",
                    "",
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
                            "UIC",
                            carCreateState.value.uic,
                            {
                                carCreateState.value = carCreateState.value.copy(uic = it)
                            }
                        ),
                        TextFormField(
                            "Agency",
                            carCreateState.value.agency,
                            {
                                carCreateState.value = carCreateState.value.copy(agency = it)
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
                            "Nickname",
                            carCreateState.value.nick?:"",
                            {
                                carCreateState.value = carCreateState.value.copy(nick = it)
                            }
                        ),
                        TextFormFieldWithImage(
                            "vagonweb",
                            carCreateState.value.vagonweb,
                            {
                                carCreateState.value = carCreateState.value.copy(vagonweb = it)
                            }
                        ),
                    )
                )
            } else if (editDialogOpen.value != null) {
                val carId = remember { editDialogOpen.value!!.id!! }
                val carEditState = remember { mutableStateOf<CarDto>(editDialogOpen.value!!.toDto()) }
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
                        TextFormFieldWithImage(
                            "vagonweb",
                            carEditState.value.vagonweb,
                            {
                                carEditState.value = carEditState.value.copy(vagonweb = it)
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
                getId = { it.id?:it.uic },
                headlineContent = {
                    Text(it.uic)
                },
                overlineContent = {
                    Text("${  it.type } ${it.nick}" )
                },
                supportingContent = {
                    AsyncImage(model = it.vagonweb, contentDescription = it.vagonweb)
                },
            )
        }
    }
}


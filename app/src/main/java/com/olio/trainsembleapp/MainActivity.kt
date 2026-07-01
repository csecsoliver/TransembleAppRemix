package com.olio.trainsembleapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import coil.compose.AsyncImage
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarDto
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.cars.CarsUiState
import com.olio.trainsembleapp.cars.CarsViewModel
import com.olio.trainsembleapp.ui.ScreenTab
import com.olio.trainsembleapp.ui.screenTabs
import com.olio.trainsembleapp.ui.theme.TrainsembleAppTheme
import okhttp3.internal.toImmutableMap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TrainsembleApplication



        setContent {
            val backStack = remember { mutableStateListOf<ScreenTab>(ScreenTab.HOME) }
            val currentTab = backStack.lastOrNull() ?: ScreenTab.HOME
            TrainsembleAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar() {
                            screenTabs.forEach { entry ->
                                NavigationBarItem(
                                    selected = currentTab == entry,
                                    icon = {
                                        Icon(
                                            entry.icon,
                                            contentDescription = "Tab icon"
                                        )
                                    },
                                    label = { Text(entry.title) },
                                    onClick = {
                                        if (currentTab != entry) {
                                            backStack.clear()
                                            backStack.add(ScreenTab.HOME)
                                            if (entry != ScreenTab.HOME) {
                                                backStack.add(entry)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {

                            entry<ScreenTab.HOME> {

                                val authState by app.authRepository.isAuthenticated.collectAsStateWithLifecycle()

                                when (val state = authState) {
                                    is AuthState.Authenticating -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(
                                                innerPadding
                                            )
                                        )
                                    }

                                    is AuthState.Authenticated -> {
                                        Button(
                                            onClick = dropUnlessResumed {
                                                backStack.add(ScreenTab.CARS)
                                            },
                                            modifier = Modifier
                                                .padding(innerPadding)
                                                .padding(horizontal = 16.dp),
                                        ) {
                                            Text("Click to navigate")
                                        }
                                    }

                                    is AuthState.NotAuthenticated -> {
                                        Text(
                                            "not authenticated",
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                }

                            }

                            entry<ScreenTab.CARS> {
                                CarsScreen(
                                    repository = app.carsRepository,
                                    innerPadding = innerPadding
                                )
                            }
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun CarsScreen(repository: CarsRepository, innerPadding: PaddingValues) {
    val viewModel = viewModel { CarsViewModel(repository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.loadCars()
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
                        uic = car.uic,
                agency = car.agency,
                type = car.type,
                nick = car.nick,
                vagonweb = car.vagonweb,
                )
                viewModel.editCar(car.id!!, entry)
            },
            onDelete = { fields ->

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

@Composable
fun EditDialog(
    title: String, onDismiss: () -> Unit,
    onSave: (fields: Map<String, String>)->Unit,
    onDelete: (fields: Map<String, String>)-> Unit,
    fields: Map<String, String>
) {
    val inputStates = remember {stringToStates(fields)}

    Dialog(
        onDismissRequest = {onDismiss()},

    ) {
        Card(

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                Text(title)
                for (state in inputStates) {
                    OutlinedTextField(
                        state = state.value,
                        label = {
                            Text(state.key)
                        }
                    )
                }
                Row() {
                    Button(
                        onClick = { onDelete(statesToString(inputStates)) }
                    ) {
                        Text("Delete")
                    }
                    Row() {
                        Button(
                            onClick = { onDismiss() }
                        ) {
                            Text("Dismiss")
                        }
                        Button(
                            onClick = { onSave(statesToString(inputStates)) }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }


        }
    }
}
fun statesToString(inputStates: Map<String, TextFieldState>): Map<String, String> {
    val mut = mutableMapOf<String, String>()
    for (i in inputStates) {
        mut[i.key] = i.value.toString()
    }
    return mut.toImmutableMap()
}
fun stringToStates(fields: Map<String, String>):  Map<String, TextFieldState> {
    val mut = mutableMapOf<String, TextFieldState>()
    for (i in fields) {
        mut[i.key] = TextFieldState(i.value)
    }
    return mut.toImmutableMap()
}
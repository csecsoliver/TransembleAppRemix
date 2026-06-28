package com.olio.trainsembleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarsRepository
import com.olio.trainsembleapp.cars.CarsUiState
import com.olio.trainsembleapp.cars.CarsViewModel
import com.olio.trainsembleapp.ui.Screen
import com.olio.trainsembleapp.ui.theme.TrainsembleAppTheme

private data object RouteA

private data class RouteB(val id: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TrainsembleApplication



        setContent {
            val backStack = remember { mutableStateListOf<Screen>(Screen.HOME) }
            TrainsembleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {

                            entry<Screen.HOME> {

                                    val authState by app.authRepository.isAuthenticated.collectAsStateWithLifecycle()

                                    when (val state = authState)  {
                                        is AuthState.Authenticating -> {
                                            CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
                                        }
                                        is AuthState.Authenticated -> {
                                            Button(
                                                onClick = dropUnlessResumed {
                                                    backStack.add(Screen.CARS)
                                                },
                                                modifier = Modifier
                                                    .padding(innerPadding)
                                                    .padding(horizontal = 16.dp),
                                            ) {
                                                Text("Click to navigate")
                                            }
                                        }
                                        is AuthState.NotAuthenticated -> {
                                            Text("not authenticated", modifier = Modifier.padding(innerPadding))
                                        }
                                    }

                            }

                            // 3. Register RouteB. You get the typed 'route' object for free!
                            entry<Screen.CARS> { route ->
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
            CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
        }
        is CarsUiState.Error -> {
            Text(text = "Error: ${state.exception.message}", modifier = Modifier.padding(innerPadding))
        }
        is CarsUiState.Success -> {
            CarList(cars = state.data, innerPadding)
        }
    }
}

@Composable
fun CarList(cars: List<Car>, innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
    ) {
        items(items = cars) { car ->
            Text(text = car.uic, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
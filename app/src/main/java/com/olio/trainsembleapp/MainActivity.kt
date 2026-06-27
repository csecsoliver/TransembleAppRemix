package com.olio.trainsembleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.olio.trainsembleapp.ui.theme.TrainsembleAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.olio.trainsembleapp.cars.Car
import com.olio.trainsembleapp.cars.CarsViewModel

private data object RouteA

private data class RouteB(val id: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TrainsembleApplication



        setContent {
            val backStack = remember { mutableStateListOf<Any>(RouteA) }
            TrainsembleAppTheme {
                
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is RouteA -> NavEntry(key) {
                                Scaffold( modifier = Modifier.fillMaxSize()) { innerPadding ->
                                    Button(
                                        onClick = dropUnlessResumed {
                                            backStack.add(RouteB("123"))
                                        },
                                        modifier = Modifier
                                            .padding(innerPadding)
                                            .padding(horizontal = 16.dp),
                                    ) {
                                        Text("Click to navigate")
                                    }
                                }

                            }

                            is RouteB -> NavEntry(key) {
                                Scaffold( modifier = Modifier.fillMaxSize()) { innerPadding ->
                                    CarsScreen(app, innerPadding)
                                }
                            }

                            else -> {
                                error("Unknown route: $key")
                            }
                        }
                    }
                )

            }

        }
    }
}

@Composable
fun CarsScreen(app: TrainsembleApplication, innerPadding: PaddingValues) {
    val repository = app.carsRepository
    val viewModel by remember { mutableStateOf(CarsViewModel(repository)) }
    val cars = viewModel.cars.collectAsStateWithLifecycle()

    CarList(cars = cars.value, innerPadding)

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
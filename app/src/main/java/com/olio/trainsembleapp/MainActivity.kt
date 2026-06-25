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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.olio.trainsembleapp.ui.theme.TrainsembleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TrainsembleApplication
        val repository = CarsRepository(app.pbClient)
        val viewModel = CarsViewModel(repository)

        setContent {
            TrainsembleAppTheme {
                Scaffold( modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CarsScreen(viewModel, innerPadding)
                }
            }
        }
    }
}
@Composable
fun CarsScreen(viewModel: CarsViewModel, innerPadding: PaddingValues) {
    val cars = viewModel.cars.collectAsStateWithLifecycle()
    CarList(cars = cars.value, innerPadding)

}
@Composable
fun CarList(cars: List<Car>, innerPadding: PaddingValues) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp), ) {
        items(items = cars) { car ->
            Text(text = car.uic, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
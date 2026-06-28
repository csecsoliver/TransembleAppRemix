package com.olio.trainsembleapp.ui

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.olio.trainsembleapp.CarsScreen
import io.ktor.util.reflect.instanceOf

sealed class Screen {
    data object HOME : Screen()
    data object CARS : Screen()
    data object ROUTES : Screen()
    data object STATIONS : Screen()
    data object SIGHTINGS : Screen()
}
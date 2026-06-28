package com.olio.trainsembleapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Train
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
import androidx.navigation3.runtime.NavKey
import com.olio.trainsembleapp.CarsScreen
import io.ktor.util.reflect.instanceOf

sealed class Screen {
    data object HOME : Screen()
    data object CARS : Screen()
    data object ROUTES : Screen()
    data object STATIONS : Screen()
    data class SIGHTINGS(val routeId: String?) : Screen()
}
sealed interface ScreenTab : NavKey {
    val title: String
    val icon: ImageVector
    data object HOME : ScreenTab {
        override val title: String = "Home"
        override val icon = Icons.Default.Home
    }
    data object CARS : ScreenTab {
        override val title = "Cars"
        override val icon = Icons.Default.Train
    }
    data object ROUTES : ScreenTab {
        override val title = "Routes"
        override val icon = Icons.Default.Route
    }
    data object STATIONS : ScreenTab {
        override val title = "Stations"
        override val icon = Icons.Default.PinDrop
    }
    data object SIGHTINGS : ScreenTab {
        override val title = "Sightings"
        override val icon = Icons.Default.CameraAlt
    }
}
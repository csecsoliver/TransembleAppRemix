package com.olio.trainsembleapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

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
val screenTabs = listOf<ScreenTab>(ScreenTab.HOME, ScreenTab.CARS, ScreenTab.STATIONS, ScreenTab.ROUTES)
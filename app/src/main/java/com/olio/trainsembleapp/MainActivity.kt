package com.olio.trainsembleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.olio.trainsembleapp.cars.CarsScreen
import com.olio.trainsembleapp.routes.RoutesScreen
import com.olio.trainsembleapp.sightings.SightingsScreen
import com.olio.trainsembleapp.stations.StationsScreen
import com.olio.trainsembleapp.ui.ScreenTab
import com.olio.trainsembleapp.ui.screenTabs
import com.olio.trainsembleapp.ui.theme.TrainsembleAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TrainsembleApplication



        setContent {
            val backStack = remember { mutableStateListOf<ScreenTab>(ScreenTab.HOME) }
            val currentTab = backStack.lastOrNull() ?: ScreenTab.HOME
            val createDialogOpen = remember { mutableStateOf(false) }
            val errorState = remember { mutableStateOf(false) }
            TrainsembleAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if ((currentTab == ScreenTab.CARS|| currentTab == ScreenTab.STATIONS || currentTab == ScreenTab.ROUTES || currentTab == ScreenTab.SIGHTINGS) && !errorState.value ) {

                            FloatingActionButton(
                                onClick = {
                                    createDialogOpen.value = true

                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add"
                                )
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
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
                                            if (backStack.contains(entry)) {
                                                while (backStack.lastOrNull() != entry) {
                                                    backStack.removeLastOrNull()
                                                }
                                            } else {
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

                                val authState = app.authRepository.isAuthenticated.collectAsStateWithLifecycle()
                                val auth = authState.value

                                when (auth) {
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

                                is AuthState.Error -> {
                                    Column(modifier = Modifier.padding(innerPadding)) {
                                        Text("Authentication error: ${auth.reason}")
                                        Button(onClick = dropUnlessResumed { app.authRepository.retry() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                                }

                            }

                            entry<ScreenTab.CARS> {
                                CarsScreen(
                                    repository = app.carsRepository,
                                    innerPadding = innerPadding,
                                    createDialogOpen = createDialogOpen.value,
                                    onCreateDialogClose = { createDialogOpen.value = false },
                                    setErrorState = {
                                        errorState.value = it
                                    }
                                )
                            }

                            entry<ScreenTab.STATIONS> {
                                StationsScreen(
                                    repository = app.stationsRepository,
                                    innerPadding = innerPadding,
                                    createDialogOpen = createDialogOpen.value,
                                    onCreateDialogClose = { createDialogOpen.value = false },
                                    setErrorState = {
                                        errorState.value = it
                                    }
                                )
                            }
                            entry<ScreenTab.ROUTES> {
                                RoutesScreen(
                                    repository = app.routesRepository,
                                    stationsRepository = app.stationsRepository,
                                    innerPadding = innerPadding,
                                    createDialogOpen = createDialogOpen.value,
                                    onCreateDialogClose = { createDialogOpen.value = false },
                                    setErrorState = {
                                        errorState.value = it
                                    }
                                )
                            }
                            entry<ScreenTab.SIGHTINGS> {
                                SightingsScreen (
                                    repository = app.sightingsRepository,
                                    stationsRepository = app.stationsRepository,
                                    routesRepository = app.routesRepository,
                                    carsRepository = app.carsRepository,
                                    innerPadding = innerPadding,
                                    createDialogOpen = createDialogOpen.value,
                                    onCreateDialogClose = { createDialogOpen.value = false },
                                    setErrorState = {
                                        errorState.value = it
                                    }
                                )
                            }
                        }
                    )
                }
            }

        }
    }
}

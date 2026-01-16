package com.example.gpsgeofenceapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import com.example.gpsgeofenceapp.service.GeofenceService
import com.example.gpsgeofenceapp.ui.navigation.BottomBar
import com.example.gpsgeofenceapp.ui.screens.AddGeofenceScreen
import com.example.gpsgeofenceapp.ui.screens.DashboardScreen
import com.example.gpsgeofenceapp.ui.viewmodel.AddGeofenceViewModel
import com.example.gpsgeofenceapp.ui.viewmodel.DashboardViewModel

class Navigation {
}

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddGeofence : Screen("add")
}


@Composable
fun AppNav(
    repository: GeofenceRepository,
    geofenceService: GeofenceService
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Dashboard.route) {
                val vm = remember {
                    DashboardViewModel(repository, geofenceService)
                }

                DashboardScreen(
                    viewModel = vm,
                    onAddClick = {
                        navController.navigate(Screen.AddGeofence.route)
                    },
                    onItemClick = {}
                )
            }

            composable(Screen.AddGeofence.route) {
                val vm = remember {
                    AddGeofenceViewModel(repository, geofenceService)
                }

                AddGeofenceScreen(
                    viewModel = vm,
                    onSaved = {
                        navController.navigate(Screen.Dashboard.route)
                    }
                )
            }
        }
    }
}


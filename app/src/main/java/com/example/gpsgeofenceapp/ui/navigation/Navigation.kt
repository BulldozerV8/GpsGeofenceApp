package com.example.gpsgeofenceapp.ui.navigation

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
import com.example.gpsgeofenceapp.ui.screens.AddGeofenceScreen
import com.example.gpsgeofenceapp.ui.screens.DashboardScreen
import com.example.gpsgeofenceapp.ui.screens.HistoryScreen // <-- Upewnij się, że masz ten import
import com.example.gpsgeofenceapp.ui.viewmodel.AddGeofenceViewModel
import com.example.gpsgeofenceapp.ui.viewmodel.DashboardViewModel
import com.example.gpsgeofenceapp.ui.viewmodel.HistoryViewModel // <-- I ten import

/**
 * Główny graf nawigacji aplikacji (Navigation Graph).
 * Definiuje dostępne ścieżki (Routes) i mapuje je na konkretne ekrany (Composables).
 * Zarządza tworzeniem i cyklem życia obiektów ViewModel dla każdego ekranu.
 */

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

    val addVm = remember {
        AddGeofenceViewModel(repository, geofenceService)
    }

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
                        addVm.clearForm()
                        navController.navigate(Screen.AddGeofence.route)
                    },
                    onItemClick = { id ->
                        addVm.loadGeofence(id)
                        navController.navigate(Screen.AddGeofence.route)
                    }
                )
            }

            composable(Screen.AddGeofence.route) {
                AddGeofenceScreen(
                    viewModel = addVm,
                    onSaved = {
                        navController.navigate(Screen.Dashboard.route)
                    }
                )
            }

            composable(BottomNavItem.History.route) {
                val historyVm = remember {
                    HistoryViewModel(repository)
                }

                HistoryScreen(viewModel = historyVm)
            }
        }
    }
}
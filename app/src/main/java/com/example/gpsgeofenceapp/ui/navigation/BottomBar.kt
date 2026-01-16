package com.example.gpsgeofenceapp.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Add
    )

    NavigationBar {
        val currentRoute =
            navController.currentBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, null) },
                label = { Text(item.label) }
            )
        }
    }
}

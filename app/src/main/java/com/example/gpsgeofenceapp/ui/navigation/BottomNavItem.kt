package com.example.gpsgeofenceapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Przechowuje konfigurację każdego ekranu:
 * - unikalną ścieżkę (route)
 * - etykietę widoczną dla użytkownika
 * - ikonę wektorową.
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(
        route = "dashboard",
        label = "Obszary",
        icon = Icons.Default.List
    )

    object Add : BottomNavItem(
        route = "add",
        label = "Dodaj",
        icon = Icons.Default.Add
    )

    object History : BottomNavItem(
        route = "history",
        label = "Historia",
        icon = androidx.compose.material.icons.Icons.Default.Info
    )
}

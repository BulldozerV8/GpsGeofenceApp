package com.example.gpsgeofenceapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.gpsgeofenceapp.domain.GeofenceModel
import com.example.gpsgeofenceapp.ui.viewmodel.DashboardViewModel

/**
 * Główny ekran aplikacji wyświetlający listę monitorowanych obszarów.
 * Odpowiada za:
 * - pobieranie listy geofence z ViewModel (StateFlow)
 * - renderowanie interaktywnej listy (LazyColumn)
 * - nawigację do edycji (kliknięcie) lub usuwanie elementów.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
){
    val geofences by viewModel.geofences.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // DEBUG: PRZYCISK TESTOWY (Patrz Raport: Sekcja 6 - Problemy z Emulatorem)
            // Służy do wymuszenia sprawdzenia lokalizacji
            /*
            Button(
                onClick = { viewModel.testLocation(context) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
                    .zIndex(1f)
            ) {
                Text("DEBUG: SPRAWDŹ GPS")
            }
            */
            // ===

            if (geofences.isEmpty()) {
                Text(
                    text = "Brak obszarów. Dodaj pierwszy!",
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    color = Color.Gray
                )
            } else {


                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(geofences) { geo ->
                        val scheduleInfo = formatSchedule(geo)

                        val muteStatus = if (geo.shouldMute) "TAK" else "NIE"

                        ListItem(
                            modifier = Modifier.clickable { onItemClick(geo.id) },

                            headlineContent = { Text(geo.name) },
                            supportingContent = {
                                Text(
                                    "Promień: ${geo.radius.toInt()} m\n" +
                                            "Godziny: ${geo.startTime} - ${geo.endTime}\n" +
                                            "Kiedy: $scheduleInfo\n" +
                                            "Wyciszanie: $muteStatus"
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.delete(geo) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Usuń",
                                        tint = Color.Red
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

fun formatSchedule(geo: GeofenceModel): String {
    if (geo.repeatDays.isNotEmpty()) {
        val daysMap = mapOf(
            1 to "Nd", 2 to "Pn", 3 to "Wt", 4 to "Śr",
            5 to "Cz", 6 to "Pt", 7 to "So"
        )
        val daysIndices = geo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }.sorted()
        return daysIndices.joinToString(", ") { daysMap[it] ?: "?" }
    }
    else if (geo.specificDate.isNotEmpty()) {
        return geo.specificDate
    }
    else {
        return "Codziennie (Zawsze)"
    }
}
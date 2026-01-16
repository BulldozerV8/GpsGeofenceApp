package com.example.gpsgeofenceapp.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gpsgeofenceapp.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
){
    val geofences by viewModel.geofences.collectAsState()
    val context = LocalContext.current

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            Column {
                // PRZYCISK TESTOWY
                Button(
                    onClick = { viewModel.testLocation(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Text(" TESTUJ POZYCJĘ (CZY JESTEM W SRODKU?)")
                }

                LazyColumn {
                    items(geofences){geo ->
                        ListItem(
                            headlineContent = { Text(geo.name) },
                            supportingContent =  {Text("Promien: ${geo.radius} m")},
                            modifier = Modifier.combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onItemClick(geo.id) },
                                onLongClick = { viewModel.delete(geo) }
                            )
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
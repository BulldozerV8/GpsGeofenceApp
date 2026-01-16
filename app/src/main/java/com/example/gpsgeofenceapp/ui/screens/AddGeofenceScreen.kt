package com.example.gpsgeofenceapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gpsgeofenceapp.ui.viewmodel.AddGeofenceViewModel
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Calendar
import java.util.Locale


/**
 * Ekran formularza dodawania i edycji obszaru (Geofence).
 * Zawiera:
 * - Mapę Google do wyboru lokalizacji i podglądu promienia.
 * - Pola tekstowe i suwaki do konfiguracji parametrów.
 * - Złożoną logikę wyboru czasu i dat (Date/Time Pickers).
 * - Sekcję opcji zaawansowanych (wyciszanie, cykliczność).
 */
@Composable
fun AddGeofenceScreen(
    viewModel: AddGeofenceViewModel,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val cameraPosition = rememberCameraPositionState()

    var isAdvancedExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        GoogleMap (
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPosition,
            onMapClick = {viewModel.selectedLatLng = it}
        ){
            viewModel.selectedLatLng?.let {
                Marker (state = MarkerState(it))
                Circle(center = it, radius = viewModel.radius.toDouble())
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = {viewModel.name = it},
                label = { Text("Nazwa obszaru") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Promień: ${viewModel.radius.toInt()} m",
                modifier = Modifier.padding(top = 8.dp)
            )
            Slider(
                value = viewModel.radius,
                onValueChange = {viewModel.radius = it},
                valueRange = 50f..500f
            )

            // Opcje zaawansowane
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { isAdvancedExpanded = !isAdvancedExpanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Opcje zaawansowane",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val statusText = if(viewModel.isAllDay && viewModel.isAlwaysActive)
                        "Aktywny: Zawsze (24h)"
                    else
                        "Aktywny: Niestandardowy"
                    Text(text = statusText, style = MaterialTheme.typography.bodySmall)
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (isAdvancedExpanded) 180f else 0f)
                )
            }

            // Czas i dni
            if (isAdvancedExpanded) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {

                        // CZAS
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = viewModel.isAllDay,
                                onCheckedChange = { viewModel.isAllDay = it }
                            )
                            Text("Cały dzień (24h)")
                        }

                        if (!viewModel.isAllDay) {
                            Row {
                                TimePickerField("Start", viewModel.startTime, context, { viewModel.startTime = it }, Modifier.weight(1f))
                                Spacer(modifier = Modifier.padding(4.dp))
                                TimePickerField("Koniec", viewModel.endTime, context, { viewModel.endTime = it }, Modifier.weight(1f))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // DNI
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = viewModel.isAlwaysActive,
                                onCheckedChange = { viewModel.isAlwaysActive = it }
                            )
                            Text("Zawsze (Każdy dzień)")
                        }

                        if (!viewModel.isAlwaysActive) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Cyklicznie (Dni tyg.)", modifier = Modifier.weight(1f))
                                Switch(
                                    checked = viewModel.isRepeating,
                                    onCheckedChange = { viewModel.isRepeating = it }
                                )
                            }

                            if (viewModel.isRepeating) {
                                DayOfWeekSelector(
                                    selectedDays = viewModel.selectedDays,
                                    onDayClick = { viewModel.toggleDay(it) }
                                )
                            } else {
                                DatePickerField(
                                    label = "Data aktywności",
                                    dateValue = viewModel.specificDate,
                                    context = context,
                                    onDateSelected = { viewModel.specificDate = it },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Wycisz telefon podczas wejścia",
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = viewModel.shouldMute,
                                onCheckedChange = { viewModel.shouldMute = it }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.save(); onSaved() },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Zapisz")
            }
        }
    }
}

@Composable
fun TimePickerField(
    label: String,
    timeValue: String,
    context: Context,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = timeValue,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            showTimePicker(context, timeValue) { newTime ->
                onTimeSelected(newTime)
            }
        },
        enabled = false,
        textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
    )
}

fun showTimePicker(context: Context, currentTime: String, onTimePicked: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    try {
        val parts = currentTime.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        calendar.set(Calendar.MINUTE, parts[1].toInt())
    } catch (e: Exception) {
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            onTimePicked(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    timePickerDialog.show()
}

@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    context: Context,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = dateValue,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            showDatePicker(context, dateValue) { newDate ->
                onDateSelected(newDate)
            }
        },
        enabled = false,
        textStyle = androidx.compose.ui.text.TextStyle(color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
    )
}

fun showDatePicker(context: Context, currentDate: String, onDatePicked: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    try {
        val parts = currentDate.split("-")
        calendar.set(Calendar.YEAR, parts[0].toInt())
        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
    } catch (e: Exception) { }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate =
                String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onDatePicked(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

@Composable
fun DayOfWeekSelector(
    selectedDays: List<Int>,
    onDayClick: (Int) -> Unit
) {
    val daysOrder = listOf(2, 3, 4, 5, 6, 7, 1)
    val labels = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")

    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOrder.forEachIndexed { index, dayId ->
            val isSelected = selectedDays.contains(dayId)
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bgColor)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onDayClick(dayId) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = labels[index], color = textColor, fontSize = 12.sp)
            }
        }
    }
}
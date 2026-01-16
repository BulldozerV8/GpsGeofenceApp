package com.example.gpsgeofenceapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.example.gpsgeofenceapp.ui.viewmodel.AddGeofenceViewModel
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddGeofenceScreen(
    viewModel: AddGeofenceViewModel,
    onSaved: () -> Unit
) {

    val cameraPosition = rememberCameraPositionState()

    Column {
        GoogleMap (
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPosition,
            onMapClick = {viewModel.selectedLatLng = it}
        ){
            viewModel.selectedLatLng?.let {
                Marker (state = MarkerState(it))
                Circle(
                    center = it,
                    radius = viewModel.radius.toDouble()
                )
            }
        }

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = {viewModel.name = it},
            label = { Text("Nazwa obszaru") }
        )

        Slider(
            value = viewModel.radius,
            onValueChange = {viewModel.radius = it},
            valueRange = 50f..500f
        )

        Button(onClick = {
            viewModel.save()
            onSaved()
        }) {
            Text("Zapisz")
        }


    }
}
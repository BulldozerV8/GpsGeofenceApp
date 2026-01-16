package com.example.gpsgeofenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpsgeofenceapp.data.repository.GeofenceRepository
import kotlinx.coroutines.launch
/**
 * ViewModel dla ekranu historii.
 * Implementacja przekazująca strumień logów z repozytorium do UI.
 * Umożliwia wywołanie akcji czyszczenia historii.
 */
class HistoryViewModel(private val repository: GeofenceRepository) : ViewModel() {

    val logs = repository.getLogs()

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}
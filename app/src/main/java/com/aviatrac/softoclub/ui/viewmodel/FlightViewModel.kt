package com.aviatrac.softoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviatrac.softoclub.data.model.*
import com.aviatrac.softoclub.data.repository.FlightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FlightViewModel(private val repository: FlightRepository) : ViewModel() {
    
    val allFlights = repository.allFlights.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    
    val totalFlights = repository.totalFlights.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )
    
    val totalFlightHours = allFlights.map { flights ->
        val totalMinutes = flights.sumOf { it.durationHours * 60 + it.durationMinutes }
        Pair(totalMinutes / 60, totalMinutes % 60)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Pair(0, 0)
    )
    
    val lastFlight = allFlights.map { flights ->
        flights.firstOrNull()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    
    val routes = repository.routes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    
    val airports = repository.airports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    
    val aircraft = repository.aircraft.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    
    private val _selectedFlight = MutableStateFlow<Flight?>(null)
    val selectedFlight: StateFlow<Flight?> = _selectedFlight.asStateFlow()
    
    fun insertFlight(flight: Flight) {
        viewModelScope.launch {
            repository.insertFlight(flight)
        }
    }
    
    fun updateFlight(flight: Flight) {
        viewModelScope.launch {
            repository.updateFlight(flight)
        }
    }
    
    fun deleteFlight(flight: Flight) {
        viewModelScope.launch {
            repository.deleteFlight(flight)
        }
    }
    
    fun loadFlight(id: Long) {
        viewModelScope.launch {
            _selectedFlight.value = repository.getFlightById(id)
        }
    }
    
    fun clearSelectedFlight() {
        _selectedFlight.value = null
    }
    
    fun deleteAllFlights() {
        viewModelScope.launch {
            repository.deleteAllFlights()
        }
    }
    
    fun getFlightsByRoute(from: String, to: String): Flow<List<Flight>> {
        return repository.getFlightsByRoute(from, to)
    }
    
    fun getFlightsPerMonth(): StateFlow<Map<String, Int>> {
        return allFlights.map { flights ->
            val calendar = Calendar.getInstance()
            flights.groupBy { flight ->
                calendar.timeInMillis = flight.date
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
            }.mapValues { it.value.size }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )
    }
    
    fun getHoursPerMonth(): StateFlow<Map<String, Int>> {
        return allFlights.map { flights ->
            val calendar = Calendar.getInstance()
            flights.groupBy { flight ->
                calendar.timeInMillis = flight.date
                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
            }.mapValues { (_, monthFlights) ->
                monthFlights.sumOf { it.durationHours * 60 + it.durationMinutes } / 60
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )
    }
    
    fun getLongestFlight(): StateFlow<Flight?> {
        return allFlights.map { flights ->
            flights.maxByOrNull { it.durationHours * 60 + it.durationMinutes }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }
    
    fun getAverageDuration(): StateFlow<Pair<Int, Int>> {
        return allFlights.map { flights ->
            if (flights.isEmpty()) {
                Pair(0, 0)
            } else {
                val totalMinutes = flights.sumOf { it.durationHours * 60 + it.durationMinutes }
                val avgMinutes = totalMinutes / flights.size
                Pair(avgMinutes / 60, avgMinutes % 60)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Pair(0, 0)
        )
    }
    
    fun getFlightsByMonth(month: Int, year: Int): StateFlow<List<Flight>> {
        return allFlights.map { flights ->
            val calendar = Calendar.getInstance()
            flights.filter { flight ->
                calendar.timeInMillis = flight.date
                calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }
}


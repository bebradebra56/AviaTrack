package com.aviatrac.softoclub.data.repository

import com.aviatrac.softoclub.data.database.FlightDao
import com.aviatrac.softoclub.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlightRepository(private val flightDao: FlightDao) {
    
    val allFlights: Flow<List<Flight>> = flightDao.getAllFlights()
    
    val totalFlights: Flow<Int> = flightDao.getTotalFlights()
    
    suspend fun getFlightById(id: Long): Flight? {
        return flightDao.getFlightById(id)
    }
    
    suspend fun insertFlight(flight: Flight): Long {
        return flightDao.insertFlight(flight)
    }
    
    suspend fun updateFlight(flight: Flight) {
        flightDao.updateFlight(flight)
    }
    
    suspend fun deleteFlight(flight: Flight) {
        flightDao.deleteFlight(flight)
    }
    
    fun getFlightsByDateRange(startDate: Long, endDate: Long): Flow<List<Flight>> {
        return flightDao.getFlightsByDateRange(startDate, endDate)
    }
    
    fun getFlightsByRoute(from: String, to: String): Flow<List<Flight>> {
        return flightDao.getFlightsByRoute(from, to)
    }
    
    suspend fun deleteAllFlights() {
        flightDao.deleteAllFlights()
    }
    
    val routes: Flow<List<Route>> = allFlights.map { flights ->
        flights.groupBy { "${it.fromAirport}-${it.toAirport}" }
            .map { (_, routeFlights) ->
                val totalMinutes = routeFlights.sumOf { it.durationHours * 60 + it.durationMinutes }
                Route(
                    from = routeFlights.first().fromAirport,
                    to = routeFlights.first().toAirport,
                    flightCount = routeFlights.size,
                    totalHours = totalMinutes / 60,
                    totalMinutes = totalMinutes % 60
                )
            }
            .sortedByDescending { it.flightCount }
    }
    
    val airports: Flow<List<Airport>> = allFlights.map { flights ->
        val airportMap = mutableMapOf<String, Pair<Int, Long>>()
        
        flights.forEach { flight ->
            val fromCount = airportMap[flight.fromAirport]?.first ?: 0
            val fromLastVisit = airportMap[flight.fromAirport]?.second ?: 0L
            airportMap[flight.fromAirport] = Pair(fromCount + 1, maxOf(fromLastVisit, flight.date))
            
            val toCount = airportMap[flight.toAirport]?.first ?: 0
            val toLastVisit = airportMap[flight.toAirport]?.second ?: 0L
            airportMap[flight.toAirport] = Pair(toCount + 1, maxOf(toLastVisit, flight.date))
        }
        
        airportMap.map { (code, data) ->
            Airport(code = code, flightCount = data.first, lastVisit = data.second)
        }.sortedByDescending { it.flightCount }
    }
    
    val aircraft: Flow<List<Aircraft>> = allFlights.map { flights ->
        flights.filter { it.aircraft.isNotBlank() }
            .groupBy { it.aircraft }
            .map { (name, aircraftFlights) ->
                val totalMinutes = aircraftFlights.sumOf { it.durationHours * 60 + it.durationMinutes }
                Aircraft(
                    name = name,
                    flightCount = aircraftFlights.size,
                    totalHours = totalMinutes / 60,
                    totalMinutes = totalMinutes % 60
                )
            }
            .sortedByDescending { it.flightCount }
    }
}


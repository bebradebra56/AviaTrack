package com.aviatrac.softoclub.data.database

import androidx.room.*
import com.aviatrac.softoclub.data.model.Flight
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Query("SELECT * FROM flights ORDER BY date DESC")
    fun getAllFlights(): Flow<List<Flight>>
    
    @Query("SELECT * FROM flights WHERE id = :id")
    suspend fun getFlightById(id: Long): Flight?
    
    @Insert
    suspend fun insertFlight(flight: Flight): Long
    
    @Update
    suspend fun updateFlight(flight: Flight)
    
    @Delete
    suspend fun deleteFlight(flight: Flight)
    
    @Query("SELECT COUNT(*) FROM flights")
    fun getTotalFlights(): Flow<Int>
    
    @Query("SELECT * FROM flights WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getFlightsByDateRange(startDate: Long, endDate: Long): Flow<List<Flight>>
    
    @Query("SELECT * FROM flights WHERE fromAirport = :from AND toAirport = :to")
    fun getFlightsByRoute(from: String, to: String): Flow<List<Flight>>
    
    @Query("DELETE FROM flights")
    suspend fun deleteAllFlights()
}


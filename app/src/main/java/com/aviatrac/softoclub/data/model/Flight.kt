package com.aviatrac.softoclub.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightType: FlightType,
    val date: Long,
    val fromAirport: String,
    val toAirport: String,
    val durationHours: Int,
    val durationMinutes: Int,
    val aircraft: String = "",
    val notes: String = ""
)

enum class FlightType {
    PILOT,
    PASSENGER
}


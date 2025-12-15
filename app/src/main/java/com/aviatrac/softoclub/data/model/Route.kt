package com.aviatrac.softoclub.data.model

data class Route(
    val from: String,
    val to: String,
    val flightCount: Int,
    val totalHours: Int,
    val totalMinutes: Int
)


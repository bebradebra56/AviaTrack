package com.aviatrac.softoclub.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.aviatrac.softoclub.data.model.Flight
import com.aviatrac.softoclub.data.model.FlightType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    
    fun exportFlightsToCsv(context: Context, flights: List<Flight>): File? {
        return try {
            val csvHeader = "Date,From,To,Duration (Hours),Duration (Minutes),Flight Type,Aircraft,Notes\n"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            val csvContent = StringBuilder(csvHeader)
            flights.forEach { flight ->
                val date = dateFormat.format(Date(flight.date))
                val flightType = if (flight.flightType == FlightType.PILOT) "Pilot" else "Passenger"
                val aircraft = flight.aircraft.ifBlank { "-" }
                val notes = flight.notes.replace("\n", " ").replace(",", ";")
                
                csvContent.append("$date,${flight.fromAirport},${flight.toAirport},")
                csvContent.append("${flight.durationHours},${flight.durationMinutes},")
                csvContent.append("$flightType,$aircraft,\"$notes\"\n")
            }
            
            // Create file in cache directory
            val fileName = "avia_track_flights_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            file.writeText(csvContent.toString())
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Avia Track Flights Export")
                putExtra(Intent.EXTRA_TEXT, "My flight data from Avia Track")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share flight data"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


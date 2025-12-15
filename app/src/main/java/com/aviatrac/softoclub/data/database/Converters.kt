package com.aviatrac.softoclub.data.database

import androidx.room.TypeConverter
import com.aviatrac.softoclub.data.model.FlightType

class Converters {
    @TypeConverter
    fun fromFlightType(value: FlightType): String {
        return value.name
    }
    
    @TypeConverter
    fun toFlightType(value: String): FlightType {
        return FlightType.valueOf(value)
    }
}


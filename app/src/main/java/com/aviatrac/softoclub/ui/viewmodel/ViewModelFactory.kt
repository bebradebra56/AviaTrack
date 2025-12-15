package com.aviatrac.softoclub.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aviatrac.softoclub.data.database.AppDatabase
import com.aviatrac.softoclub.data.preferences.PreferencesManager
import com.aviatrac.softoclub.data.repository.FlightRepository

class FlightViewModelFactory(private val repository: FlightRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    
    companion object {
        fun create(context: Context): FlightViewModelFactory {
            val database = AppDatabase.getDatabase(context)
            val repository = FlightRepository(database.flightDao())
            return FlightViewModelFactory(repository)
        }
    }
}

class SettingsViewModelFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    
    companion object {
        fun create(context: Context): SettingsViewModelFactory {
            val preferencesManager = PreferencesManager(context)
            return SettingsViewModelFactory(preferencesManager)
        }
    }
}


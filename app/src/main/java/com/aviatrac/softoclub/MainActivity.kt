package com.aviatrac.softoclub

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.aviatrac.softoclub.ui.navigation.AppNavigation
import com.aviatrac.softoclub.ui.navigation.Screen
import com.aviatrac.softoclub.ui.theme.AviaTrackTheme
import com.aviatrac.softoclub.ui.viewmodel.FlightViewModel
import com.aviatrac.softoclub.ui.viewmodel.FlightViewModelFactory
import com.aviatrac.softoclub.ui.viewmodel.SettingsViewModel
import com.aviatrac.softoclub.ui.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    
    private lateinit var flightViewModel: FlightViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModels
        flightViewModel = ViewModelProvider(
            this,
            FlightViewModelFactory.create(this)
        )[FlightViewModel::class.java]
        
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory.create(this)
        )[SettingsViewModel::class.java]
        
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.BLACK))
        
        setContent {
            AviaTrackTheme {
                val onboardingCompleted by settingsViewModel.onboardingCompleted.collectAsState()
                
                val startDestination = if (onboardingCompleted) {
                    Screen.Dashboard.route
                } else {
                    Screen.Splash.route
                }
                
                AppNavigation(
                    flightViewModel = flightViewModel,
                    settingsViewModel = settingsViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}

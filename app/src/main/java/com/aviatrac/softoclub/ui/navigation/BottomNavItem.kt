package com.aviatrac.softoclub.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(Screen.Dashboard.route, Icons.Default.Dashboard, "Dashboard")
    object Flights : BottomNavItem(Screen.Flights.route, Icons.Default.Flight, "Flights")
    object Stats : BottomNavItem(Screen.Stats.route, Icons.Default.Analytics, "Stats")
    object Settings : BottomNavItem(Screen.Settings.route, Icons.Default.Settings, "Settings")
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Flights,
    BottomNavItem.Stats,
    BottomNavItem.Settings
)


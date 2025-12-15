package com.aviatrac.softoclub.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aviatrac.softoclub.ui.screens.*
import com.aviatrac.softoclub.ui.viewmodel.FlightViewModel
import com.aviatrac.softoclub.ui.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(
    flightViewModel: FlightViewModel,
    settingsViewModel: SettingsViewModel,
    startDestination: String
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Flights.route,
        Screen.Stats.route,
        Screen.Settings.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigate = { onboardingCompleted ->
                        navController.navigate(
                            if (onboardingCompleted) Screen.Dashboard.route else Screen.Onboarding.route
                        ) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        settingsViewModel.setOnboardingCompleted(true)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = flightViewModel,
                    onNavigateToAddFlight = {
                        navController.navigate(Screen.AddFlight.createRoute())
                    },
                    onNavigateToFlightDetails = { flightId ->
                        navController.navigate(Screen.FlightDetails.createRoute(flightId))
                    }
                )
            }
            
            // Flights
            composable(Screen.Flights.route) {
                FlightsScreen(
                    viewModel = flightViewModel,
                    onNavigateToAddFlight = {
                        navController.navigate(Screen.AddFlight.createRoute())
                    },
                    onNavigateToFlightDetails = { flightId ->
                        navController.navigate(Screen.FlightDetails.createRoute(flightId))
                    },
                    onNavigateToEdit = { flightId ->
                        navController.navigate(Screen.AddFlight.createRoute(flightId))
                    }
                )
            }
            
            // Add/Edit Flight
            composable(
                route = Screen.AddFlight.route,
                arguments = listOf(
                    navArgument("flightId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val flightIdString = backStackEntry.arguments?.getString("flightId")
                val flightId = flightIdString?.toLongOrNull()
                AddFlightScreen(
                    viewModel = flightViewModel,
                    flightId = flightId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Flight Details
            composable(
                route = Screen.FlightDetails.route,
                arguments = listOf(
                    navArgument("flightId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val flightId = backStackEntry.arguments?.getLong("flightId") ?: 0L
                FlightDetailsScreen(
                    viewModel = flightViewModel,
                    flightId = flightId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddFlight.createRoute(id))
                    }
                )
            }
            
            // Stats
            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = flightViewModel,
                    onNavigateToRoutes = { navController.navigate(Screen.Routes.route) },
                    onNavigateToAirports = { navController.navigate(Screen.Airports.route) },
                    onNavigateToAircraft = { navController.navigate(Screen.Aircraft.route) },
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) }
                )
            }
            
            // Routes
            composable(Screen.Routes.route) {
                RoutesScreen(
                    viewModel = flightViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRouteFlights = { from, to ->
                        navController.navigate(Screen.RouteFlights.createRoute(from, to))
                    }
                )
            }
            
            // Route Flights
            composable(
                route = Screen.RouteFlights.route,
                arguments = listOf(
                    navArgument("from") { type = NavType.StringType },
                    navArgument("to") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val from = backStackEntry.arguments?.getString("from") ?: ""
                val to = backStackEntry.arguments?.getString("to") ?: ""
                RouteFlightsScreen(
                    viewModel = flightViewModel,
                    from = from,
                    to = to,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFlightDetails = { flightId ->
                        navController.navigate(Screen.FlightDetails.createRoute(flightId))
                    }
                )
            }
            
            // Airports
            composable(Screen.Airports.route) {
                AirportsScreen(
                    viewModel = flightViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Aircraft
            composable(Screen.Aircraft.route) {
                AircraftScreen(
                    viewModel = flightViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Calendar
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    viewModel = flightViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFlightDetails = { flightId ->
                        navController.navigate(Screen.FlightDetails.createRoute(flightId))
                    }
                )
            }
            
            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    flightViewModel = flightViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                    onNavigateToExport = { navController.navigate(Screen.Export.route) }
                )
            }
            
            // Profile
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Reminders
            composable(Screen.Reminders.route) {
                RemindersScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Export
            composable(Screen.Export.route) {
                ExportScreen(
                    viewModel = flightViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}


package com.aviatrac.softoclub.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Flights : Screen("flights")
    object FlightDetails : Screen("flight_details/{flightId}") {
        fun createRoute(flightId: Long) = "flight_details/$flightId"
    }
    object AddFlight : Screen("add_flight?flightId={flightId}") {
        fun createRoute(flightId: Long? = null) = if (flightId != null) "add_flight?flightId=$flightId" else "add_flight"
    }
    object Routes : Screen("routes")
    object RouteFlights : Screen("route_flights/{from}/{to}") {
        fun createRoute(from: String, to: String) = "route_flights/$from/$to"
    }
    object Airports : Screen("airports")
    object Aircraft : Screen("aircraft")
    object Stats : Screen("stats")
    object Calendar : Screen("calendar")
    object Reminders : Screen("reminders")
    object Export : Screen("export")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}


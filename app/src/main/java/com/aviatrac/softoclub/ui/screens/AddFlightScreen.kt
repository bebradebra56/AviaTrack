package com.aviatrac.softoclub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aviatrac.softoclub.data.model.Flight
import com.aviatrac.softoclub.data.model.FlightType
import com.aviatrac.softoclub.ui.viewmodel.FlightViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(
    viewModel: FlightViewModel,
    flightId: Long? = null,
    onNavigateBack: () -> Unit
) {
    var flightType by remember { mutableStateOf(FlightType.PILOT) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var fromAirport by remember { mutableStateOf("") }
    var toAirport by remember { mutableStateOf("") }
    var durationHours by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var aircraft by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val isEditing = flightId != null
    
    // Load existing flight if editing
    LaunchedEffect(flightId) {
        if (flightId != null && flightId > 0) {
            viewModel.loadFlight(flightId)
        }
    }
    
    val selectedFlight by viewModel.selectedFlight.collectAsState()
    
    LaunchedEffect(selectedFlight) {
        selectedFlight?.let { flight ->
            flightType = flight.flightType
            selectedDate = flight.date
            fromAirport = flight.fromAirport
            toAirport = flight.toAirport
            durationHours = flight.durationHours.toString()
            durationMinutes = flight.durationMinutes.toString()
            aircraft = flight.aircraft
            notes = flight.notes
        }
    }
    
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Flight" else "Add Flight",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Flight Type Selector
            Text(
                "Flight Type",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = flightType == FlightType.PILOT,
                    onClick = { flightType = FlightType.PILOT },
                    label = { Text("Pilot") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = flightType == FlightType.PASSENGER,
                    onClick = { flightType = FlightType.PASSENGER },
                    label = { Text("Passenger") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            
            // Date
            Text(
                "Date",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(dateFormatter.format(Date(selectedDate)))
            }
            
            // From Airport
            OutlinedTextField(
                value = fromAirport,
                onValueChange = { fromAirport = it.uppercase() },
                label = { Text("From") },
                placeholder = { Text("Airport code or name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // To Airport
            OutlinedTextField(
                value = toAirport,
                onValueChange = { toAirport = it.uppercase() },
                label = { Text("To") },
                placeholder = { Text("Airport code or name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Duration
            Text(
                "Duration",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = durationHours,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) durationHours = it },
                    label = { Text("Hours") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) durationMinutes = it },
                    label = { Text("Minutes") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            // Aircraft (Optional)
            OutlinedTextField(
                value = aircraft,
                onValueChange = { aircraft = it },
                label = { Text("Aircraft (Optional)") },
                placeholder = { Text("e.g., A320, B737") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                placeholder = { Text("Additional information") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        val hours = durationHours.toIntOrNull() ?: 0
                        val minutes = durationMinutes.toIntOrNull() ?: 0
                        
                        if (fromAirport.isNotBlank() && toAirport.isNotBlank() && (hours > 0 || minutes > 0)) {
                            val flight = Flight(
                                id = flightId ?: 0,
                                flightType = flightType,
                                date = selectedDate,
                                fromAirport = fromAirport.trim(),
                                toAirport = toAirport.trim(),
                                durationHours = hours,
                                durationMinutes = minutes,
                                aircraft = aircraft.trim(),
                                notes = notes.trim()
                            )
                            
                            if (isEditing) {
                                viewModel.updateFlight(flight)
                            } else {
                                viewModel.insertFlight(flight)
                            }
                            viewModel.clearSelectedFlight()
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = fromAirport.isNotBlank() && toAirport.isNotBlank() && 
                             (durationHours.toIntOrNull() ?: 0) + (durationMinutes.toIntOrNull() ?: 0) > 0
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


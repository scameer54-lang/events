package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EventType
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEventDialog(
    viewModel: EventBidViewModel,
    onDismiss: () -> Unit,
    onEventCreated: (String) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Form State
    var title by remember { mutableStateOf("") }
    var selectedEventType by remember { mutableStateOf(EventType.WEDDING) }
    var city by remember { mutableStateOf("Hyderabad") }
    var venueArea by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("20 Dec 2026") }
    var guestCountStr by remember { mutableStateOf("250") }
    var budgetStr by remember { mutableStateOf("150000") }
    var description by remember { mutableStateOf("") }
    val selectedServices = remember { mutableStateListOf("Stage & Lighting", "Floral Decoration") }

    val cities = listOf("Hyderabad", "Bangalore", "Chennai", "Delhi NCR", "Mumbai", "Pune", "Kolkata")
    val availableServices = listOf(
        "Floral Decoration", "Stage & Lighting", "LED Screen Video Wall",
        "Catering & Live Counters", "Photography & 4K Cinema", "DJ & Sound System",
        "Tents & Furniture", "Guest Hospitality"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .imePadding()
                .testTag("post_event_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Post Event Requirement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Step $step of 3: " + when (step) {
                                1 -> "Basic Details"
                                2 -> "Date & Budget"
                                else -> "Services & Review"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Indigo600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { step / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    color = Indigo600,
                    trackColor = Slate100
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (step) {
                    1 -> {
                        // Step 1: Event Type & Title
                        Text(
                            text = "Event Category",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(EventType.values()) { type ->
                                FilterChip(
                                    selected = selectedEventType == type,
                                    onClick = { selectedEventType = type },
                                    label = { Text(type.displayName) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Event Title / Occasion") },
                            placeholder = { Text("e.g. Royal Wedding Reception") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("event_title_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select City",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cities) { c ->
                                FilterChip(
                                    selected = city == c,
                                    onClick = { city = c },
                                    label = { Text(c) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = venueArea,
                            onValueChange = { venueArea = it },
                            label = { Text("Venue / Area") },
                            placeholder = { Text("e.g. Gachibowli, Novotel Convention") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    2 -> {
                        // Step 2: Date, Guest Count & Budget
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { eventDate = it },
                            label = { Text("Event Date") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = guestCountStr,
                            onValueChange = { guestCountStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Expected Guest Count") },
                            leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = budgetStr,
                            onValueChange = { budgetStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Estimated Budget (in INR ₹)") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 Tip: Specifying a realistic budget ensures top-tier verified vendors submit their most competitive bids.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }

                    3 -> {
                        // Step 3: Required Services & Description
                        Text(
                            text = "Required Services",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            availableServices.chunked(2).forEach { rowServices ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowServices.forEach { s ->
                                        val isChecked = selectedServices.contains(s)
                                        FilterChip(
                                            selected = isChecked,
                                            onClick = {
                                                if (isChecked) selectedServices.remove(s) else selectedServices.add(s)
                                            },
                                            label = { Text(s, fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Special Requirements & Theme Details") },
                            placeholder = { Text("Describe specific color palettes, staging preferences, or timing...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(onClick = { step -= 1 }) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step += 1
                            } else {
                                val budget = budgetStr.toLongOrNull() ?: 50000L
                                val guests = guestCountStr.toIntOrNull() ?: 100
                                val finalTitle = if (title.isBlank()) "${selectedEventType.displayName} Celebration" else title
                                val finalVenue = if (venueArea.isBlank()) "City Center" else venueArea
                                val finalDesc = if (description.isBlank()) "Looking for high-quality event service execution for our upcoming event." else description

                                val created = viewModel.postEvent(
                                    title = finalTitle,
                                    eventType = selectedEventType,
                                    city = city,
                                    venueArea = finalVenue,
                                    eventDate = eventDate,
                                    guestCount = guests,
                                    budget = budget,
                                    services = selectedServices.toList(),
                                    description = finalDesc
                                )
                                onEventCreated(created.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        modifier = Modifier.testTag("post_event_next_button")
                    ) {
                        Text(if (step < 3) "Next" else "Publish Event Requirement")
                    }
                }
            }
        }
    }
}

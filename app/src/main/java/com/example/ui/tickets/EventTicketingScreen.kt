package com.example.ui.tickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EventTicket
import com.example.model.PublicEventListing
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTicketingScreen(
    viewModel: EventBidViewModel,
    onBack: () -> Unit
) {
    val publicEvents by viewModel.publicEvents.collectAsState()
    val myTickets by viewModel.myTickets.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Browse Events, 1: My Booked Tickets
    var selectedEventToBook by remember { mutableStateOf<PublicEventListing?>(null) }
    var confirmedTicket by remember { mutableStateOf<EventTicket?>(null) }

    if (selectedEventToBook != null) {
        val event = selectedEventToBook!!
        var selectedTier by remember { mutableStateOf("General Access") }
        var quantity by remember { mutableStateOf(1) }
        val priceMultiplier = if (selectedTier == "VIP Pass") 1.8f else 1.0f
        val perTicketPrice = (event.basePrice * priceMultiplier).toLong()

        Dialog(
            onDismissRequest = { selectedEventToBook = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .imePadding()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Book Event Tickets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        IconButton(onClick = { selectedEventToBook = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Indigo600)
                    Text("${event.venue}, ${event.city} • ${event.date}", style = MaterialTheme.typography.bodySmall, color = Slate600)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Select Ticket Category", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FilterChip(
                            selected = selectedTier == "General Access",
                            onClick = { selectedTier = "General Access" },
                            label = { Text("General Access (₹${event.basePrice})") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedTier == "VIP Pass",
                            onClick = { selectedTier = "VIP Pass" },
                            label = { Text("VIP Pass (₹${(event.basePrice * 1.8).toLong()})") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quantity selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Number of Tickets:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (quantity > 1) quantity-- },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(36.dp)
                            ) { Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

                            Text(
                                text = "$quantity",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )

                            OutlinedButton(
                                onClick = { if (quantity < 10) quantity++ },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(36.dp)
                            ) { Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Amount Payable", style = MaterialTheme.typography.labelSmall, color = Slate400)
                                Text("₹${perTicketPrice * quantity}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Emerald600)
                            }
                            Text("Instant QR Pass", style = MaterialTheme.typography.labelSmall, color = Indigo600, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val booked = viewModel.bookEventTicket(event, selectedTier, perTicketPrice, quantity)
                            selectedEventToBook = null
                            confirmedTicket = booked
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm & Book Ticket", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (confirmedTicket != null) {
        val t = confirmedTicket!!
        AlertDialog(
            onDismissRequest = { confirmedTicket = null },
            icon = {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = Emerald600, modifier = Modifier.size(40.dp))
            },
            title = { Text("Ticket Confirmed!", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(t.eventTitle, fontWeight = FontWeight.Bold, color = Indigo600)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ref: ${t.bookingRef} • ${t.quantity}x ${t.ticketType}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text("Date: ${t.eventDate} • Venue: ${t.venue}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Paid: ₹${t.totalAmount}", fontWeight = FontWeight.Bold, color = Emerald600)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Slate100,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎟️ Digital QR Pass available in 'My Bookings'. Show at venue entrance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate700,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        confirmedTicket = null
                        selectedTab = 1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("View My Tickets")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Ticket Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Indigo600
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Public Events & Expos", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Bookings (${myTickets.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(publicEvents) { pubEvent ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                if (pubEvent.imageResId != null) {
                                    Image(
                                        painter = painterResource(id = pubEvent.imageResId),
                                        contentDescription = pubEvent.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                    )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Surface(color = Indigo50, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            pubEvent.category,
                                            color = Indigo600,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(pubEvent.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Place, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${pubEvent.venue}, ${pubEvent.city}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${pubEvent.date} • ${pubEvent.time}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(pubEvent.description, style = MaterialTheme.typography.bodySmall, color = Slate700, maxLines = 2)

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Slate100)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Tickets From", style = MaterialTheme.typography.labelSmall, color = Slate400)
                                            Text("₹${pubEvent.basePrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Indigo600)
                                        }

                                        Button(
                                            onClick = { selectedEventToBook = pubEvent },
                                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("book_ticket_${pubEvent.id}")
                                        ) {
                                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Book Tickets")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (myTickets.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 30.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Slate400, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("No Booked Tickets Yet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Browse live expos and networking galas to book entry passes.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                }
                            }
                        }
                    }

                    items(myTickets) { t ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(color = Emerald100, shape = RoundedCornerShape(6.dp)) {
                                        Text(t.status, color = Emerald600, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                    Text("Ref: ${t.bookingRef}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Indigo600)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(t.eventTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                                Text("${t.venue}, ${t.city} • ${t.eventDate}", style = MaterialTheme.typography.bodySmall, color = Slate600)

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Slate50,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${t.quantity}x ${t.ticketType}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                        Text("Total: ₹${t.totalAmount}", fontWeight = FontWeight.Bold, color = Emerald600, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

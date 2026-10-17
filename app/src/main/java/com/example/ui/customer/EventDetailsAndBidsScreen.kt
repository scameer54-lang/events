package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.TrustBadgeRow
import com.example.ui.components.TrustCriteriaDialog
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsAndBidsScreen(
    eventId: String,
    viewModel: EventBidViewModel,
    onBack: () -> Unit,
    onOpenChat: (eventId: String, vendorName: String) -> Unit,
    onViewVendorProfile: (String) -> Unit
) {
    val events by viewModel.events.collectAsState()
    val allBids by viewModel.bids.collectAsState()
    val event = events.find { it.id == eventId } ?: events.firstOrNull()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedTrustBadgeInfo by remember { mutableStateOf<TrustBadge?>(null) }
    var bookingSuccessDialog by remember { mutableStateOf<BidItem?>(null) }

    val trustBadge = selectedTrustBadgeInfo
    if (trustBadge != null) {
        TrustCriteriaDialog(badge = trustBadge) {
            selectedTrustBadgeInfo = null
        }
    }

    val acceptedBid = bookingSuccessDialog
    if (acceptedBid != null && event != null) {
        AlertDialog(
            onDismissRequest = { bookingSuccessDialog = null },
            icon = {
                Icon(
                    Icons.Default.Celebration,
                    contentDescription = null,
                    tint = Emerald600,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text("Quotation Accepted!", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "You have officially confirmed ${acceptedBid.vendorName} for ${event.title}."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Final Agreed Price: ${viewModel.formatInr(acceptedBid.price)}",
                        fontWeight = FontWeight.Bold,
                        color = Emerald600
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "A contract confirmation has been generated and sent to both parties. You can now use the in-app chat for timeline coordination.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        bookingSuccessDialog = null
                        onOpenChat(event.id, acceptedBid.vendorName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Open Chat with Vendor")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingSuccessDialog = null }) {
                    Text("Done")
                }
            }
        )
    }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found")
        }
        return
    }

    val bidsForThisEvent = allBids.filter { it.eventId == event.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Quotations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Slate900
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Event Details Summary Banner
            item {
                Surface(
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        if (event.imageResId != null) {
                            Image(
                                painter = painterResource(id = event.imageResId),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Indigo50,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = event.eventType.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = if (event.status == EventStatus.BOOKED) Emerald100 else Amber100,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = event.status.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (event.status == EventStatus.BOOKED) Emerald600 else Amber600,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${event.venueArea}, ${event.city}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(event.eventDate, style = MaterialTheme.typography.bodySmall, color = Slate600)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated Budget: ${viewModel.formatInr(event.estimatedBudget)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600
                                )

                                Text(
                                    text = "Deadline: ${event.biddingDeadline}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }
            }

            // Tab Switcher
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Indigo600
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Quotations (${bidsForThisEvent.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Requirements & Scope", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedTab == 0) {
                // Quotations Comparison View
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Received Quotations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Text(
                                text = "Compare & Hire",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }

                        if (bidsForThisEvent.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.HourglassEmpty,
                                        contentDescription = null,
                                        tint = Amber500,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Awaiting Vendor Quotations",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Eligible verified vendors in ${event.city} are reviewing your requirements. You will receive notifications as bids are submitted.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                items(bidsForThisEvent) { bid ->
                    QuotationCard(
                        bid = bid,
                        formattedPrice = viewModel.formatInr(bid.price),
                        onShortlist = { viewModel.shortlistBid(bid.id) },
                        onAccept = {
                            viewModel.acceptBid(bid.id, event.id)
                            bookingSuccessDialog = bid
                        },
                        onChat = { onOpenChat(event.id, bid.vendorName) },
                        onViewProfile = { onViewVendorProfile(bid.vendorId) },
                        onBadgeClick = { selectedTrustBadgeInfo = it }
                    )
                }
            } else {
                // Requirements & Scope Tab
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Description & Occasion Details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate700,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Slate100)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Services Required",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            event.servicesRequired.forEach { s ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Emerald600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = s, style = MaterialTheme.typography.bodyMedium, color = Slate900)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Slate100)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Procurement Security & Rules",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• All quotations are sealed and binding for the validity duration specified by the vendor.\n• Booking terms are locked upon acceptance.\n• Dispute resolution and verification documents are guaranteed by EventBid Admin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuotationCard(
    bid: BidItem,
    formattedPrice: String,
    onShortlist: () -> Unit,
    onAccept: () -> Unit,
    onChat: () -> Unit,
    onViewProfile: () -> Unit,
    onBadgeClick: (TrustBadge) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("quotation_card_${bid.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Vendor Name, Status, Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bid.vendorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${bid.vendorRating} (${bid.vendorReviewCount} reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• Response: ${bid.responseTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (bid.status) {
                        BidStatus.ACCEPTED -> Emerald100
                        BidStatus.SHORTLISTED -> Amber100
                        else -> Slate100
                    }
                ) {
                    Text(
                        text = bid.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (bid.status) {
                            BidStatus.ACCEPTED -> Emerald600
                            BidStatus.SHORTLISTED -> Amber600
                            else -> Slate600
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trust Badges
            TrustBadgeRow(
                isVerified = bid.isVerified,
                isTopRated = bid.isTopRated,
                isCustomerRecommended = bid.isCustomerRecommended,
                onBadgeClick = onBadgeClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price Box
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
                        Text(
                            text = "Total Quotation Price",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Validity: ${bid.validityDays} Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                        Text(
                            text = bid.submittedAt,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Slate400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inclusions
            Text(
                text = "Package Inclusions:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )
            Spacer(modifier = Modifier.height(4.dp))
            bid.inclusions.forEach { inc ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = inc, style = MaterialTheme.typography.bodySmall, color = Slate700)
                }
            }

            if (bid.exclusions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Exclusions: " + bid.exclusions.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Rose600
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Profile", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onChat,
                    modifier = Modifier.weight(1.1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo600)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", fontSize = 12.sp)
                }

                if (bid.status != BidStatus.ACCEPTED) {
                    if (bid.status != BidStatus.SHORTLISTED) {
                        OutlinedButton(
                            onClick = onShortlist,
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Text("Shortlist", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("accept_bid_${bid.id}")
                    ) {
                        Text("Accept Bid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

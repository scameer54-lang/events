import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PdfAndExcelExporter
import com.example.model.*
import com.example.ui.components.AgreementPreviewDialog
import com.example.ui.components.InvoicePreviewDialog
import com.example.ui.components.TrustBadgeRow
import com.example.ui.components.TrustCriteriaDialog
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@Composable
fun CustomerDashboardScreen(
    viewModel: EventBidViewModel,
    onNavigateToEventDetails: (String) -> Unit,
    onOpenPostEvent: () -> Unit,
    onOpenChat: (eventId: String, vendorName: String) -> Unit,
    onViewVendorProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val bids by viewModel.bids.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val agreements by viewModel.agreements.collectAsState()

    var customerTab by remember { mutableStateOf(0) } // 0: Requirements Feed, 1: Agreements & Invoices
    var selectedCategoryFilter by remember { mutableStateOf<EventType?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<EventStatus?>(null) }
    var selectedCityFilter by remember { mutableStateOf<String?>(null) }
    var selectedBudgetFilter by remember { mutableStateOf<String?>(null) }

    var selectedTrustBadgeInfo by remember { mutableStateOf<TrustBadge?>(null) }
    var previewInvoice by remember { mutableStateOf<PlatformInvoice?>(null) }
    var previewAgreement by remember { mutableStateOf<EventServiceAgreement?>(null) }

    if (previewInvoice != null) {
        InvoicePreviewDialog(invoice = previewInvoice!!) { previewInvoice = null }
    }

    if (previewAgreement != null) {
        AgreementPreviewDialog(agreement = previewAgreement!!) { previewAgreement = null }
    }

    val badge = selectedTrustBadgeInfo
    if (badge != null) {
        TrustCriteriaDialog(badge = badge) {
            selectedTrustBadgeInfo = null
        }
    }

    val shortlistedCount = bids.count { it.status == BidStatus.SHORTLISTED }
    val totalQuotes = bids.size
    val bookedCount = events.count { it.status == EventStatus.BOOKED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Navy900, Navy800)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Namaste, Priya!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Let's organize your next celebration",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate400
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Amber500.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Amber500),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "PS",
                                    fontWeight = FontWeight.Bold,
                                    color = Amber500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Hero tagline card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Navy700.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate600),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Plan Your Perfect Event with Trusted Vendors",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Post requirements, receive transparent competitive quotations & hire government-verified event specialists.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate200,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onOpenPostEvent,
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("post_event_hero_button")
                            ) {
                                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Post an Event Requirement", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Summary Metric Tiles
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    count = events.size.toString(),
                    label = "My Events",
                    icon = Icons.Default.Event,
                    color = Indigo600,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    count = totalQuotes.toString(),
                    label = "Quotations",
                    icon = Icons.Default.ReceiptLong,
                    color = Amber600,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    count = shortlistedCount.toString(),
                    label = "Shortlisted",
                    icon = Icons.Default.BookmarkBorder,
                    color = Emerald600,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    count = bookedCount.toString(),
                    label = "Booked",
                    icon = Icons.Default.CheckCircleOutline,
                    color = Slate600,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Filter Chips
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Event Categories",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All Services") }
                        )
                    }
                    items(EventType.values()) { type ->
                        FilterChip(
                            selected = selectedCategoryFilter == type,
                            onClick = { selectedCategoryFilter = if (selectedCategoryFilter == type) null else type },
                            label = { Text(type.displayName) }
                        )
                    }
                }
            }
        }

        // Active Event Requirements Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Event Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                Text(
                    text = "${events.size} Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = Indigo600,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Events List
        val filteredEvents = if (selectedCategoryFilter == null) events else events.filter { it.eventType == selectedCategoryFilter }
        items(filteredEvents) { event ->
            EventCard(
                event = event,
                formattedBudget = viewModel.formatInr(event.estimatedBudget),
                onCardClick = { onNavigateToEventDetails(event.id) },
                onViewBids = { onNavigateToEventDetails(event.id) }
            )
        }
    }
}

@Composable
fun MetricTile(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Slate600
            )
        }
    }
}

@Composable
fun EventCard(
    event: EventItem,
    formattedBudget: String,
    onCardClick: () -> Unit,
    onViewBids: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .testTag("event_card_${event.id}")
    ) {
        Column {
            // Optional image header if available
            if (event.imageResId != null) {
                Image(
                    painter = painterResource(id = event.imageResId),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.venueArea}, ${event.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.eventDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.guestCount} Guests",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Slate100)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Estimated Budget",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                        Text(
                            text = formattedBudget,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }

                    Button(
                        onClick = onViewBids,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("View ${event.bidsCount} Quotations", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

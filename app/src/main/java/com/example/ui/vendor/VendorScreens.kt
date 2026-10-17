package com.example.ui.vendor

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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.TrustBadgeRow
import com.example.ui.components.TrustCriteriaDialog
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@Composable
fun VendorDashboardScreen(
    viewModel: EventBidViewModel,
    onOpenVerification: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenCampaigns: () -> Unit,
    onOpenBiddingForm: (EventItem) -> Unit,
    onOpenChat: (eventId: String, customerName: String) -> Unit
) {
    val vendor by viewModel.vendorProfile.collectAsState()
    val subscription by viewModel.vendorSubscription.collectAsState()
    val events by viewModel.events.collectAsState()
    val bids by viewModel.bids.collectAsState()

    var selectedTrustBadgeInfo by remember { mutableStateOf<TrustBadge?>(null) }

    val badge = selectedTrustBadgeInfo
    if (badge != null) {
        TrustCriteriaDialog(badge = badge) {
            selectedTrustBadgeInfo = null
        }
    }

    val myBids = bids.filter { it.vendorId == vendor.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Vendor Header
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
                                text = "Welcome, ${vendor.ownerName}!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${vendor.businessName} • ${vendor.primaryCity}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate400
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Indigo600.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Indigo500)
                        ) {
                            Text(
                                text = "${subscription.tier.planName} Plan",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Amber500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TrustBadgeRow(
                        isVerified = vendor.isVerified,
                        isTopRated = vendor.isTopRated,
                        isCustomerRecommended = vendor.isCustomerRecommended,
                        onBadgeClick = { selectedTrustBadgeInfo = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quota Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuotaCard(
                            label = "Remaining Bids",
                            value = "${subscription.remainingBids}",
                            subtext = "of ${subscription.tier.maxBids} monthly",
                            icon = Icons.Default.Gavel,
                            color = Amber500,
                            modifier = Modifier.weight(1f)
                        )

                        QuotaCard(
                            label = "Lead Views",
                            value = "${subscription.remainingViews}",
                            subtext = "of ${subscription.tier.maxOpportunityViews} monthly",
                            icon = Icons.Default.Visibility,
                            color = Indigo500,
                            modifier = Modifier.weight(1f)
                        )

                        QuotaCard(
                            label = "Active Bids",
                            value = "${myBids.size}",
                            subtext = "${myBids.count { it.status == BidStatus.SHORTLISTED }} shortlisted",
                            icon = Icons.Default.Receipt,
                            color = Emerald500,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Verification Status Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (vendor.isVerified) Emerald100.copy(alpha = 0.6f) else Amber100),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (vendor.isVerified) Icons.Default.VerifiedUser else Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = if (vendor.isVerified) Emerald600 else Amber600,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (vendor.isVerified) "Official Verified Vendor" else "Action Required: Complete Verification",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = Slate900
                            )
                            Text(
                                text = if (vendor.isVerified) "GSTIN & MSME verified. Enjoy full bidding eligibility." else "Upload government business documents to unlock top tier bidding.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                    }

                    Button(
                        onClick = onOpenVerification,
                        colors = ButtonDefaults.buttonColors(containerColor = if (vendor.isVerified) Emerald600 else Amber600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("vendor_manage_verification_btn")
                    ) {
                        Text(if (vendor.isVerified) "Manage" else "Verify", fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionTile(
                        title = "Upgrade Quota",
                        subtitle = "Plans & Limits",
                        icon = Icons.Default.Upgrade,
                        color = Indigo600,
                        onClick = onOpenSubscriptions,
                        modifier = Modifier.weight(1f)
                    )

                    ActionTile(
                        title = "Promotions",
                        subtitle = "Featured Ads",
                        icon = Icons.Default.Campaign,
                        color = Amber600,
                        onClick = onOpenCampaigns,
                        modifier = Modifier.weight(1f)
                    )

                    ActionTile(
                        title = "Verify Docs",
                        subtitle = "GST & MSME",
                        icon = Icons.Default.Security,
                        color = Emerald600,
                        onClick = onOpenVerification,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Available Bidding Opportunities Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Event Opportunities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "${events.size} Available",
                    style = MaterialTheme.typography.bodySmall,
                    color = Indigo600,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Opportunities List
        items(events) { event ->
            val hasBid = myBids.any { it.eventId == event.id }
            OpportunityCard(
                event = event,
                formattedBudget = viewModel.formatInr(event.estimatedBudget),
                hasSubmittedBid = hasBid,
                remainingBidsQuota = subscription.remainingBids,
                onSubmitBidClick = { onOpenBiddingForm(event) },
                onChatClick = { onOpenChat(event.id, event.customerName) }
            )
        }
    }
}

@Composable
fun QuotaCard(
    label: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy700.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate600),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = Slate200)
            Text(text = subtext, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = Slate400, maxLines = 1)
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate900)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate400)
        }
    }
}

@Composable
fun OpportunityCard(
    event: EventItem,
    formattedBudget: String,
    hasSubmittedBid: Boolean,
    remainingBidsQuota: Int,
    onSubmitBidClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("opportunity_card_${event.id}")
    ) {
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
                    color = Amber100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Deadline: ${event.biddingDeadline}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Amber600,
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

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${event.venueArea}, ${event.city}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                Spacer(modifier = Modifier.width(10.dp))
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(event.eventDate, style = MaterialTheme.typography.bodySmall, color = Slate600)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate700,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))
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
                        color = Indigo600
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (hasSubmittedBid) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Emerald100
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quotation Submitted", fontWeight = FontWeight.Bold, color = Emerald600, fontSize = 12.sp)
                            }
                        }

                        IconButton(onClick = onChatClick) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = Indigo600)
                        }
                    } else {
                        Button(
                            onClick = onSubmitBidClick,
                            enabled = remainingBidsQuota > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("submit_quotation_btn_${event.id}")
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (remainingBidsQuota > 0) "Submit Quotation" else "Quota Reached")
                        }
                    }
                }
            }
        }
    }
}

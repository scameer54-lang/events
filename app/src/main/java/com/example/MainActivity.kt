package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EventItem
import com.example.model.UserRole
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.chat.ChatScreen
import com.example.ui.components.RoleSwitcherBar
import com.example.ui.customer.CustomerDashboardScreen
import com.example.ui.customer.EventDetailsAndBidsScreen
import com.example.ui.customer.PostEventDialog
import com.example.ui.customer.VendorProfileModal
import com.example.ui.theme.EventBidTheme
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Navy900
import com.example.ui.vendor.BidSubmissionDialog
import com.example.ui.vendor.CampaignMarketplaceScreen
import com.example.ui.vendor.SubscriptionPlansScreen
import com.example.ui.vendor.VendorDashboardScreen
import com.example.ui.vendor.VendorVerificationScreen
import com.example.viewmodel.EventBidViewModel

sealed class AppScreen {
    object Main : AppScreen()
    data class EventDetails(val eventId: String) : AppScreen()
    data class Chat(val eventId: String, val counterpartName: String) : AppScreen()
    object VendorVerification : AppScreen()
    object VendorSubscriptions : AppScreen()
    object VendorCampaigns : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventBidTheme {
                EventBidApp()
            }
        }
    }
}

@Composable
fun EventBidApp(
    viewModel: EventBidViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsState()

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Main) }
    var selectedBottomTab by remember { mutableStateOf(0) }

    // Dialogs state
    var showPostEventDialog by remember { mutableStateOf(false) }
    var selectedBidSubmissionEvent by remember { mutableStateOf<EventItem?>(null) }
    var inspectVendorProfileId by remember { mutableStateOf<String?>(null) }

    // Handle back button for sub-screens
    if (currentScreen !is AppScreen.Main) {
        BackHandler {
            currentScreen = AppScreen.Main
        }
    }

    // Modals
    if (showPostEventDialog) {
        PostEventDialog(
            viewModel = viewModel,
            onDismiss = { showPostEventDialog = false },
            onEventCreated = { newEventId ->
                showPostEventDialog = false
                currentScreen = AppScreen.EventDetails(newEventId)
            }
        )
    }

    val currentBidEvent = selectedBidSubmissionEvent
    if (currentBidEvent != null) {
        BidSubmissionDialog(
            event = currentBidEvent,
            viewModel = viewModel,
            onDismiss = { selectedBidSubmissionEvent = null },
            onBidSubmitted = {
                val eventId = currentBidEvent.id
                selectedBidSubmissionEvent = null
                currentScreen = AppScreen.Chat(eventId, "Priya Sharma (Organizer)")
            }
        )
    }

    val currentVendorId = inspectVendorProfileId
    if (currentVendorId != null) {
        VendorProfileModal(
            vendorId = currentVendorId,
            viewModel = viewModel,
            onDismiss = { inspectVendorProfileId = null },
            onOpenChat = {
                inspectVendorProfileId = null
                currentScreen = AppScreen.Chat("EVT-1001", "Dream Decor")
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Persistent Role Switcher
                RoleSwitcherBar(
                    currentRole = currentRole,
                    onRoleSelected = { newRole ->
                        viewModel.switchRole(newRole)
                        currentScreen = AppScreen.Main
                        selectedBottomTab = 0
                    }
                )
            }
        },
        bottomBar = {
            if (currentScreen is AppScreen.Main) {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    when (currentRole) {
                        UserRole.CUSTOMER -> {
                            NavigationBarItem(
                                selected = selectedBottomTab == 0,
                                onClick = { selectedBottomTab = 0 },
                                icon = { Icon(Icons.Default.Explore, contentDescription = "Dashboard") },
                                label = { Text("Explore") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 1,
                                onClick = { showPostEventDialog = true },
                                icon = { Icon(Icons.Default.AddCircle, contentDescription = "Post Event") },
                                label = { Text("Post Event") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 2,
                                onClick = {
                                    currentScreen = AppScreen.Chat("EVT-1001", "Dream Decor")
                                },
                                icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages") },
                                label = { Text("Chat") }
                            )
                        }

                        UserRole.VENDOR -> {
                            NavigationBarItem(
                                selected = selectedBottomTab == 0,
                                onClick = { selectedBottomTab = 0 },
                                icon = { Icon(Icons.Default.Storefront, contentDescription = "Opportunities") },
                                label = { Text("Opportunities") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 1,
                                onClick = { currentScreen = AppScreen.VendorVerification },
                                icon = { Icon(Icons.Default.Security, contentDescription = "Verification") },
                                label = { Text("Verification") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 2,
                                onClick = { currentScreen = AppScreen.VendorSubscriptions },
                                icon = { Icon(Icons.Default.Upgrade, contentDescription = "Plans") },
                                label = { Text("Plans") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 3,
                                onClick = { currentScreen = AppScreen.VendorCampaigns },
                                icon = { Icon(Icons.Default.Campaign, contentDescription = "Ads") },
                                label = { Text("Campaigns") }
                            )
                        }

                        UserRole.ADMIN, UserRole.OPERATIONS -> {
                            NavigationBarItem(
                                selected = selectedBottomTab == 0,
                                onClick = { selectedBottomTab = 0 },
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Audit Desk") },
                                label = { Text("Audit Desk") }
                            )
                            NavigationBarItem(
                                selected = selectedBottomTab == 1,
                                onClick = { currentScreen = AppScreen.Chat("EVT-1001", "Priya Sharma & Dream Decor") },
                                icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Dispute Chat") },
                                label = { Text("Disputes") }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Main -> {
                    when (currentRole) {
                        UserRole.CUSTOMER -> {
                            CustomerDashboardScreen(
                                viewModel = viewModel,
                                onNavigateToEventDetails = { id ->
                                    currentScreen = AppScreen.EventDetails(id)
                                },
                                onOpenPostEvent = { showPostEventDialog = true },
                                onOpenChat = { eventId, vendorName ->
                                    currentScreen = AppScreen.Chat(eventId, vendorName)
                                },
                                onViewVendorProfile = { vendorId ->
                                    inspectVendorProfileId = vendorId
                                }
                            )
                        }

                        UserRole.VENDOR -> {
                            VendorDashboardScreen(
                                viewModel = viewModel,
                                onOpenVerification = { currentScreen = AppScreen.VendorVerification },
                                onOpenSubscriptions = { currentScreen = AppScreen.VendorSubscriptions },
                                onOpenCampaigns = { currentScreen = AppScreen.VendorCampaigns },
                                onOpenBiddingForm = { event -> selectedBidSubmissionEvent = event },
                                onOpenChat = { eventId, custName ->
                                    currentScreen = AppScreen.Chat(eventId, custName)
                                }
                            )
                        }

                        UserRole.ADMIN, UserRole.OPERATIONS -> {
                            AdminDashboardScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }

                is AppScreen.EventDetails -> {
                    EventDetailsAndBidsScreen(
                        eventId = screen.eventId,
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Main },
                        onOpenChat = { eventId, vendorName ->
                            currentScreen = AppScreen.Chat(eventId, vendorName)
                        },
                        onViewVendorProfile = { vId ->
                            inspectVendorProfileId = vId
                        }
                    )
                }

                is AppScreen.Chat -> {
                    ChatScreen(
                        eventId = screen.eventId,
                        counterpartName = screen.counterpartName,
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Main }
                    )
                }

                is AppScreen.VendorVerification -> {
                    VendorVerificationScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Main }
                    )
                }

                is AppScreen.VendorSubscriptions -> {
                    SubscriptionPlansScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Main }
                    )
                }

                is AppScreen.VendorCampaigns -> {
                    CampaignMarketplaceScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.Main }
                    )
                }
            }
        }
    }
}

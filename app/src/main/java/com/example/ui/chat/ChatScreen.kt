package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ChatMessage
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    eventId: String,
    counterpartName: String,
    viewModel: EventBidViewModel,
    onBack: () -> Unit
) {
    val allMessages by viewModel.chatMessages.collectAsState()
    val events by viewModel.events.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    val event = events.find { it.id == eventId } ?: events.firstOrNull()
    val eventTitle = event?.title ?: "Event Negotiation"
    val messages = allMessages.filter { it.eventId == (event?.id ?: "EVT-1001") }

    var inputMessage by remember { mutableStateOf("") }
    var showAttachmentDialog by remember { mutableStateOf(false) }
    var previewAttachmentName by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showAttachmentDialog) {
        Dialog(onDismissRequest = { showAttachmentDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Share Event File or Document",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    AttachmentOptionItem(
                        icon = Icons.Default.PictureAsPdf,
                        color = Rose600,
                        title = "Itemized Quotation Deck (PDF)",
                        subtitle = "Detailed material & labor cost breakdown",
                        onClick = {
                            viewModel.sendChatMessage(
                                eventId = event?.id ?: "EVT-1001",
                                eventTitle = eventTitle,
                                text = "Sharing our official verified itemized quotation and floral breakdown.",
                                attachmentName = "Itemized_Quotation_EventBid.pdf",
                                attachmentType = "PDF"
                            )
                            showAttachmentDialog = false
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AttachmentOptionItem(
                        icon = Icons.Default.Image,
                        color = Indigo600,
                        title = "Stage & Floral Reference Photo",
                        subtitle = "High-res portfolio setup at Taj Krishna",
                        onClick = {
                            viewModel.sendChatMessage(
                                eventId = event?.id ?: "EVT-1001",
                                eventTitle = eventTitle,
                                text = "Here is the exact floral arch and warm ambient lighting stage setup.",
                                attachmentName = "Stage_Mandap_Design_Reference.jpg",
                                attachmentType = "IMAGE"
                            )
                            showAttachmentDialog = false
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AttachmentOptionItem(
                        icon = Icons.Default.Handshake,
                        color = Emerald600,
                        title = "Event Service Agreement",
                        subtitle = "Terms, cancellation & timeline guarantee",
                        onClick = {
                            viewModel.sendChatMessage(
                                eventId = event?.id ?: "EVT-1001",
                                eventTitle = eventTitle,
                                text = "Contract terms draft shared for final booking confirmation.",
                                attachmentName = "EventBid_Standard_Service_Agreement.pdf",
                                attachmentType = "PDF"
                            )
                            showAttachmentDialog = false
                        }
                    )
                }
            }
        }
    }

    if (previewAttachmentName != null) {
        AlertDialog(
            onDismissRequest = { previewAttachmentName = null },
            icon = {
                Icon(
                    Icons.Default.FilePresent,
                    contentDescription = null,
                    tint = Indigo600,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text(previewAttachmentName!!, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Secure procurement attachment stored on EventBid document cloud.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Status: Verified & Timestamped\nAssociated Event: ${event?.title}\nLinked Parties: Priya Sharma ↔ Dream Decor",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { previewAttachmentName = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Close Preview")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Indigo100,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = counterpartName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = counterpartName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Emerald500, CircleShape)
                                )
                            }
                            Text(
                                text = "Event: ${eventTitle.take(24)}...",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Slate600
                            )
                        }
                    }
                },
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
                .background(Slate100)
        ) {
            // Event Context Header Bar
            Surface(
                color = Indigo50,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Indigo600, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${event?.eventType?.displayName} • ${event?.city}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo600
                        )
                    }

                    Text(
                        text = "Budget: ${viewModel.formatInr(event?.estimatedBudget ?: 0L)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages) { msg ->
                    val isMyMsg = if (currentRole == UserRole.CUSTOMER) msg.isFromCustomer else !msg.isFromCustomer
                    ChatMessageBubble(
                        message = msg,
                        isMyMessage = isMyMsg,
                        onAttachmentClick = { previewAttachmentName = it }
                    )
                }
            }

            // Quick Canned Reply Chips
            val cannedReplies = if (currentRole == UserRole.CUSTOMER) {
                listOf(
                    "Can you share an itemized breakdown?",
                    "Does price include transport & taxes?",
                    "Can we do an on-site venue recce?",
                    "We are ready to book!"
                )
            } else {
                listOf(
                    "Yes, we are available on your date!",
                    "Walkway lanterns are included complimentary!",
                    "Setup will finish 3 hours prior to event.",
                    "Our verified florists will be on-site."
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(cannedReplies) { reply ->
                    Surface(
                        onClick = {
                            viewModel.sendChatMessage(
                                eventId = event?.id ?: "EVT-1001",
                                eventTitle = eventTitle,
                                text = reply
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Slate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Text(
                            text = reply,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Slate900,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Bottom Input Bar
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showAttachmentDialog = true },
                        modifier = Modifier.testTag("chat_attachment_btn")
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach file", tint = Indigo600)
                    }

                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Type your message here...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendChatMessage(
                                    eventId = event?.id ?: "EVT-1001",
                                    eventTitle = eventTitle,
                                    text = inputMessage.trim()
                                )
                                inputMessage = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Indigo600),
                        modifier = Modifier.testTag("chat_send_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMyMessage: Boolean,
    onAttachmentClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = Slate400,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMyMessage) 14.dp else 2.dp,
                bottomEnd = if (isMyMessage) 2.dp else 14.dp
            ),
            color = if (isMyMessage) Indigo600 else Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMyMessage) Color.White else Slate900,
                    lineHeight = 18.sp
                )

                if (message.attachmentName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = { onAttachmentClick(message.attachmentName) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMyMessage) Indigo500 else Slate100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (message.attachmentType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                                contentDescription = null,
                                tint = if (isMyMessage) Color.White else Indigo600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.attachmentName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isMyMessage) Color.White else Slate900,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                        color = if (isMyMessage) Indigo100 else Slate400
                    )
                    if (isMyMessage) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Indigo100,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate400)
            }
        }
    }
}

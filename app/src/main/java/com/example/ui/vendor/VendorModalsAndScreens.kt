package com.example.ui.vendor

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidSubmissionDialog(
    event: EventItem,
    viewModel: EventBidViewModel,
    onDismiss: () -> Unit,
    onBidSubmitted: () -> Unit
) {
    var priceStr by remember { mutableStateOf((event.estimatedBudget * 0.95).toLong().toString()) }
    var validityDaysStr by remember { mutableStateOf("7") }
    var deliveryNotes by remember { mutableStateOf("Complete on-site setup will be delivered 3 hours prior to event start.") }
    val inclusions = remember { mutableStateListOf("All floral mandap installations", "Ambient fairy & par lights", "Dedicated crew supervisor") }
    val exclusions = remember { mutableStateListOf("Venue generator power diesel charges") }
    var newInclusion by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val subscription by viewModel.vendorSubscription.collectAsState()
    val hasCredits = subscription.remainingBids > 0

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
                .testTag("bid_submission_dialog")
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
                            text = "Submit Quotation Bid",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = Indigo600,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quota alert & strict credit gate
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (hasCredits) Amber100 else Rose100
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasCredits) Icons.Default.Gavel else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (hasCredits) Amber600 else Rose600,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasCredits)
                                "Submitting consumes 1 bid credit. Remaining: ${subscription.remainingBids} bids"
                            else
                                "ACCESS LOCKED: 0 Bids Remaining. Please purchase credits to submit quotation.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (hasCredits) Amber600 else Rose600,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!hasCredits) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your monthly quota has been exhausted. Choose one of our affordable packages starting at ₹199 to unlock instant bid submissions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Upgrade, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upgrade Subscription Plans")
                    }
                    return@Column
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quotation Amount (in INR ₹)") },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quotation_price_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = validityDaysStr,
                    onValueChange = { validityDaysStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Quotation Validity (in Days)") },
                    leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Package Inclusions:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                inclusions.forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Emerald600, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = { inclusions.removeAt(index) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Slate400, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newInclusion,
                        onValueChange = { newInclusion = it },
                        placeholder = { Text("Add custom inclusion...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (newInclusion.isNotBlank()) {
                                inclusions.add(newInclusion)
                                newInclusion = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deliveryNotes,
                    onValueChange = { deliveryNotes = it },
                    label = { Text("Delivery Terms & Logistics Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMsg!!, color = Rose600, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val price = priceStr.toLongOrNull() ?: 0L
                        val days = validityDaysStr.toIntOrNull() ?: 7
                        if (price <= 0) {
                            errorMsg = "Please enter a valid quotation price"
                            return@Button
                        }
                        val success = viewModel.submitBid(
                            eventId = event.id,
                            price = price,
                            inclusions = inclusions.toList(),
                            exclusions = exclusions.toList(),
                            validityDays = days,
                            deliveryNotes = deliveryNotes
                        )
                        if (success) {
                            onBidSubmitted()
                        } else {
                            errorMsg = "Monthly bidding quota exhausted. Please upgrade your subscription."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_submit_bid_btn")
                ) {
                    Text("Confirm & Submit Quotation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorVerificationScreen(
    viewModel: EventBidViewModel,
    onBack: () -> Unit
) {
    val vendor by viewModel.vendorProfile.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }

    if (showUploadDialog) {
        UploadDocumentDialog(
            viewModel = viewModel,
            onDismiss = { showUploadDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Verification Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Emerald600, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Government & Business Verification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload business documents via PDF file or capture directly using your camera scanner. All documents are reviewed and audited by EventBid operations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Emerald100,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Unlocked Perks for Verified Vendors:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Emerald600)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("✓ Prominent 'Verified' Green Shield badge on quotes", style = MaterialTheme.typography.bodySmall, color = Slate900)
                                Text("✓ Eligibility for high-value events (> ₹1,00,000)", style = MaterialTheme.typography.bodySmall, color = Slate900)
                                Text("✓ 3.2x higher shortlist rate from wedding planners", style = MaterialTheme.typography.bodySmall, color = Slate900)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Submitted Documents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Button(
                        onClick = { showUploadDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload / Camera")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(vendor.verificationDocuments) { doc ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = when (doc.status) {
                                    VerificationStatus.VERIFIED -> Icons.Default.CheckCircle
                                    VerificationStatus.UNDER_REVIEW -> Icons.Default.AccessTime
                                    VerificationStatus.REJECTED -> Icons.Default.Cancel
                                    else -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = when (doc.status) {
                                    VerificationStatus.VERIFIED -> Emerald600
                                    VerificationStatus.UNDER_REVIEW -> Amber500
                                    VerificationStatus.REJECTED -> Rose600
                                    else -> Slate400,
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = doc.docType.displayName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Slate900
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = if (doc.uploadSource == UploadSource.CAMERA) Amber100 else Indigo50,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (doc.uploadSource == UploadSource.CAMERA) "📷 Camera" else "📁 PDF",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                            color = if (doc.uploadSource == UploadSource.CAMERA) Amber600 else Indigo600,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Doc No: ${doc.docNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                                Text(
                                    text = "File: ${doc.fileName}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Slate400
                                )

                                if (doc.rejectionReason != null) {
                                    Text(
                                        text = "Reason: ${doc.rejectionReason}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Rose600,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (doc.status) {
                                VerificationStatus.VERIFIED -> Emerald100
                                VerificationStatus.UNDER_REVIEW -> Amber100
                                VerificationStatus.REJECTED -> Rose100
                                else -> Slate100
                            }
                        ) {
                            Text(
                                text = doc.status.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (doc.status) {
                                    VerificationStatus.VERIFIED -> Emerald600
                                    VerificationStatus.UNDER_REVIEW -> Amber600
                                    VerificationStatus.REJECTED -> Rose600
                                    else -> Slate600
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadDocumentDialog(
    viewModel: EventBidViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(VerificationDocType.TRADE_LICENSE) }
    var uploadMode by remember { mutableStateOf(UploadSource.FILE) }
    var docNumber by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var captionNote by remember { mutableStateOf("") }
    var cameraCaptured by remember { mutableStateOf(false) }

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
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upload Verification Document", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Upload Mode selector (File vs Camera)
                Text("Select Upload Method", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { uploadMode = UploadSource.FILE },
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == UploadSource.FILE) Indigo50 else Slate100,
                        border = if (uploadMode == UploadSource.FILE) androidx.compose.foundation.BorderStroke(1.dp, Indigo600) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Indigo600, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Device File/PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        onClick = { uploadMode = UploadSource.CAMERA },
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == UploadSource.CAMERA) Indigo50 else Slate100,
                        border = if (uploadMode == UploadSource.CAMERA) androidx.compose.foundation.BorderStroke(1.dp, Indigo600) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Amber600, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera Scan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uploadMode == UploadSource.CAMERA) {
                    // Simulated Camera Viewfinder
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy900,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (cameraCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = if (cameraCaptured) Emerald500 else Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (cameraCaptured) "✓ Document Captured: scan_${System.currentTimeMillis() % 10000}.jpg" else "Align document inside border",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        cameraCaptured = true
                                        fileName = "camera_scan_${selectedType.name.lowercase()}.jpg"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text(if (cameraCaptured) "Retake Photo" else "Snap Document", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text("Document Type", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                VerificationDocType.values().forEach { t ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedType == t,
                            onClick = { selectedType = t }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(t.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(t.requiredFor, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate400)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("Registration / Certificate Number") },
                    placeholder = { Text("e.g. 29AABCU9876Q1Z2 or FSSAI-2026") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name / Attachment") },
                    placeholder = { Text(if (uploadMode == UploadSource.CAMERA) "camera_document_scan.jpg" else "GST_Registration_2026.pdf") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = captionNote,
                    onValueChange = { captionNote = it },
                    label = { Text("Document Caption / Notes") },
                    placeholder = { Text("e.g. Valid until 2027, verified with CA") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalNum = if (docNumber.isBlank()) "DOC-${(1000..9999).random()}" else docNumber
                        val finalFile = if (fileName.isBlank()) "${selectedType.name.lowercase()}_scan.${if (uploadMode == UploadSource.CAMERA) "jpg" else "pdf"}" else fileName
                        viewModel.uploadDocument(selectedType, finalNum, finalFile, uploadMode)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit for Admin Review", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// User requested packages: 199, 299, 399, 499, 1999
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPlansScreen(
    viewModel: EventBidViewModel,
    onBack: () -> Unit
) {
    val currentSub by viewModel.vendorSubscription.collectAsState()
    var paymentCheckoutTier by remember { mutableStateOf<SubscriptionTier?>(null) }
    val context = LocalContext.current

    if (paymentCheckoutTier != null) {
        MockPaymentGatewayDialog(
            tier = paymentCheckoutTier!!,
            onDismiss = { paymentCheckoutTier = null },
            onPaymentSuccess = {
                viewModel.upgradeSubscription(paymentCheckoutTier!!)
                paymentCheckoutTier = null
                Toast.makeText(context, "Subscription Activated! New Quotas Applied.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Subscription Plans", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Exporting GST Subscription Invoice (PDF)...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Invoice PDF", tint = Indigo600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Surface(
                    color = Indigo50,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Affordable monthly packages for Indian vendors: ₹199, ₹299, ₹399, ₹499 & ₹1,999 with zero lead commission.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate900,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(SubscriptionTier.values()) { tier ->
                val isCurrent = (currentSub.tier == tier)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, Indigo600) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tier.planName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = tier.profileType,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Indigo600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${tier.monthlyPriceInr}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600
                                )
                                Text(
                                    text = "per month",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate400
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(modifier = Modifier.height(12.dp))

                        tier.perks.forEach { perk ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(perk, style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Indigo50,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Current Active Plan (${currentSub.remainingBids} bids left)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600,
                                    modifier = Modifier.padding(10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        } else {
                            Button(
                                onClick = { paymentCheckoutTier = tier },
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Choose ${tier.planName} (₹${tier.monthlyPriceInr}/mo)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MockPaymentGatewayDialog(
    tier: SubscriptionTier,
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedPaymentMode by remember { mutableStateOf("UPI / Google Pay") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("EventBid Secure Checkout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tier.planName + " Membership", fontWeight = FontWeight.Bold)
                            Text("${tier.maxBids} Bids • ${tier.maxOpportunityViews} Lead Views", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                        Text("₹${tier.monthlyPriceInr}", fontWeight = FontWeight.Bold, color = Indigo600, style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Select Indian Payment Method", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                listOf("UPI / Google Pay / PhonePe", "Credit / Debit Card (Visa/Mastercard/RuPay)", "Net Banking (SBI / HDFC / ICICI)").forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPaymentMode == mode,
                            onClick = { selectedPaymentMode = mode }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(mode, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onPaymentSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay ₹${tier.monthlyPriceInr} & Activate", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignMarketplaceScreen(
    viewModel: EventBidViewModel,
    onBack: () -> Unit
) {
    val campaigns by viewModel.campaigns.collectAsState()
    var purchaseType by remember { mutableStateOf<CampaignType?>(null) }

    if (purchaseType != null) {
        AlertDialog(
            onDismissRequest = { purchaseType = null },
            title = { Text("Launch Promotional Campaign", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(purchaseType!!.title, fontWeight = FontWeight.Bold, color = Indigo600)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(purchaseType!!.description, style = MaterialTheme.typography.bodySmall, color = Slate600)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Campaign Budget: ₹${purchaseType!!.priceInr}", fontWeight = FontWeight.Bold, color = Emerald600)
                    Text("Duration: ${purchaseType!!.durationDays} Days", style = MaterialTheme.typography.bodySmall, color = Slate600)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.purchaseCampaign(purchaseType!!, "Hyderabad & Bangalore", "Wedding & Corporate")
                        purchaseType = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("Pay & Launch")
                }
            },
            dismissButton = {
                TextButton(onClick = { purchaseType = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promotional Campaigns", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Slate50),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Active Campaign Performance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(campaigns) { camp ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(camp.campaignType.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Surface(color = Emerald100, shape = RoundedCornerShape(4.dp)) {
                                Text(camp.status, color = Emerald600, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Target: ${camp.targetCity} • ${camp.targetCategory}", style = MaterialTheme.typography.bodySmall, color = Slate600)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("👁 ${camp.impressions} Views", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Indigo600)
                            Text("👆 ${camp.clicks} Clicks", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Amber600)
                            Text("💬 ${camp.enquiries} Enquiries", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Emerald600)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Available Promotional Packages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(CampaignType.values()) { type ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(type.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(type.description, style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${type.priceInr} • ${type.durationDays} Days", fontWeight = FontWeight.Bold, color = Indigo600, style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = { purchaseType = type },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Buy")
                        }
                    }
                }
            }
        }
    }
}

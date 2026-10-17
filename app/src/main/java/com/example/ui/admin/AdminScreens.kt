package com.example.ui.admin

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.EventBidRepository
import com.example.data.PdfAndExcelExporter
import com.example.model.*
import com.example.ui.components.AgreementPreviewDialog
import com.example.ui.components.InvoicePreviewDialog
import com.example.ui.theme.*
import com.example.viewmodel.EventBidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: EventBidViewModel
) {
    val context = LocalContext.current
    val queue by viewModel.adminVerificationQueue.collectAsState()
    val events by viewModel.events.collectAsState()
    val bids by viewModel.bids.collectAsState()
    val vendor by viewModel.vendorProfile.collectAsState()
    val users by viewModel.usersList.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val agreements by viewModel.agreements.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: Verification Desk, 1: Analytics & Graphs, 2: User Governance, 3: Invoices & Agreements

    // Filters for Verification Desk
    var statusFilter by remember { mutableStateOf<VerificationStatus?>(null) }
    var docTypeFilter by remember { mutableStateOf<VerificationDocType?>(null) }

    // Filters for Analytics & Invoices
    var analyticsPeriod by remember { mutableStateOf("Year-to-Date (2026)") }
    var analyticsMetric by remember { mutableStateOf("All Metrics") }
    var invoiceTypeFilter by remember { mutableStateOf("ALL") } // ALL, CUSTOMER, VENDOR, AGREEMENT

    // Dialog states
    var previewDocumentItem by remember { mutableStateOf<EventBidRepository.AdminVerificationSubmission?>(null) }
    var previewInvoice by remember { mutableStateOf<PlatformInvoice?>(null) }
    var previewAgreement by remember { mutableStateOf<EventServiceAgreement?>(null) }
    var rejectSubmissionId by remember { mutableStateOf<String?>(null) }
    var revokeVendorDialog by remember { mutableStateOf(false) }
    var deleteUserCandidate by remember { mutableStateOf<AppUser?>(null) }

    if (previewInvoice != null) {
        InvoicePreviewDialog(
            invoice = previewInvoice!!,
            onDismiss = { previewInvoice = null }
        )
    }

    if (previewAgreement != null) {
        AgreementPreviewDialog(
            agreement = previewAgreement!!,
            onDismiss = { previewAgreement = null }
        )
    }

    // Document Inspection Modal (Admin sees full vendor info + document)
    if (previewDocumentItem != null) {
        val item = previewDocumentItem!!
        Dialog(
            onDismissRequest = { previewDocumentItem = null },
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
                        Text("Vendor Audit & Document Inspection", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { previewDocumentItem = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vendor Identity Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(item.vendorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("Proprietor: ${item.ownerName} • ${item.businessType}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("Email: ${item.email} • Phone: ${item.phone}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("Operating City: ${item.city}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Document Preview Frame
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy900,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_eventbid_logo),
                                    contentDescription = "Document Seal",
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "OFFICIAL GOVERNMENT AUDIT RECORD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Amber500,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.docType.displayName}: ${item.docNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Upload Source: ${if (item.uploadSource == UploadSource.CAMERA) "📷 Camera Live Scan" else "📁 Device PDF Document"}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Slate400
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val sId = item.submissionId
                                previewDocumentItem = null
                                rejectSubmissionId = sId
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Disapprove")
                        }

                        Button(
                            onClick = {
                                viewModel.adminApproveVerification(item.submissionId)
                                previewDocumentItem = null
                                Toast.makeText(context, "Document Approved! Verified Shield Awarded.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve & Verify")
                        }
                    }
                }
            }
        }
    }

    if (rejectSubmissionId != null) {
        val sId = rejectSubmissionId!!
        AdminRejectReasonDialog(
            submissionId = sId,
            onDismiss = { rejectSubmissionId = null },
            onReject = { reason ->
                viewModel.adminRejectVerification(sId, reason)
                rejectSubmissionId = null
                Toast.makeText(context, "Verification Rejected with Feedback.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (revokeVendorDialog) {
        AlertDialog(
            onDismissRequest = { revokeVendorDialog = false },
            title = { Text("Revoke / Expire Verification", fontWeight = FontWeight.Bold, color = Rose600) },
            text = {
                Text("Are you sure you want to revoke Dream Decor's verified badge? This will mark all documents as expired and strip the verified green badge on customer quotations.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminRevokeVerification(vendor.id)
                        revokeVendorDialog = false
                        Toast.makeText(context, "Status Revoked. Badges Removed.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Revoke Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { revokeVendorDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (deleteUserCandidate != null) {
        val userToDelete = deleteUserCandidate!!
        AlertDialog(
            onDismissRequest = { deleteUserCandidate = null },
            title = { Text("Delete User Account", fontWeight = FontWeight.Bold, color = Rose600) },
            text = { Text("Are you sure you want to permanently delete user ${userToDelete.fullName} (${userToDelete.email})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(userToDelete.id)
                        deleteUserCandidate = null
                        Toast.makeText(context, "User deleted successfully.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Delete User")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteUserCandidate = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EventBid Admin & Operations Console", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        val file = PdfAndExcelExporter.generateAdminReportPdf(context, events, bids, users)
                        if (file != null) {
                            PdfAndExcelExporter.shareFile(context, file, "application/pdf", "EventBid Platform Audit Ledger")
                            Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Unable to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Rose600)
                    }
                    IconButton(onClick = {
                        val file = PdfAndExcelExporter.generateAdminExcel(context, events, bids, users)
                        if (file != null) {
                            PdfAndExcelExporter.shareFile(context, file, "text/csv", "EventBid Master Ledger Excel")
                            Toast.makeText(context, "Exported Excel Sheet: ${file.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Unable to generate Excel", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.TableChart, contentDescription = "Export Excel", tint = Emerald600)
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
            // Admin Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                contentColor = Indigo600,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("Audit Desk (${queue.count { it.status == VerificationStatus.UNDER_REVIEW }})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("Analytics & Graphs", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("User Governance", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 3,
                    onClick = { selectedAdminTab = 3 },
                    text = { Text("Invoices & Contracts", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedAdminTab) {
                0 -> {
                    // --- Tab 0: Verification Desk ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            // Filter Controls
                            Text("Filter Verification Submissions:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = statusFilter == null,
                                        onClick = { statusFilter = null },
                                        label = { Text("All Statuses") }
                                    )
                                }
                                items(VerificationStatus.values()) { st ->
                                    FilterChip(
                                        selected = statusFilter == st,
                                        onClick = { statusFilter = if (statusFilter == st) null else st },
                                        label = { Text(st.label) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = docTypeFilter == null,
                                        onClick = { docTypeFilter = null },
                                        label = { Text("All Document Types") }
                                    )
                                }
                                items(VerificationDocType.values()) { dt ->
                                    FilterChip(
                                        selected = docTypeFilter == dt,
                                        onClick = { docTypeFilter = if (docTypeFilter == dt) null else dt },
                                        label = { Text(dt.displayName) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        val filteredQueue = queue.filter { item ->
                            (statusFilter == null || item.status == statusFilter) &&
                            (docTypeFilter == null || item.docType == docTypeFilter)
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Indigo50,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Indigo600, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Audit Queue: ${filteredQueue.size} of ${queue.size} submissions • Avg audit turnaround: 1.8 hrs • MCA & GSTN validation enabled",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        fontWeight = FontWeight.Medium,
                                        color = Slate900
                                    )
                                }
                            }
                        }

                        items(filteredQueue) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { previewDocumentItem = item }
                                    .testTag("admin_item_${item.submissionId}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(item.vendorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            Text("Owner: ${item.ownerName} • ${item.phone}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (item.status) {
                                                VerificationStatus.VERIFIED -> Emerald100
                                                VerificationStatus.UNDER_REVIEW -> Amber100
                                                VerificationStatus.REJECTED -> Rose100
                                                else -> Slate100
                                            }
                                        ) {
                                            Text(
                                                text = item.status.label,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = when (item.status) {
                                                    VerificationStatus.VERIFIED -> Emerald600
                                                    VerificationStatus.UNDER_REVIEW -> Amber600
                                                    VerificationStatus.REJECTED -> Rose600
                                                    else -> Slate600
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Slate50,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (item.uploadSource == UploadSource.CAMERA) Icons.Default.PhotoCamera else Icons.Default.Description,
                                                    contentDescription = null,
                                                    tint = Indigo600,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("${item.docType.displayName}: ${item.docNumber}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                            }
                                            Text(if (item.uploadSource == UploadSource.CAMERA) "Camera Scan" else "PDF File", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate400)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { previewDocumentItem = item },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Inspect Document", fontSize = 11.sp)
                                        }

                                        if (item.status == VerificationStatus.UNDER_REVIEW) {
                                            Button(
                                                onClick = {
                                                    viewModel.adminApproveVerification(item.submissionId)
                                                    Toast.makeText(context, "Approved! Verified Badge Activated.", Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // --- Tab 1: Analytics & Rich Graphs ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("Marketplace Analytics & BI Desk", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("Real-time procurement metrics, GMV growth, bidding liquidity, and conversion funnel across India.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Filter 1: Period Selector
                            Text("Reporting Timeframe:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate700)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val periods = listOf("Year-to-Date (2026)", "Last 30 Days", "Last 7 Days", "Q3 2026")
                                items(periods) { p ->
                                    FilterChip(
                                        selected = analyticsPeriod == p,
                                        onClick = { analyticsPeriod = p },
                                        label = { Text(p, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Filter 2: Metric Focus
                            Text("Metric Focus Area:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate700)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val metrics = listOf("All Metrics", "Platform Revenue", "Bidding Funnel", "Category Demand", "Subscriptions")
                                items(metrics) { m ->
                                    FilterChip(
                                        selected = analyticsMetric == m,
                                        onClick = { analyticsMetric = m },
                                        label = { Text(m, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Graph 1: Platform Revenue & GMV Trend
                        if (analyticsMetric == "All Metrics" || analyticsMetric == "Platform Revenue") {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Platform Procurement GMV & Revenue", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                                Text("Total YTD Volume: ₹18.42 Lakhs", style = MaterialTheme.typography.bodySmall, color = Emerald600, fontWeight = FontWeight.Bold)
                                            }
                                            Surface(
                                                color = Emerald100,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("+34% MoM", color = Emerald600, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        val monthsData = when (analyticsPeriod) {
                                            "Last 7 Days" -> listOf("Mon" to 0.40f, "Tue" to 0.55f, "Wed" to 0.65f, "Thu" to 0.70f, "Fri" to 0.85f, "Sat" to 1.0f, "Sun" to 0.95f)
                                            "Last 30 Days" -> listOf("Week 1" to 0.50f, "Week 2" to 0.68f, "Week 3" to 0.82f, "Week 4" to 1.0f)
                                            "Q3 2026" -> listOf("Jul" to 0.65f, "Aug" to 0.80f, "Sep" to 0.95f)
                                            else -> listOf("May" to 0.35f, "Jun" to 0.48f, "Jul" to 0.62f, "Aug" to 0.76f, "Sep" to 0.88f, "Oct" to 1.0f)
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(130.dp)
                                                .background(Slate50, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            monthsData.forEach { (lbl, ratio) ->
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("₹${(ratio * 4.8f).format1Dec()}L", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = Indigo600, fontWeight = FontWeight.SemiBold)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .width(26.dp)
                                                            .fillMaxHeight(ratio * 0.75f)
                                                            .background(
                                                                if (ratio == 1.0f) Amber500 else Indigo600,
                                                                RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                                            )
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(lbl, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate600)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Surface(color = Amber50, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Caption: Peak wedding and corporate conference procurement surged in Oct 2026, driving ₹4.85 Lakhs processed across Hyderabad and Bangalore.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Amber800,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Graph 2: Bidding Conversion Funnel
                        if (analyticsMetric == "All Metrics" || analyticsMetric == "Bidding Funnel") {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Procurement Bidding & Conversion Funnel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                        Text("Stage-by-stage pipeline efficiency from customer request to locked contract.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                        Spacer(modifier = Modifier.height(14.dp))

                                        FunnelStageRow("1. Event Requirements Posted", "148 Events", 1.0f, "100%", Indigo600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        FunnelStageRow("2. Vendor Quotations Submitted", "422 Bids", 0.85f, "285% ratio", Amber600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        FunnelStageRow("3. Quotations Shortlisted", "86 Shortlisted", 0.58f, "58% rate", Emerald600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        FunnelStageRow("4. Contracts Executed & Confirmed", "62 Booked", 0.42f, "42% win", Navy900)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(color = Indigo50, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Caption: Market Liquidity: Organizers receive an average of 2.85 competitive vendor quotations within 1.6 hours. 42% of inquiries convert into signed escrow contracts.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Indigo700,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Graph 3: Category Demand Share
                        if (analyticsMetric == "All Metrics" || analyticsMetric == "Category Demand") {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Procurement Demand by Service Category", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                        Text("Share of total tender volume and gross marketplace expenditure.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                        Spacer(modifier = Modifier.height(12.dp))

                                        DemandProgressRow("Wedding Mandap & Floral Decor (₹8.84L)", 0.48f, "48%", Amber600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        DemandProgressRow("Corporate AV & LED Screens (₹4.42L)", 0.24f, "24%", Indigo600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        DemandProgressRow("Gourmet Catering & Live Food (₹3.31L)", 0.18f, "18%", Emerald600)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        DemandProgressRow("Photography & Cinematography (₹1.84L)", 0.10f, "10%", Rose600)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(color = Slate100, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Caption: High-Value Specialization: Mandap decor & corporate AV staging represent 72% of procurement spend with high ticket sizes ranging from ₹1.5L to ₹6.5L.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Slate700,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Graph 4: Vendor Subscriptions Distribution (Packages 199, 299, 399, 499, 1999)
                        if (analyticsMetric == "All Metrics" || analyticsMetric == "Subscriptions") {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Vendor Subscription Plans & MRR", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                            Surface(color = Indigo50, shape = RoundedCornerShape(4.dp)) {
                                                Text("MRR: ₹55,884", fontWeight = FontWeight.Bold, color = Indigo600, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))

                                        SubscriptionTierProgressRow("VIP Enterprise (₹1,999/mo)", 8, 15992, 0.28f, Amber600)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SubscriptionTierProgressRow("Standard Pro (₹399/mo)", 38, 15162, 0.27f, Indigo600)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SubscriptionTierProgressRow("Growth Plus (₹499/mo)", 28, 13972, 0.25f, Emerald600)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SubscriptionTierProgressRow("Basic Growth (₹299/mo)", 24, 7176, 0.13f, Slate700)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        SubscriptionTierProgressRow("Micro Starter (₹199/mo)", 18, 3582, 0.07f, Slate500)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(color = Emerald50, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Caption: Subscription Engine Health: 116 paying vendors across 5 tailored tiers (₹199 to ₹1,999). Pro (₹399) and Enterprise (₹1,999) generate 55% of recurring cash flows.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Emerald800,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Export Actions Row
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Official Export & Audit Reports", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleSmall)
                                    Text("Download signed PDF audit documentation or complete Excel raw data ledger (.csv).", color = Slate400, style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val file = PdfAndExcelExporter.generateAdminReportPdf(context, events, bids, users)
                                                if (file != null) {
                                                    PdfAndExcelExporter.shareFile(context, file, "application/pdf", "EventBid Platform Audit Ledger")
                                                    Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Export PDF Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                val file = PdfAndExcelExporter.generateAdminExcel(context, events, bids, users)
                                                if (file != null) {
                                                    PdfAndExcelExporter.shareFile(context, file, "text/csv", "EventBid Master Ledger Excel")
                                                    Toast.makeText(context, "Exported Excel Sheet: ${file.name}", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Export Excel Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // --- Tab 2: User Governance ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Registered Platform Users (${users.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Manage & Reset", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        items(users) { u ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(u.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                                if (u.isPremium) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(color = Amber100, shape = RoundedCornerShape(4.dp)) {
                                                        Text("★ Premium", color = Amber600, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                            Text("${u.role.name} • ${u.email}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                            Text(u.phone, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate400)
                                        }

                                        IconButton(onClick = { deleteUserCandidate = u }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose600)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.toggleUserPremium(u.id)
                                                Toast.makeText(context, "Toggled Premium for ${u.fullName}", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(if (u.isPremium) "Revoke VIP" else "Make VIP", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                viewModel.resetUserSession(u.id)
                                                Toast.makeText(context, "Session reset for ${u.fullName}", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reset Session", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // --- Tab 3: Invoices & Contracts ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Official Invoices & Service Agreements", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("GST compliance documents, tax invoices, and locked bilateral booking contracts.", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Document Type Filters
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val docFilters = listOf(
                                    "ALL" to "All Records",
                                    "CUSTOMER" to "Customer Invoices",
                                    "VENDOR" to "Vendor Subscriptions",
                                    "AGREEMENT" to "Service Contracts"
                                )
                                items(docFilters) { (key, label) ->
                                    FilterChip(
                                        selected = invoiceTypeFilter == key,
                                        onClick = { invoiceTypeFilter = key },
                                        label = { Text(label, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Informative Caption Banner
                            val totalTax = invoices.sumOf { it.cgstAmount + it.sgstAmount }
                            val totalContractVal = agreements.sumOf { it.totalContractValue }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Indigo50,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Ledger: ${invoices.size} Tax Invoices • ${agreements.size} Bilateral Contracts • ₹$totalTax GST Deposited",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                        Text(
                                            text = "All documents generated with 18% GST (CGST 9% + SGST 9%) and legal arbitration lock under Indian IT Act 2000.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = Slate600
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Agreements Section
                        if (invoiceTypeFilter == "ALL" || invoiceTypeFilter == "AGREEMENT") {
                            agreements.forEach { ag ->
                                item {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { previewAgreement = ag }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Amber100,
                                                    modifier = Modifier.size(40.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.Handshake, contentDescription = null, tint = Amber600, modifier = Modifier.size(22.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(ag.agreementNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Surface(color = Emerald100, shape = RoundedCornerShape(4.dp)) {
                                                            Text("Contract", color = Emerald600, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                        }
                                                    }
                                                    Text("${ag.customerName} ↔ ${ag.vendorName}", style = MaterialTheme.typography.bodySmall, color = Slate700, fontWeight = FontWeight.SemiBold)
                                                    Text("Value: ₹${ag.totalContractValue} • ${ag.eventTitle}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate500, maxLines = 1)
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = {
                                                    val file = PdfAndExcelExporter.generateAgreementPdf(context, ag)
                                                    if (file != null) {
                                                        PdfAndExcelExporter.shareFile(context, file, "application/pdf", "Agreement ${ag.agreementNumber}")
                                                        Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }) {
                                                    Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Rose600, modifier = Modifier.size(20.dp))
                                                }
                                                IconButton(onClick = {
                                                    val file = PdfAndExcelExporter.generateAgreementExcel(context, ag)
                                                    if (file != null) {
                                                        PdfAndExcelExporter.shareFile(context, file, "text/csv", "Agreement ${ag.agreementNumber}")
                                                        Toast.makeText(context, "Exported Excel: ${file.name}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }) {
                                                    Icon(Icons.Default.TableChart, contentDescription = "Excel", tint = Emerald600, modifier = Modifier.size(20.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Invoices Section
                        val filteredInvoices = when (invoiceTypeFilter) {
                            "CUSTOMER" -> invoices.filter { it.type == "CUSTOMER_BOOKING" || it.type == "TICKET_SALE" }
                            "VENDOR" -> invoices.filter { it.type == "VENDOR_SUBSCRIPTION" || it.type == "CAMPAIGN_FEE" }
                            "AGREEMENT" -> emptyList()
                            else -> invoices
                        }

                        filteredInvoices.forEach { inv ->
                            item {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { previewInvoice = inv }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Indigo50,
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Indigo600, modifier = Modifier.size(22.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        color = if (inv.type.startsWith("VENDOR")) Amber100 else Indigo50,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = if (inv.type.startsWith("VENDOR")) "Subscription" else "Booking",
                                                            color = if (inv.type.startsWith("VENDOR")) Amber700 else Indigo600,
                                                            fontWeight = FontWeight.Bold,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Text("Billed To: ${inv.billedToName}", style = MaterialTheme.typography.bodySmall, color = Slate700, fontWeight = FontWeight.SemiBold)
                                                Text("Amount: ₹${inv.totalAmount} (incl. 18% GST) • ${inv.issueDate}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate500)
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                val file = PdfAndExcelExporter.generateInvoicePdf(context, inv)
                                                if (file != null) {
                                                    PdfAndExcelExporter.shareFile(context, file, "application/pdf", "Invoice ${inv.invoiceNumber}")
                                                    Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                                                }
                                            }) {
                                                Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Rose600, modifier = Modifier.size(20.dp))
                                            }
                                            IconButton(onClick = {
                                                val file = PdfAndExcelExporter.generateInvoiceExcel(context, inv)
                                                if (file != null) {
                                                    PdfAndExcelExporter.shareFile(context, file, "text/csv", "Invoice ${inv.invoiceNumber}")
                                                    Toast.makeText(context, "Exported Excel: ${file.name}", Toast.LENGTH_SHORT).show()
                                                }
                                            }) {
                                                Icon(Icons.Default.TableChart, contentDescription = "Excel", tint = Emerald600, modifier = Modifier.size(20.dp))
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
    }
}

@Composable
fun FunnelStageRow(stage: String, countText: String, progress: Float, percentText: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(stage, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Slate900)
                Text(countText, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate600)
            }
            Text(percentText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Slate100,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
fun SubscriptionTierProgressRow(tierName: String, vendorCount: Int, revenue: Long, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$tierName • $vendorCount vendors", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Slate800)
            Text("₹$revenue", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Slate100,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
        )
    }
}

fun Float.format1Dec(): String {
    return String.format(java.util.Locale.ENGLISH, "%.1f", this)
}

@Composable
fun DemandProgressRow(label: String, progress: Float, percentText: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(percentText, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Slate100,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
fun AdminRejectReasonDialog(
    submissionId: String,
    onDismiss: () -> Unit,
    onReject: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("GST number mismatch with registered business legal entity name") }

    val presetReasons = listOf(
        "GST number mismatch with registered business legal entity name",
        "Document scan resolution illegible or watermark obscured",
        "Expired trade license / renewal receipt needed",
        "Authorized signatory proof missing or incomplete"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Disapprove Verification Document",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Rose600
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select compliance reason to provide feedback to the vendor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                presetReasons.forEach { r ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == r,
                            onClick = { selectedReason = r }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(r, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onReject(selectedReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm Disapprove")
                    }
                }
            }
        }
    }
}

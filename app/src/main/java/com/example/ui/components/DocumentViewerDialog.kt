package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.PdfAndExcelExporter
import com.example.model.EventServiceAgreement
import com.example.model.PlatformInvoice
import com.example.ui.theme.*

@Composable
fun InvoicePreviewDialog(
    invoice: PlatformInvoice,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Indigo50,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = Indigo600, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Official Tax Invoice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text(invoice.invoiceNumber, style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Brand Header Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_eventbid_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("EventBid India", color = Amber500, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                            Surface(color = Emerald600.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text(invoice.status, color = Emerald500, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("GSTIN: 36AAACE1234F1Z8 • SAC: 998599 (Event Procurement Services)", color = Slate400, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Billed To Details
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("BILLED TO:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate600)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(invoice.billedToName, fontWeight = FontWeight.Bold, color = Slate900, style = MaterialTheme.typography.titleSmall)
                        Text("${invoice.billedToEmail} • ${invoice.billedToCity}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        if (invoice.billedToGstin != null) {
                            Text("GSTIN: ${invoice.billedToGstin}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Indigo600, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Date: ${invoice.issueDate}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate600)
                            Text("Ref: ${invoice.referenceId}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Line Items
                Text("Itemized Scope Breakdown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))

                invoice.items.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate50,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = Slate900)
                                Text("SAC: ${item.hsnSac} • Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate400)
                            }
                            Text("₹${item.amount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Slate900)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Totals & Tax Calculation Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Indigo50.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxable Subtotal", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${invoice.subTotal}", style = MaterialTheme.typography.bodySmall, color = Slate900, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CGST (9%)", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${invoice.cgstAmount}", style = MaterialTheme.typography.bodySmall, color = Slate900)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SGST (9%)", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${invoice.sgstAmount}", style = MaterialTheme.typography.bodySmall, color = Slate900)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid Amount", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            Text("₹${invoice.totalAmount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Indigo600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val file = PdfAndExcelExporter.generateInvoicePdf(context, invoice)
                            if (file != null) {
                                PdfAndExcelExporter.shareFile(context, file, "application/pdf", "Invoice ${invoice.invoiceNumber}")
                                Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val file = PdfAndExcelExporter.generateInvoiceExcel(context, invoice)
                            if (file != null) {
                                PdfAndExcelExporter.shareFile(context, file, "text/csv", "Invoice ${invoice.invoiceNumber} Excel")
                                Toast.makeText(context, "Exported Excel Sheet: ${file.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AgreementPreviewDialog(
    agreement: EventServiceAgreement,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Amber100,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Handshake, contentDescription = null, tint = Amber600, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Event Service Agreement", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Slate900)
                            Text("Contract: ${agreement.agreementNumber}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legal Header Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LEGAL BILATERAL CONTRACT", color = Amber500, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Surface(color = Emerald600, shape = RoundedCornerShape(4.dp)) {
                                Text(agreement.status, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(agreement.eventTitle, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Executed on ${agreement.executionDate} • Enforced by EventBid Escrow", color = Slate400, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Parties Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CONTRACTING PARTIES:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate600)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Client: ${agreement.customerName} (${agreement.customerPhone})", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = Slate900)
                        Text("Vendor: ${agreement.vendorName} • Proprietor: ${agreement.vendorOwner}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = Slate900)
                        Text("GSTIN: ${agreement.vendorGstin} • Venue: ${agreement.eventVenue}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        Text("Event Date: ${agreement.eventDate}", style = MaterialTheme.typography.bodySmall, color = Indigo600, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scope Deliverables
                Text("Contracted Deliverables & Inclusions:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = Slate900)
                Spacer(modifier = Modifier.height(6.dp))

                agreement.servicesScope.forEach { s ->
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(15.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(s, style = MaterialTheme.typography.bodySmall, color = Slate700)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Consideration
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Agreed Contract Value", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${agreement.totalContractValue}", fontWeight = FontWeight.Bold, color = Indigo600, style = MaterialTheme.typography.titleSmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Advance Paid (Escrow Locked)", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${agreement.advancePaid}", fontWeight = FontWeight.Bold, color = Emerald600, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Remaining Balance Due", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text("₹${agreement.remainingBalance}", fontWeight = FontWeight.Bold, color = Slate900, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Milestones
                Text("Milestone Schedule:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = Slate600)
                agreement.milestones.forEach { m ->
                    Text("• $m", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Slate700, modifier = Modifier.padding(vertical = 1.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Terms & Arbitration
                Text("Cancellation Terms:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = Slate600)
                Text(agreement.cancellationTerms, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate600)

                Spacer(modifier = Modifier.height(6.dp))
                Text("Arbitration Clause:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = Slate600)
                Text(agreement.disputeClause, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = Slate600)

                Spacer(modifier = Modifier.height(16.dp))

                // Export Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val file = PdfAndExcelExporter.generateAgreementPdf(context, agreement)
                            if (file != null) {
                                PdfAndExcelExporter.shareFile(context, file, "application/pdf", "Agreement ${agreement.agreementNumber}")
                                Toast.makeText(context, "Exported PDF: ${file.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val file = PdfAndExcelExporter.generateAgreementExcel(context, agreement)
                            if (file != null) {
                                PdfAndExcelExporter.shareFile(context, file, "text/csv", "Agreement ${agreement.agreementNumber} Excel")
                                Toast.makeText(context, "Exported Excel Sheet: ${file.name}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Excel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

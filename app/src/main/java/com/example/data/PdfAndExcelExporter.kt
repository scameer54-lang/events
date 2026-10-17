package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfAndExcelExporter {

    private const val A4_WIDTH = 595
    private const val A4_HEIGHT = 842

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "$title generated from EventBid India.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share $title via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Saved to ${file.name}: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // 1. Export Invoice as Formatted PDF
    fun generateInvoicePdf(context: Context, invoice: PlatformInvoice): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var y = 40f

        // Brand Banner
        paint.color = Color.rgb(15, 23, 42) // Navy 900
        canvas.drawRect(0f, 0f, A4_WIDTH.toFloat(), 90f, paint)

        // Brand Title
        paint.color = Color.rgb(245, 158, 11) // Amber 500
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EventBid India", 30f, 42f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("E-Procurement & Bidding Marketplace Platform", 30f, 58f, paint)
        canvas.drawText("GSTIN: 36AAACE1234F1Z8 • HSN/SAC: 998599 • Govt. of India Tax Invoice", 30f, 72f, paint)

        // Invoice Title & Status Badge
        y = 120f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TAX INVOICE / RECEIPT", 30f, y, paint)

        paint.color = Color.rgb(5, 150, 105) // Emerald
        paint.textSize = 12f
        canvas.drawText("[ ${invoice.status} ]", A4_WIDTH - 110f, y, paint)

        // Invoice Meta Block
        y += 24f
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Invoice Number: ${invoice.invoiceNumber}", 30f, y, paint)
        canvas.drawText("Issue Date: ${invoice.issueDate}", 220f, y, paint)
        canvas.drawText("Payment Mode: ${invoice.paymentMode}", 390f, y, paint)

        y += 16f
        canvas.drawText("Reference ID: ${invoice.referenceId}", 30f, y, paint)
        canvas.drawText("Due Date: ${invoice.dueDate}", 220f, y, paint)

        // Billed To Box
        y += 25f
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(30f, y, A4_WIDTH - 30f, y + 65f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("BILLED TO / CUSTOMER DETAILS:", 42f, y + 18f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9.5f
        canvas.drawText("Name: ${invoice.billedToName} • Email: ${invoice.billedToEmail}", 42f, y + 34f, paint)
        canvas.drawText("City: ${invoice.billedToCity} • Legal GSTIN: ${invoice.billedToGstin ?: "Consumer B2C"}", 42f, y + 48f, paint)

        // Table Header
        y += 85f
        paint.color = Color.rgb(37, 99, 235) // Indigo
        canvas.drawRect(30f, y, A4_WIDTH - 30f, y + 24f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DESCRIPTION & SERVICE SCOPE", 40f, y + 16f, paint)
        canvas.drawText("SAC", 320f, y + 16f, paint)
        canvas.drawText("QTY", 370f, y + 16f, paint)
        canvas.drawText("RATE (INR)", 420f, y + 16f, paint)
        canvas.drawText("TOTAL (INR)", 495f, y + 16f, paint)

        // Line Items
        y += 24f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9.5f

        invoice.items.forEachIndexed { index, item ->
            val rowY = y + (index * 24f)
            paint.color = if (index % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            canvas.drawRect(30f, rowY, A4_WIDTH - 30f, rowY + 24f, paint)

            paint.color = Color.rgb(30, 41, 59)
            val desc = if (item.description.length > 45) item.description.substring(0, 42) + "..." else item.description
            canvas.drawText(desc, 40f, rowY + 16f, paint)
            canvas.drawText(item.hsnSac, 320f, rowY + 16f, paint)
            canvas.drawText(item.quantity.toString(), 375f, rowY + 16f, paint)
            canvas.drawText("₹" + item.unitRate, 420f, rowY + 16f, paint)
            canvas.drawText("₹" + item.amount, 495f, rowY + 16f, paint)
        }

        // Summary Breakdown Box
        y += (invoice.items.size * 24f) + 20f
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRect(300f, y, A4_WIDTH - 30f, y + 90f, paint)

        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 9.5f
        canvas.drawText("Taxable Subtotal:", 315f, y + 18f, paint)
        canvas.drawText("₹${invoice.subTotal}", 480f, y + 18f, paint)

        canvas.drawText("Central GST (CGST @ 9%):", 315f, y + 36f, paint)
        canvas.drawText("₹${invoice.cgstAmount}", 480f, y + 36f, paint)

        canvas.drawText("State GST (SGST @ 9%):", 315f, y + 54f, paint)
        canvas.drawText("₹${invoice.sgstAmount}", 480f, y + 54f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        canvas.drawText("TOTAL PAYABLE:", 315f, y + 76f, paint)
        canvas.drawText("₹${invoice.totalAmount}", 480f, y + 76f, paint)

        // Footer / Stamp
        y = A4_HEIGHT - 100f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawLine(30f, y, A4_WIDTH - 30f, y, paint)

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Terms & Conditions: This document is an electronic tax invoice under Section 31 of CGST Act, 2017.", 30f, y + 18f, paint)
        canvas.drawText("All disputes are subject to the exclusive jurisdiction of courts in Hyderabad / Bangalore, India.", 30f, y + 30f, paint)
        canvas.drawText("EventBid Platforms Private Limited • Corporate Identity Number (CIN): U72900TG2026PTC198421", 30f, y + 42f, paint)

        pdfDocument.finishPage(page)

        return savePdfToFile(context, pdfDocument, "Invoice_${invoice.invoiceNumber}.pdf")
    }

    // 2. Export Legal Agreement / Contract as PDF
    fun generateAgreementPdf(context: Context, agreement: EventServiceAgreement): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header Banner
        paint.color = Color.rgb(15, 23, 42)
        canvas.drawRect(0f, 0f, A4_WIDTH.toFloat(), 85f, paint)

        paint.color = Color.rgb(245, 158, 11)
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EventBid Procurement Legal Contract", 30f, 38f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Bilateral Event Service & Fulfillment Agreement • Contract Ref: ${agreement.agreementNumber}", 30f, 54f, paint)
        canvas.drawText("Status: ${agreement.status} • Executed on: ${agreement.executionDate}", 30f, 68f, paint)

        var y = 115f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. CONTRACTING PARTIES & APPOINTMENT", 30f, y, paint)

        y += 18f
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(51, 65, 85)
        canvas.drawText("Client / Event Organizer: ${agreement.customerName} (Phone: ${agreement.customerPhone})", 30f, y, paint)
        y += 14f
        canvas.drawText("Service Provider / Vendor: ${agreement.vendorName} (Proprietor: ${agreement.vendorOwner})", 30f, y, paint)
        y += 14f
        canvas.drawText("Vendor Tax Registration: GSTIN ${agreement.vendorGstin}", 30f, y, paint)

        y += 24f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. EVENT DETAILS & SCOPE OF SERVICES", 30f, y, paint)

        y += 18f
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(51, 65, 85)
        canvas.drawText("Event Title: ${agreement.eventTitle}", 30f, y, paint)
        y += 14f
        canvas.drawText("Date & Venue: ${agreement.eventDate} at ${agreement.eventVenue}", 30f, y, paint)
        y += 16f
        canvas.drawText("Agreed Scope of Deliverables:", 30f, y, paint)

        agreement.servicesScope.forEach { s ->
            y += 14f
            canvas.drawText("• $s", 45f, y, paint)
        }

        y += 24f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. FINANCIAL CONSIDERATION & PAYMENT MILESTONES", 30f, y, paint)

        y += 18f
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(51, 65, 85)
        canvas.drawText("Total Agreed Contract Value: ₹${agreement.totalContractValue}", 30f, y, paint)
        y += 14f
        canvas.drawText("Initial Advance Deposit Received: ₹${agreement.advancePaid} (Secured via EventBid Escrow)", 30f, y, paint)
        y += 14f
        canvas.drawText("Balance Due on Completion: ₹${agreement.remainingBalance}", 30f, y, paint)

        y += 16f
        canvas.drawText("Milestone Schedule:", 30f, y, paint)
        agreement.milestones.forEach { m ->
            y += 14f
            canvas.drawText("→ $m", 45f, y, paint)
        }

        y += 24f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("4. CANCELLATION & DISPUTE ARBITRATION", 30f, y, paint)

        y += 18f
        paint.textSize = 8.5f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Cancellation: ${agreement.cancellationTerms}", 30f, y, paint)
        y += 14f
        canvas.drawText("Dispute Resolution: ${agreement.disputeClause}", 30f, y, paint)

        // Signatures Box
        y += 30f
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(30f, y, A4_WIDTH - 30f, y + 60f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DIGITALLY SIGNED & VERIFIED BY EVENTBID ESCROW", 40f, y + 20f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 8.5f
        paint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Client Acceptance Stamp: ${agreement.customerName} (IP Locked)", 40f, y + 36f, paint)
        canvas.drawText("Vendor Acceptance Stamp: ${agreement.vendorName} (GST Verified)", 40f, y + 48f, paint)

        pdfDocument.finishPage(page)
        return savePdfToFile(context, pdfDocument, "Agreement_${agreement.agreementNumber}.pdf")
    }

    // 3. Export Admin Master Report PDF
    fun generateAdminReportPdf(
        context: Context,
        events: List<EventItem>,
        bids: List<BidItem>,
        users: List<AppUser>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header
        paint.color = Color.rgb(15, 23, 42)
        canvas.drawRect(0f, 0f, A4_WIDTH.toFloat(), 80f, paint)

        paint.color = Color.rgb(245, 158, 11)
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EventBid India — Platform Audit Report", 30f, 38f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH).format(Date())
        canvas.drawText("Generated on: $dateStr • Confidential Platform Audit Ledger", 30f, 56f, paint)

        var y = 110f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Executive Platform Summary", 30f, y, paint)

        y += 20f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Total Requirements Posted: ${events.size}", 30f, y, paint)
        canvas.drawText("Total Vendor Quotation Bids: ${bids.size}", 200f, y, paint)
        canvas.drawText("Registered Platform Users: ${users.size}", 380f, y, paint)

        y += 18f
        val totalBudget = events.sumOf { it.estimatedBudget }
        canvas.drawText("Total Procurement GMV: ₹$totalBudget", 30f, y, paint)
        canvas.drawText("Active Bids Shortlisted: ${bids.count { it.status == BidStatus.SHORTLISTED }}", 200f, y, paint)
        canvas.drawText("Confirmed Bookings: ${events.count { it.status == EventStatus.BOOKED }}", 380f, y, paint)

        // Events Table Header
        y += 35f
        paint.color = Color.rgb(37, 99, 235)
        canvas.drawRect(30f, y, A4_WIDTH - 30f, y + 22f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EVENT ID", 38f, y + 15f, paint)
        canvas.drawText("TITLE & CATEGORY", 110f, y + 15f, paint)
        canvas.drawText("CITY", 290f, y + 15f, paint)
        canvas.drawText("BUDGET (INR)", 360f, y + 15f, paint)
        canvas.drawText("STATUS", 460f, y + 15f, paint)

        y += 22f
        paint.typeface = Typeface.DEFAULT

        events.take(15).forEachIndexed { index, ev ->
            val rowY = y + (index * 22f)
            paint.color = if (index % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            canvas.drawRect(30f, rowY, A4_WIDTH - 30f, rowY + 22f, paint)

            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(ev.id, 38f, rowY + 15f, paint)
            val title = if (ev.title.length > 25) ev.title.substring(0, 23) + "..." else ev.title
            canvas.drawText(title, 110f, rowY + 15f, paint)
            canvas.drawText(ev.city, 290f, rowY + 15f, paint)
            canvas.drawText("₹" + ev.estimatedBudget, 360f, rowY + 15f, paint)
            canvas.drawText(ev.status.label, 460f, rowY + 15f, paint)
        }

        pdfDocument.finishPage(page)
        return savePdfToFile(context, pdfDocument, "EventBid_Audit_Report_${System.currentTimeMillis() % 10000}.pdf")
    }

    // 4. Export Excel Spreadsheet (.csv formatted with UTF-8 BOM)
    fun generateAdminExcel(
        context: Context,
        events: List<EventItem>,
        bids: List<BidItem>,
        users: List<AppUser>
    ): File? {
        val fileName = "EventBid_Ledger_Export_${System.currentTimeMillis() % 10000}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                // Write UTF-8 BOM so Excel opens Hindi/Special chars cleanly
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                val sb = StringBuilder()
                sb.append("=== EVENTBID INDIA E-PROCUREMENT PLATFORM MASTER LEDGER ===\n\n")

                // Section 1: Events
                sb.append("EVENT REQUIREMENTS & TENDERS\n")
                sb.append("Event ID,Title,Type,City,Venue,Event Date,Guest Count,Budget (INR),Bids Count,Status,Customer Name,Customer Phone\n")
                events.forEach { e ->
                    sb.append("\"${e.id}\",\"${escapeCsv(e.title)}\",\"${e.eventType.displayName}\",\"${e.city}\",\"${escapeCsv(e.venueArea)}\",\"${e.eventDate}\",${e.guestCount},${e.estimatedBudget},${e.bidsCount},\"${e.status.label}\",\"${escapeCsv(e.customerName)}\",\"${e.customerPhone}\"\n")
                }

                sb.append("\n=== VENDOR QUOTATION BIDS ===\n")
                sb.append("Bid ID,Event ID,Vendor Name,Rating,Price (INR),Validity Days,Status,Response Time,Verified\n")
                bids.forEach { b ->
                    sb.append("\"${b.id}\",\"${b.eventId}\",\"${escapeCsv(b.vendorName)}\",${b.vendorRating},${b.price},${b.validityDays},\"${b.status.label}\",\"${b.responseTime}\",${b.isVerified}\n")
                }

                sb.append("\n=== REGISTERED PLATFORM USERS ===\n")
                sb.append("User ID,Full Name,Email,Phone,Role,VIP Status,Joined Date\n")
                users.forEach { u ->
                    sb.append("\"${u.id}\",\"${escapeCsv(u.fullName)}\",\"${u.email}\",\"${u.phone}\",\"${u.role.name}\",${u.isPremium},\"${u.joinedDate}\"\n")
                }

                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // 5. Export Invoices to Excel
    fun generateInvoiceExcel(context: Context, invoice: PlatformInvoice): File? {
        val fileName = "Invoice_${invoice.invoiceNumber}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                val sb = StringBuilder()
                sb.append("TAX INVOICE - EVENTBID INDIA\n")
                sb.append("Invoice Number,${invoice.invoiceNumber}\n")
                sb.append("Issue Date,${invoice.issueDate}\n")
                sb.append("Due Date,${invoice.dueDate}\n")
                sb.append("Billed To,${invoice.billedToName}\n")
                sb.append("Billed Email,${invoice.billedToEmail}\n")
                sb.append("Billed City,${invoice.billedToCity}\n")
                sb.append("GSTIN,${invoice.billedToGstin ?: "Consumer B2C"}\n\n")

                sb.append("Line Item,SAC Code,Quantity,Unit Rate (INR),Amount (INR)\n")
                invoice.items.forEach { item ->
                    sb.append("\"${escapeCsv(item.description)}\",${item.hsnSac},${item.quantity},${item.unitRate},${item.amount}\n")
                }

                sb.append("\nSubtotal,${invoice.subTotal}\n")
                sb.append("CGST (9%),${invoice.cgstAmount}\n")
                sb.append("SGST (9%),${invoice.sgstAmount}\n")
                sb.append("Total Amount,${invoice.totalAmount}\n")
                sb.append("Payment Mode,${invoice.paymentMode}\n")
                sb.append("Reference,${invoice.referenceId}\n")
                sb.append("Status,${invoice.status}\n")

                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            return file
        } catch (e: Exception) {
            return null
        }
    }

    // 6. Export Agreement to Excel
    fun generateAgreementExcel(context: Context, agreement: EventServiceAgreement): File? {
        val fileName = "Agreement_${agreement.agreementNumber}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                val sb = StringBuilder()
                sb.append("EVENT SERVICE AGREEMENT - EVENTBID PROCUREMENT\n")
                sb.append("Agreement Number,${agreement.agreementNumber}\n")
                sb.append("Event ID,${agreement.eventId}\n")
                sb.append("Event Title,\"${escapeCsv(agreement.eventTitle)}\"\n")
                sb.append("Customer Name,${agreement.customerName}\n")
                sb.append("Customer Phone,${agreement.customerPhone}\n")
                sb.append("Vendor Business,${agreement.vendorName}\n")
                sb.append("Vendor Owner,${agreement.vendorOwner}\n")
                sb.append("Vendor GSTIN,${agreement.vendorGstin}\n")
                sb.append("Event Date,${agreement.eventDate}\n")
                sb.append("Event Venue,\"${escapeCsv(agreement.eventVenue)}\"\n")
                sb.append("Contract Value (INR),${agreement.totalContractValue}\n")
                sb.append("Advance Paid (INR),${agreement.advancePaid}\n")
                sb.append("Balance Due (INR),${agreement.remainingBalance}\n")
                sb.append("Status,${agreement.status}\n")
                sb.append("Execution Date,${agreement.executionDate}\n\n")

                sb.append("Scope Deliverables\n")
                agreement.servicesScope.forEach { sb.append("\"${escapeCsv(it)}\"\n") }

                sb.append("\nMilestones\n")
                agreement.milestones.forEach { sb.append("\"${escapeCsv(it)}\"\n") }

                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            return file
        } catch (e: Exception) {
            return null
        }
    }

    private fun savePdfToFile(context: Context, pdfDocument: PdfDocument, fileName: String): File? {
        val file = File(context.cacheDir, fileName)
        return try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    private fun escapeCsv(str: String): String {
        return str.replace("\"", "\"\"")
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.EventBidRepository
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies EventBid name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("EventBid", appName)
    }

    @Test
    fun `test vendor verification approval workflow`() {
        val repo = EventBidRepository()
        
        // Upload a new document
        repo.uploadVendorDocument(
            docType = VerificationDocType.TRADE_LICENSE,
            docNumber = "BBMP-2026-TEST",
            fileName = "trade_license.pdf"
        )
        val queue = repo.adminVerificationQueue.value
        val pending = queue.firstOrNull { it.docNumber == "BBMP-2026-TEST" }
        assertNotNull(pending)
        assertEquals(VerificationStatus.UNDER_REVIEW, pending!!.status)

        // Admin approves document
        repo.approveVerification(pending.submissionId)

        val updatedQueue = repo.adminVerificationQueue.value
        val approved = updatedQueue.first { it.submissionId == pending.submissionId }
        assertEquals(VerificationStatus.VERIFIED, approved.status)

        // Vendor profile should have VERIFIED trust badge
        val vendor = repo.vendorProfile.value
        assertTrue(vendor.isVerified)
        assertTrue(vendor.trustBadges.contains(TrustBadge.VERIFIED))
    }

    @Test
    fun `test vendor quota decrement upon bid submission`() {
        val repo = EventBidRepository()
        val initialBidsQuota = repo.vendorSubscription.value.remainingBids
        assertTrue(initialBidsQuota > 0)

        val success = repo.submitBid(
            eventId = "EVT-1002",
            price = 24000L,
            inclusions = listOf("Decorations", "Sound"),
            exclusions = emptyList(),
            validityDays = 7,
            deliveryNotes = "Early morning setup"
        )

        assertTrue(success)
        assertEquals(initialBidsQuota - 1, repo.vendorSubscription.value.remainingBids)

        // Check bid created
        val bids = repo.bids.value
        val latestBid = bids.find { it.eventId == "EVT-1002" && it.price == 24000L }
        assertNotNull(latestBid)
    }

    @Test
    fun `test real time chat message exchange between customer and vendor`() {
        val repo = EventBidRepository()
        val initialCount = repo.chatMessages.value.size

        repo.switchRole(UserRole.CUSTOMER)
        repo.sendMessage(
            eventId = "EVT-1001",
            eventTitle = "Wedding Decoration",
            text = "Can you provide live drone videography?"
        )

        val updatedMessages = repo.chatMessages.value
        assertEquals(initialCount + 1, updatedMessages.size)
        val sent = updatedMessages.last()
        assertEquals("Can you provide live drone videography?", sent.text)
        assertTrue(sent.isFromCustomer)
    }
}

package com.example.model

import com.example.R

enum class UserRole {
    CUSTOMER,
    VENDOR,
    ADMIN,
    OPERATIONS
}

enum class EventType(val displayName: String, val iconName: String) {
    WEDDING("Wedding & Engagement", "favorite"),
    BIRTHDAY("Birthday Celebration", "cake"),
    CORPORATE("Corporate Conference", "business_center"),
    CATERING("Catering & Food", "restaurant"),
    DECORATION("Decoration & Floral", "palette"),
    PHOTOGRAPHY("Photography & Video", "photo_camera"),
    DJ_SOUND("DJ, Sound & Stage", "speaker"),
    VENUE_RENTAL("Venue & Furniture", "apartment"),
    FULL_MANAGEMENT("Full Event Package", "stars")
}

enum class EventStatus(val label: String) {
    PENDING_APPROVAL("Pending Approval"),
    BIDDING_OPEN("Bidding Open"),
    SHORTLISTED("Shortlisted"),
    BOOKED("Booked & Confirmed"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class EventItem(
    val id: String,
    val title: String,
    val eventType: EventType,
    val city: String,
    val venueArea: String,
    val eventDate: String,
    val guestCount: Int,
    val estimatedBudget: Long, // in INR
    val servicesRequired: List<String>,
    val description: String,
    val biddingDeadline: String,
    val status: EventStatus,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val createdAt: String,
    val bidsCount: Int = 0,
    val imageResId: Int? = null
)

enum class BidStatus(val label: String) {
    SUBMITTED("Submitted"),
    SHORTLISTED("Shortlisted"),
    ACCEPTED("Accepted / Won"),
    REJECTED("Not Selected")
}

data class BidItem(
    val id: String,
    val eventId: String,
    val vendorId: String,
    val vendorName: String,
    val vendorRating: Float,
    val vendorReviewCount: Int,
    val price: Long, // in INR
    val inclusions: List<String>,
    val exclusions: List<String>,
    val validityDays: Int,
    val deliveryNotes: String,
    val submittedAt: String,
    val status: BidStatus,
    val isVerified: Boolean = true,
    val isTopRated: Boolean = false,
    val isCustomerRecommended: Boolean = false,
    val responseTime: String = "< 2 hrs"
)

enum class TrustBadge(val title: String, val subtitle: String, val description: String) {
    VERIFIED(
        "Verified Vendor",
        "Govt & Business Verified",
        "Official GSTIN, PAN, and Trade Registration documents have been verified with Ministry of Corporate Affairs / GSTN records."
    ),
    TOP_RATED(
        "Top Rated",
        "4.8+ Customer Rating",
        "Consistently holds a 4.8+ rating across at least 15 completed verified bookings on EventBid."
    ),
    CUSTOMER_RECOMMENDED(
        "Customer Recommended",
        "98% Satisfaction",
        "Achieved a 98% re-hire or recommendation score with zero unresolved customer complaints."
    )
}

enum class VerificationDocType(val displayName: String, val requiredFor: String) {
    GSTIN("GSTIN Certificate", "Tax & Commercial Legitimacy"),
    UDYAM_MSME("MSME / Udyam Certificate", "Registered Enterprise Verification"),
    PAN_CARD("Business PAN Card", "Entity Financial Identity"),
    TRADE_LICENSE("Trade License / FSSAI", "Local Municipality & Health Compliance"),
    CANCELLED_CHEQUE("Bank Passbook / Cheque", "Secure Payouts & Refund Guarantee")
}

enum class VerificationStatus(val label: String) {
    NOT_SUBMITTED("Not Submitted"),
    UNDER_REVIEW("Under Review"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    EXPIRED("Expired")
}

enum class UploadSource(val label: String) {
    FILE("Device Document / PDF"),
    CAMERA("Camera Live Scan")
}

data class VerificationDocument(
    val docType: VerificationDocType,
    val docNumber: String,
    val fileName: String,
    val uploadedAt: String,
    val status: VerificationStatus,
    val uploadSource: UploadSource = UploadSource.FILE,
    val verifiedAt: String? = null,
    val rejectionReason: String? = null,
    val expiryDate: String? = null,
    val previewImageRes: Int? = R.drawable.ic_eventbid_logo
)

data class VendorProfile(
    val id: String,
    val businessName: String,
    val businessType: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val primaryCity: String,
    val operatingCities: List<String>,
    val categories: List<String>,
    val aboutText: String,
    val rating: Float,
    val reviewCount: Int,
    val completedEventsCount: Int,
    val responseTime: String,
    val verificationDocuments: List<VerificationDocument>,
    val trustBadges: Set<TrustBadge>,
    val isVerified: Boolean = true,
    val isTopRated: Boolean = false,
    val isCustomerRecommended: Boolean = false,
    val memberSince: String = "2024"
)

// Requested Packages: 199, 299, 399, 499, 1999
enum class SubscriptionTier(
    val planName: String,
    val monthlyPriceInr: Long,
    val maxOpportunityViews: Int,
    val maxBids: Int,
    val profileType: String,
    val perks: List<String>
) {
    PACKAGE_199(
        "Micro Starter",
        199,
        5,
        2,
        "Freelancer / Solo Vendor",
        listOf("5 event opportunity views / mo", "2 quotation bid submissions", "Direct customer chat", "Basic profile in directory")
    ),
    PACKAGE_299(
        "Basic Growth",
        299,
        15,
        6,
        "Rising Business",
        listOf("15 event opportunity views / mo", "6 quotation bid submissions", "SMS & In-app lead alerts", "Standard lead tracking")
    ),
    PACKAGE_399(
        "Standard Pro",
        399,
        25,
        12,
        "Professional Decorator",
        listOf("25 event opportunity views / mo", "12 quotation bid submissions", "Verified vendor badge eligibility", "Priority notification alerts")
    ),
    PACKAGE_499(
        "Growth Plus",
        499,
        50,
        20,
        "Premier Event Agency",
        listOf("50 event opportunity views / mo", "20 quotation bid submissions", "Enhanced profile placement", "10% campaign discount", "Dedicated RM")
    ),
    PACKAGE_1999(
        "VIP Enterprise",
        1999,
        200,
        70,
        "Corporate & High-Budget VIP",
        listOf("200 event opportunity views / mo", "70 quotation bid submissions", "Featured top-row placement", "25% campaign discount", "24/7 Relationship Manager", "Priority Dispute Resolution")
    )
}

data class VendorSubscription(
    val tier: SubscriptionTier,
    val remainingViews: Int,
    val remainingBids: Int,
    val billingCycleEnd: String,
    val isActive: Boolean = true
)

enum class CampaignType(val title: String, val priceInr: Long, val durationDays: Int, val description: String) {
    FEATURED_PROFILE("Featured Vendor Profile", 1099, 7, "Showcase your profile at the top of category searches for 7 days."),
    SPONSORED_CATEGORY("Sponsored Category Banner", 2199, 15, "Prominent banner placement within your target event category."),
    CITY_PROMOTION("City-Wide Promotion", 3199, 15, "Target all event organizers looking for services in your metro city."),
    FESTIVAL_SPECIAL("Wedding Season / Festival Special", 5199, 30, "Maximum visibility across high-budget Indian wedding & festival events.")
}

data class CampaignItem(
    val id: String,
    val vendorId: String,
    val vendorName: String,
    val campaignType: CampaignType,
    val targetCity: String,
    val targetCategory: String,
    val status: String,
    val impressions: Int,
    val clicks: Int,
    val enquiries: Int,
    val startDate: String,
    val endDate: String,
    val spendInr: Long
)

data class ChatMessage(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val recipientId: String,
    val text: String,
    val attachmentName: String? = null,
    val attachmentType: String? = null,
    val timestamp: String,
    val isRead: Boolean = true,
    val isFromCustomer: Boolean
)

data class AppUser(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val isPremium: Boolean = false,
    val status: String = "Active", // "Active", "Suspended"
    val joinedDate: String = "Oct 2026"
)

data class PublicEventListing(
    val id: String,
    val title: String,
    val category: String,
    val city: String,
    val venue: String,
    val date: String,
    val time: String,
    val basePrice: Long,
    val availableTickets: Int,
    val description: String,
    val imageResId: Int? = null
)

data class EventTicket(
    val id: String,
    val eventTitle: String,
    val city: String,
    val eventDate: String,
    val venue: String,
    val ticketType: String,
    val pricePerTicket: Long,
    val quantity: Int,
    val totalAmount: Long,
    val bookingRef: String,
    val bookedAt: String,
    val status: String = "Confirmed"
)

data class InvoiceLineItem(
    val description: String,
    val hsnSac: String = "998599",
    val quantity: Int = 1,
    val unitRate: Long,
    val amount: Long
)

data class PlatformInvoice(
    val invoiceNumber: String,
    val issueDate: String,
    val dueDate: String,
    val billedToName: String,
    val billedToEmail: String,
    val billedToGstin: String? = null,
    val billedToCity: String,
    val type: String, // "VENDOR_SUBSCRIPTION", "CUSTOMER_BOOKING", "CAMPAIGN_FEE", "TICKET_SALE"
    val items: List<InvoiceLineItem>,
    val subTotal: Long,
    val cgstAmount: Long, // 9%
    val sgstAmount: Long, // 9%
    val totalAmount: Long,
    val status: String = "PAID",
    val paymentMode: String = "UPI Instant Settlement",
    val referenceId: String
)

data class EventServiceAgreement(
    val agreementNumber: String,
    val eventId: String,
    val eventTitle: String,
    val customerName: String,
    val customerPhone: String,
    val vendorName: String,
    val vendorOwner: String,
    val vendorGstin: String,
    val eventDate: String,
    val eventVenue: String,
    val servicesScope: List<String>,
    val totalContractValue: Long,
    val advancePaid: Long,
    val remainingBalance: Long,
    val milestones: List<String>,
    val cancellationTerms: String,
    val disputeClause: String,
    val executionDate: String,
    val status: String = "EXECUTED & BINDING"
)


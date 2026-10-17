package com.example.data

import com.example.R
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

class EventBidRepository {

    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    // Currency Formatter for INR (e.g., ₹2,00,000)
    fun formatInr(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<AppUser?>(
        AppUser(
            id = "USR-001",
            fullName = "Priya Sharma",
            email = "priya.sharma@gmail.com",
            phone = "+91 98765 43210",
            role = UserRole.CUSTOMER,
            isPremium = true
        )
    )
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    fun signInWithOtp(phone: String, otp: String): Boolean {
        if (otp.length == 6) {
            val role = _currentRole.value
            _currentUser.value = AppUser(
                id = "USR-${System.currentTimeMillis() % 10000}",
                fullName = if (role == UserRole.CUSTOMER) "Priya Sharma" else "Rajesh Kumar (Dream Decor)",
                email = if (role == UserRole.CUSTOMER) "priya.sharma@gmail.com" else "rajesh@dreamdecor.in",
                phone = phone,
                role = role,
                isPremium = true
            )
            return true
        }
        return false
    }

    fun signInWithGoogle(name: String, email: String) {
        val role = _currentRole.value
        _currentUser.value = AppUser(
            id = "USR-${System.currentTimeMillis() % 10000}",
            fullName = name,
            email = email,
            phone = "+91 98450 11223",
            role = role,
            isPremium = true
        )
    }

    fun registerAccount(name: String, email: String, phone: String, role: UserRole) {
        _currentRole.value = role
        _currentUser.value = AppUser(
            id = "USR-${System.currentTimeMillis() % 10000}",
            fullName = name,
            email = email,
            phone = phone,
            role = role,
            isPremium = false
        )
    }

    fun signOutUser() {
        _currentUser.value = null
    }

    // --- User Management List for Admin ---
    private val _usersList = MutableStateFlow<List<AppUser>>(
        listOf(
            AppUser("USR-101", "Priya Sharma", "priya.sharma@gmail.com", "+91 98765 43210", UserRole.CUSTOMER, isPremium = true),
            AppUser("USR-102", "Rajesh Kumar (Dream Decor)", "rajesh@dreamdecor.in", "+91 98450 11223", UserRole.VENDOR, isPremium = true),
            AppUser("USR-103", "Ananya Reddy", "ananya.reddy@gmail.com", "+91 98111 22334", UserRole.CUSTOMER, isPremium = false),
            AppUser("USR-104", "Vikram Sengupta", "vikram@techcorp.in", "+91 99223 88441", UserRole.CUSTOMER, isPremium = true),
            AppUser("USR-105", "Sunil Verma (Royal Events)", "sunil@royalevents.com", "+91 98330 44556", UserRole.VENDOR, isPremium = false),
            AppUser("USR-106", "Karan Malhotra (Shree Decor)", "karan@shreedecor.in", "+91 98221 66778", UserRole.VENDOR, isPremium = false)
        )
    )
    val usersList: StateFlow<List<AppUser>> = _usersList.asStateFlow()

    fun toggleUserPremium(userId: String) {
        _usersList.value = _usersList.value.map {
            if (it.id == userId) it.copy(isPremium = !it.isPremium) else it
        }
    }

    fun resetUserSession(userId: String) {
        _usersList.value = _usersList.value.map {
            if (it.id == userId) it.copy(status = "Session Reset") else it
        }
    }

    fun deleteUser(userId: String) {
        _usersList.value = _usersList.value.filterNot { it.id == userId }
    }

    // --- Preloaded Events ---
    private val _events = MutableStateFlow<List<EventItem>>(
        listOf(
            EventItem(
                id = "EVT-1001",
                title = "Wedding Decoration & Mandap Stage",
                eventType = EventType.WEDDING,
                city = "Hyderabad",
                venueArea = "Banjara Hills, Taj Krishna Lawns",
                eventDate = "28 Nov 2026",
                guestCount = 450,
                estimatedBudget = 200000,
                servicesRequired = listOf("Floral Mandap", "Stage Lighting", "Entrance Arch", "LED Backdrop"),
                description = "We are organizing a royal destination-style wedding reception. Looking for experienced decorators capable of marigold & orchid floral installations, warm fairy lights, and grand entrance arch.",
                biddingDeadline = "24 Oct 2026",
                status = EventStatus.BIDDING_OPEN,
                customerId = "CUST-01",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 43210",
                createdAt = "15 Oct 2026",
                bidsCount = 3,
                imageResId = R.drawable.img_wedding
            ),
            EventItem(
                id = "EVT-1002",
                title = "1st Birthday Party Celebration",
                eventType = EventType.BIRTHDAY,
                city = "Bangalore",
                venueArea = "Indiranagar Club Lawn",
                eventDate = "15 Dec 2026",
                guestCount = 80,
                estimatedBudget = 25000,
                servicesRequired = listOf("Balloon Art", "Theme Backdrop", "Kids Mascot", "Cake Table Setup"),
                description = "Jungle theme birthday celebration for a 1-year-old baby boy. Needs balloon arch, animal cutouts, stage backdrop, and baby milestone board.",
                biddingDeadline = "30 Oct 2026",
                status = EventStatus.BIDDING_OPEN,
                customerId = "CUST-02",
                customerName = "Ananya Reddy",
                customerPhone = "+91 98111 22334",
                createdAt = "16 Oct 2026",
                bidsCount = 1
            ),
            EventItem(
                id = "EVT-1003",
                title = "Corporate Tech Bangalore Summit",
                eventType = EventType.CORPORATE,
                city = "Bangalore",
                venueArea = "Whitefield Convention Centre",
                eventDate = "10 Jan 2027",
                guestCount = 350,
                estimatedBudget = 80000,
                servicesRequired = listOf("LED Video Wall", "Dual Podiums", "Stage Audio-Visual", "Lavalier Mics"),
                description = "Annual software innovation summit. Requires high-definition 16x9 P3 LED video wall, digital mixing console, 4 wireless lapel mics, and live audio technician.",
                biddingDeadline = "15 Nov 2026",
                status = EventStatus.BIDDING_OPEN,
                customerId = "CUST-03",
                customerName = "Vikram Sengupta (TechCorp)",
                customerPhone = "+91 99223 88441",
                createdAt = "14 Oct 2026",
                bidsCount = 2,
                imageResId = R.drawable.img_corporate
            ),
            EventItem(
                id = "EVT-1004",
                title = "Gourmet Catering & Live Food Counters",
                eventType = EventType.CATERING,
                city = "Chennai",
                venueArea = "Adyar Grand Palace",
                eventDate = "05 Dec 2026",
                guestCount = 200,
                estimatedBudget = 120000,
                servicesRequired = listOf("South Indian Buffet", "Live Chaat Counter", "Dessert Bar"),
                description = "Engagement ceremony lunch with authentic vegetarian delicacies, traditional filter coffee station, and interactive live tawa chaat.",
                biddingDeadline = "20 Oct 2026",
                status = EventStatus.BIDDING_OPEN,
                customerId = "CUST-01",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 43210",
                createdAt = "12 Oct 2026",
                bidsCount = 1
            )
        )
    )
    val events: StateFlow<List<EventItem>> = _events.asStateFlow()

    // --- Preloaded Bids ---
    private val _bids = MutableStateFlow<List<BidItem>>(
        listOf(
            BidItem(
                id = "BID-201",
                eventId = "EVT-1001",
                vendorId = "VEND-01",
                vendorName = "Dream Decor & Flowers",
                vendorRating = 4.8f,
                vendorReviewCount = 134,
                price = 185000,
                inclusions = listOf("Grand 30ft Marigold Mandap", "1000m Warm Fairy Lights", "Royal Velvet Drapes", "Entrance Floral Archway", "On-site Crew Coordinator"),
                exclusions = listOf("Venue power generator rental", "Custom acrylic dance floor"),
                validityDays = 7,
                deliveryNotes = "Setup will commence at 6:00 AM on 28th Nov. Full completion 3 hours prior to guest arrival.",
                submittedAt = "16 Oct 2026, 11:30 AM",
                status = BidStatus.SHORTLISTED,
                isVerified = true,
                isTopRated = true,
                isCustomerRecommended = true,
                responseTime = "< 1 hr"
            ),
            BidItem(
                id = "BID-202",
                eventId = "EVT-1001",
                vendorId = "VEND-02",
                vendorName = "Royal Events & Celebrations",
                vendorRating = 4.6f,
                vendorReviewCount = 98,
                price = 195000,
                inclusions = listOf("Fabric & Flower Mandap", "Stage Spotlights & Amber Wash", "Welcome Board & Lantern Walkway"),
                exclusions = listOf("Imported Dutch roses surcharge"),
                validityDays = 5,
                deliveryNotes = "Can provide free complementary photobooth backdrop if booked within 48 hours.",
                submittedAt = "16 Oct 2026, 03:15 PM",
                status = BidStatus.SUBMITTED,
                isVerified = true,
                isTopRated = false,
                isCustomerRecommended = true,
                responseTime = "< 2 hrs"
            ),
            BidItem(
                id = "BID-203",
                eventId = "EVT-1001",
                vendorId = "VEND-03",
                vendorName = "Shree Luxury Decorations",
                vendorRating = 4.5f,
                vendorReviewCount = 76,
                price = 210000,
                inclusions = listOf("Premium Metal Frame Mandap", "Imported Orchids & Lilies", "Ambient LED Par Lights", "Bridal Seating Sofas"),
                exclusions = listOf("Transportation outside city limits"),
                validityDays = 10,
                deliveryNotes = "Includes 2 dedicated on-site florists throughout the event duration.",
                submittedAt = "17 Oct 2026, 09:40 AM",
                status = BidStatus.SUBMITTED,
                isVerified = false,
                isTopRated = false,
                isCustomerRecommended = false,
                responseTime = "< 4 hrs"
            )
        )
    )
    val bids: StateFlow<List<BidItem>> = _bids.asStateFlow()

    // --- Vendor Profile & Verification Documents ---
    private val _vendorProfile = MutableStateFlow(
        VendorProfile(
            id = "VEND-01",
            businessName = "Dream Decor & Flowers",
            businessType = "Private Limited Company",
            ownerName = "Rajesh Kumar",
            email = "rajesh@dreamdecor.in",
            phone = "+91 98450 11223",
            primaryCity = "Bangalore",
            operatingCities = listOf("Bangalore", "Hyderabad", "Chennai"),
            categories = listOf("Decoration & Floral", "Stage & Lighting", "Full Weddings"),
            aboutText = "We specialize in crafting bespoke and memorable decorations for weddings, luxury birthdays, and corporate events across South India. With over 150+ successfully delivered ceremonies, we bring perfection to every petal.",
            rating = 4.8f,
            reviewCount = 134,
            completedEventsCount = 152,
            responseTime = "< 1 hr",
            trustBadges = setOf(TrustBadge.VERIFIED, TrustBadge.TOP_RATED, TrustBadge.CUSTOMER_RECOMMENDED),
            isVerified = true,
            isTopRated = true,
            isCustomerRecommended = true,
            verificationDocuments = listOf(
                VerificationDocument(
                    docType = VerificationDocType.GSTIN,
                    docNumber = "36AAACD1234F1Z8",
                    fileName = "GST_Registration_Certificate_2026.pdf",
                    uploadedAt = "10 Oct 2026",
                    status = VerificationStatus.VERIFIED,
                    uploadSource = UploadSource.FILE,
                    verifiedAt = "12 Oct 2026",
                    expiryDate = "31 Dec 2027"
                ),
                VerificationDocument(
                    docType = VerificationDocType.UDYAM_MSME,
                    docNumber = "UDYAM-KR-03-0098765",
                    fileName = "Udyam_MSME_Registration.pdf",
                    uploadedAt = "10 Oct 2026",
                    status = VerificationStatus.VERIFIED,
                    uploadSource = UploadSource.FILE,
                    verifiedAt = "12 Oct 2026"
                ),
                VerificationDocument(
                    docType = VerificationDocType.PAN_CARD,
                    docNumber = "AAACD1234F",
                    fileName = "Company_PAN_Card.pdf",
                    uploadedAt = "10 Oct 2026",
                    status = VerificationStatus.VERIFIED,
                    uploadSource = UploadSource.CAMERA,
                    verifiedAt = "12 Oct 2026"
                ),
                VerificationDocument(
                    docType = VerificationDocType.TRADE_LICENSE,
                    docNumber = "BBMP/TL/2026/8941",
                    fileName = "BBMP_Trade_License.pdf",
                    uploadedAt = "15 Oct 2026",
                    status = VerificationStatus.UNDER_REVIEW,
                    uploadSource = UploadSource.FILE,
                    verifiedAt = null
                )
            )
        )
    )
    val vendorProfile: StateFlow<VendorProfile> = _vendorProfile.asStateFlow()

    // Admin Verification Desk Submission
    data class AdminVerificationSubmission(
        val submissionId: String,
        val vendorId: String,
        val vendorName: String,
        val ownerName: String,
        val email: String,
        val phone: String,
        val businessType: String,
        val city: String,
        val docType: VerificationDocType,
        val docNumber: String,
        val fileName: String,
        val uploadSource: UploadSource,
        val submittedDate: String,
        var status: VerificationStatus,
        var rejectionReason: String? = null
    )

    private val _adminVerificationQueue = MutableStateFlow<List<AdminVerificationSubmission>>(
        listOf(
            AdminVerificationSubmission(
                submissionId = "VERIF-901",
                vendorId = "VEND-01",
                vendorName = "Dream Decor & Flowers",
                ownerName = "Rajesh Kumar",
                email = "rajesh@dreamdecor.in",
                phone = "+91 98450 11223",
                businessType = "Private Limited",
                city = "Bangalore",
                docType = VerificationDocType.TRADE_LICENSE,
                docNumber = "BBMP/TL/2026/8941",
                fileName = "BBMP_Trade_License_2026.pdf",
                uploadSource = UploadSource.FILE,
                submittedDate = "15 Oct 2026",
                status = VerificationStatus.UNDER_REVIEW
            ),
            AdminVerificationSubmission(
                submissionId = "VERIF-902",
                vendorId = "VEND-03",
                vendorName = "Shree Luxury Decorations",
                ownerName = "Karan Malhotra",
                email = "karan@shreedecor.in",
                phone = "+91 98221 66778",
                businessType = "Proprietorship",
                city = "Hyderabad",
                docType = VerificationDocType.GSTIN,
                docNumber = "29AABCU9876Q1Z2",
                fileName = "Shree_Decor_GSTIN.jpg",
                uploadSource = UploadSource.CAMERA,
                submittedDate = "16 Oct 2026",
                status = VerificationStatus.UNDER_REVIEW
            ),
            AdminVerificationSubmission(
                submissionId = "VERIF-903",
                vendorId = "VEND-04",
                vendorName = "Grand Feast Catering",
                ownerName = "Meenakshi Sundaram",
                email = "info@grandfeast.com",
                phone = "+91 94440 98123",
                businessType = "Partnership",
                city = "Chennai",
                docType = VerificationDocType.TRADE_LICENSE,
                docNumber = "FSSAI-1002349001",
                fileName = "FSSAI_Central_License.pdf",
                uploadSource = UploadSource.FILE,
                submittedDate = "14 Oct 2026",
                status = VerificationStatus.UNDER_REVIEW
            )
        )
    )
    val adminVerificationQueue: StateFlow<List<AdminVerificationSubmission>> = _adminVerificationQueue.asStateFlow()

    // --- Subscription & Quotas (Default: Growth Plus ₹499/mo) ---
    private val _vendorSubscription = MutableStateFlow(
        VendorSubscription(
            tier = SubscriptionTier.PACKAGE_499,
            remainingViews = 35,
            remainingBids = 14,
            billingCycleEnd = "15 Nov 2026",
            isActive = true
        )
    )
    val vendorSubscription: StateFlow<VendorSubscription> = _vendorSubscription.asStateFlow()

    // --- Public Events For Ticket Booking ---
    private val _publicEvents = MutableStateFlow<List<PublicEventListing>>(
        listOf(
            PublicEventListing(
                id = "PUB-01",
                title = "Hyderabad Royal Wedding & Decor Expo 2026",
                category = "Exhibition & Wedding Showcase",
                city = "Hyderabad",
                venue = "Hitex Exhibition Center, Hall 2",
                date = "22 Nov 2026",
                time = "10:00 AM - 8:00 PM",
                basePrice = 299,
                availableTickets = 240,
                description = "South India's premier wedding procurement expo with live stage mandap setups, celebrity designer showcases, bridal tastings, and exclusive vendor discounts.",
                imageResId = R.drawable.img_wedding
            ),
            PublicEventListing(
                id = "PUB-02",
                title = "Bangalore Tech & Innovation Gala Night",
                category = "Conference & Networking Party",
                city = "Bangalore",
                venue = "Whitefield Convention Lawns",
                date = "12 Dec 2026",
                time = "6:00 PM - 11:30 PM",
                basePrice = 499,
                availableTickets = 180,
                description = "Evening networking cocktail gala featuring top tech founders, live acoustic performance, LED interactive art displays, and gourmet dinner.",
                imageResId = R.drawable.img_corporate
            ),
            PublicEventListing(
                id = "PUB-03",
                title = "Chennai Festive Music & Food Carnival",
                category = "Live Entertainment & Carnival",
                city = "Chennai",
                venue = "Island Grounds, Marina",
                date = "25 Dec 2026",
                time = "4:00 PM - 10:00 PM",
                basePrice = 199,
                availableTickets = 350,
                description = "Grand family Christmas carnival with 40+ authentic street food counters, live DJ band performance, kids amusement games, and festive stage lighting.",
                imageResId = R.drawable.img_wedding
            )
        )
    )
    val publicEvents: StateFlow<List<PublicEventListing>> = _publicEvents.asStateFlow()

    // --- Booked Tickets ---
    private val _myTickets = MutableStateFlow<List<EventTicket>>(
        listOf(
            EventTicket(
                id = "TCK-8812",
                eventTitle = "Hyderabad Royal Wedding & Decor Expo 2026",
                city = "Hyderabad",
                eventDate = "22 Nov 2026",
                venue = "Hitex Exhibition Center, Hall 2",
                ticketType = "VIP Delegate Pass",
                pricePerTicket = 499,
                quantity = 2,
                totalAmount = 998,
                bookingRef = "EB-TKT-9941",
                bookedAt = "15 Oct 2026",
                status = "Confirmed"
            )
        )
    )
    val myTickets: StateFlow<List<EventTicket>> = _myTickets.asStateFlow()

    // --- Official Platform Invoices (GST Compliant) ---
    private val _invoices = MutableStateFlow<List<PlatformInvoice>>(
        listOf(
            PlatformInvoice(
                invoiceNumber = "INV-2026-0812",
                issueDate = "16 Oct 2026",
                dueDate = "16 Oct 2026",
                billedToName = "Priya Sharma",
                billedToEmail = "priya.sharma@gmail.com",
                billedToCity = "Hyderabad",
                type = "CUSTOMER_BOOKING",
                items = listOf(
                    InvoiceLineItem("Grand Wedding Mandap & Floral Decoration Escrow Deposit", "998599", 1, 185000, 185000)
                ),
                subTotal = 185000,
                cgstAmount = 16650,
                sgstAmount = 16650,
                totalAmount = 218300,
                status = "PAID",
                paymentMode = "UPI Autopay / Escrow Locked",
                referenceId = "PAY-EB-HYD-98124"
            ),
            PlatformInvoice(
                invoiceNumber = "INV-2026-0901",
                issueDate = "15 Oct 2026",
                dueDate = "15 Nov 2026",
                billedToName = "Dream Decor & Flowers (Rajesh Kumar)",
                billedToEmail = "rajesh@dreamdecor.in",
                billedToGstin = "36AABCU9876Q1Z2",
                billedToCity = "Hyderabad",
                type = "VENDOR_SUBSCRIPTION",
                items = listOf(
                    InvoiceLineItem("Vendor Monthly Subscription — Growth Plus Tier (50 Lead Views, 20 Bids)", "998599", 1, 499, 499)
                ),
                subTotal = 499,
                cgstAmount = 45,
                sgstAmount = 45,
                totalAmount = 589,
                status = "PAID",
                paymentMode = "Razorpay NetBanking",
                referenceId = "PAY-SUB-499-102"
            ),
            PlatformInvoice(
                invoiceNumber = "INV-2026-0902",
                issueDate = "14 Oct 2026",
                dueDate = "14 Nov 2026",
                billedToName = "Royal Events & Production (Sunil Verma)",
                billedToEmail = "sunil@royalevents.com",
                billedToGstin = "07AAACR1234F1Z5",
                billedToCity = "Delhi NCR",
                type = "VENDOR_SUBSCRIPTION",
                items = listOf(
                    InvoiceLineItem("Vendor Monthly Subscription — Standard Pro Tier (25 Views, 12 Bids)", "998599", 1, 399, 399)
                ),
                subTotal = 399,
                cgstAmount = 36,
                sgstAmount = 36,
                totalAmount = 471,
                status = "PAID",
                paymentMode = "UPI / PhonePe",
                referenceId = "PAY-SUB-399-881"
            ),
            PlatformInvoice(
                invoiceNumber = "INV-2026-0903",
                issueDate = "12 Oct 2026",
                dueDate = "12 Nov 2026",
                billedToName = "TechCorp Solutions (Vikram Sengupta)",
                billedToEmail = "vikram@techcorp.in",
                billedToGstin = "29AAACT5566K1Z9",
                billedToCity = "Bangalore",
                type = "VENDOR_SUBSCRIPTION",
                items = listOf(
                    InvoiceLineItem("Vendor Enterprise VIP Package (200 Views, 70 Bids, Dedicated RM)", "998599", 1, 1999, 1999)
                ),
                subTotal = 1999,
                cgstAmount = 180,
                sgstAmount = 180,
                totalAmount = 2359,
                status = "PAID",
                paymentMode = "Corporate Card",
                referenceId = "PAY-SUB-1999-712"
            ),
            PlatformInvoice(
                invoiceNumber = "INV-2026-0904",
                issueDate = "10 Oct 2026",
                dueDate = "10 Oct 2026",
                billedToName = "Dream Decor & Flowers",
                billedToEmail = "rajesh@dreamdecor.in",
                billedToGstin = "36AABCU9876Q1Z2",
                billedToCity = "Hyderabad",
                type = "CAMPAIGN_FEE",
                items = listOf(
                    InvoiceLineItem("Featured Vendor Profile 7-Day Search Sponsorship Banner", "998599", 1, 1099, 1099)
                ),
                subTotal = 1099,
                cgstAmount = 99,
                sgstAmount = 99,
                totalAmount = 1297,
                status = "PAID",
                paymentMode = "Google Pay UPI",
                referenceId = "PAY-CAMP-1099-301"
            )
        )
    )
    val invoices: StateFlow<List<PlatformInvoice>> = _invoices.asStateFlow()

    // --- Official Bilateral Event Procurement Service Agreements ---
    private val _agreements = MutableStateFlow<List<EventServiceAgreement>>(
        listOf(
            EventServiceAgreement(
                agreementNumber = "AGR-HYD-2026-1001",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 43210",
                vendorName = "Dream Decor & Floral Art",
                vendorOwner = "Rajesh Kumar",
                vendorGstin = "36AABCU9876Q1Z2",
                eventDate = "28 Nov 2026",
                eventVenue = "Taj Krishna Lawns, Banjara Hills, Hyderabad",
                servicesScope = listOf(
                    "All fresh flower floral mandap installations with marigolds and orchids",
                    "Ambient fairy & 24 par can LED lights across stage and dining lawn",
                    "Dedicated on-site crew supervisor with 3-hour setup advance buffer",
                    "30 vintage brass walkway lanterns with warm fairy lighting"
                ),
                totalContractValue = 185000,
                advancePaid = 55500,
                remainingBalance = 129500,
                milestones = listOf(
                    "30% advance on signing contract (Escrow locked in EventBid)",
                    "50% upon on-site frame and floral structural inspection (27 Nov, 6 PM)",
                    "20% final signoff after stage handover and reception completion"
                ),
                cancellationTerms = "100% refund for cancellations 14+ days prior. 50% between 7-14 days. Non-refundable under 7 days to cover procured perishables.",
                disputeClause = "Any dispute arising hereunder shall be subject to binding arbitration through EventBid Ombudsman under the Indian Arbitration and Conciliation Act, 1996.",
                executionDate = "16 Oct 2026",
                status = "EXECUTED & BINDING"
            )
        )
    )
    val agreements: StateFlow<List<EventServiceAgreement>> = _agreements.asStateFlow()

    fun bookTicket(
        event: PublicEventListing,
        ticketType: String,
        pricePerTicket: Long,
        quantity: Int
    ): EventTicket {
        val total = pricePerTicket * quantity
        val ticket = EventTicket(
            id = "TCK-${(1000..9999).random()}",
            eventTitle = event.title,
            city = event.city,
            eventDate = event.date,
            venue = event.venue,
            ticketType = ticketType,
            pricePerTicket = pricePerTicket,
            quantity = quantity,
            totalAmount = total,
            bookingRef = "EB-TKT-${(1000..9999).random()}",
            bookedAt = "Just now",
            status = "Confirmed"
        )
        _myTickets.value = listOf(ticket) + _myTickets.value

        // Auto-generate Ticket Invoice
        val tax = (total * 0.18).toLong()
        val ticketInvoice = PlatformInvoice(
            invoiceNumber = "INV-TKT-2026-${(1000..9999).random()}",
            issueDate = "Today",
            dueDate = "Today",
            billedToName = _currentUser.value?.fullName ?: "Valued Delegate",
            billedToEmail = _currentUser.value?.email ?: "guest@eventbid.in",
            billedToCity = event.city,
            type = "TICKET_SALE",
            items = listOf(
                InvoiceLineItem("Event Ticket: ${event.title} (${quantity}x $ticketType)", "998599", quantity, pricePerTicket, total)
            ),
            subTotal = total,
            cgstAmount = tax / 2,
            sgstAmount = tax / 2,
            totalAmount = total + tax,
            status = "PAID",
            paymentMode = "UPI / Card",
            referenceId = ticket.bookingRef
        )
        _invoices.value = listOf(ticketInvoice) + _invoices.value

        return ticket
    }

    // --- Promotional Campaigns ---
    private val _campaigns = MutableStateFlow<List<CampaignItem>>(
        listOf(
            CampaignItem(
                id = "CAMP-301",
                vendorId = "VEND-01",
                vendorName = "Dream Decor & Flowers",
                campaignType = CampaignType.FEATURED_PROFILE,
                targetCity = "Bangalore & Hyderabad",
                targetCategory = "Decoration & Floral",
                status = "Active",
                impressions = 2480,
                clicks = 186,
                enquiries = 24,
                startDate = "10 Oct 2026",
                endDate = "17 Oct 2026",
                spendInr = 1099
            ),
            CampaignItem(
                id = "CAMP-302",
                vendorId = "VEND-01",
                vendorName = "Dream Decor & Flowers",
                campaignType = CampaignType.SPONSORED_CATEGORY,
                targetCity = "South India Metros",
                targetCategory = "Wedding Decorations",
                status = "Active",
                impressions = 4120,
                clicks = 340,
                enquiries = 48,
                startDate = "01 Oct 2026",
                endDate = "16 Oct 2026",
                spendInr = 2199
            )
        )
    )
    val campaigns: StateFlow<List<CampaignItem>> = _campaigns.asStateFlow()

    // --- Real-time Chat System ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "MSG-1",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                senderId = "VEND-01",
                senderName = "Dream Decor",
                senderRole = UserRole.VENDOR,
                recipientId = "CUST-01",
                text = "Namaste Priya ji! We are pleased to submit our formal quotation for your royal wedding reception at Taj Krishna Lawns.",
                timestamp = "16 Oct, 11:32 AM",
                isFromCustomer = false
            ),
            ChatMessage(
                id = "MSG-2",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                senderId = "CUST-01",
                senderName = "Priya Sharma",
                senderRole = UserRole.CUSTOMER,
                recipientId = "VEND-01",
                text = "Thank you Rajesh! I reviewed your bid of ₹1,85,000. Can you share some photos of similar royal marigold mandaps you designed recently?",
                timestamp = "16 Oct, 11:45 AM",
                isFromCustomer = true
            ),
            ChatMessage(
                id = "MSG-3",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                senderId = "VEND-01",
                senderName = "Dream Decor",
                senderRole = UserRole.VENDOR,
                recipientId = "CUST-01",
                text = "Certainly! Attached is our recent Taj Hyderabad Royal Mandap portfolio and the detailed itemized PDF breakdown.",
                attachmentName = "Mandap_Royal_Design_Deck.pdf",
                attachmentType = "PDF",
                timestamp = "16 Oct, 11:50 AM",
                isFromCustomer = false
            ),
            ChatMessage(
                id = "MSG-4",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                senderId = "CUST-01",
                senderName = "Priya Sharma",
                senderRole = UserRole.CUSTOMER,
                recipientId = "VEND-01",
                text = "The floral canopy in photo 3 looks breathtaking! Does the price include the entrance walkway lanterns as well?",
                timestamp = "16 Oct, 12:10 PM",
                isFromCustomer = true
            ),
            ChatMessage(
                id = "MSG-5",
                eventId = "EVT-1001",
                eventTitle = "Wedding Decoration & Mandap Stage",
                senderId = "VEND-01",
                senderName = "Dream Decor",
                senderRole = UserRole.VENDOR,
                recipientId = "CUST-01",
                text = "Yes, absolutely! Since you have shortlisted our bid, we have included 30 vintage brass lanterns along the walkway with warm fairy lights as part of the package at no extra cost.",
                timestamp = "16 Oct, 12:15 PM",
                isFromCustomer = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // --- Actions ---

    // 1. Post Event
    fun createEvent(
        title: String,
        eventType: EventType,
        city: String,
        venueArea: String,
        eventDate: String,
        guestCount: Int,
        budget: Long,
        services: List<String>,
        description: String
    ): EventItem {
        val newEvent = EventItem(
            id = "EVT-${1000 + _events.value.size + 1}",
            title = title,
            eventType = eventType,
            city = city,
            venueArea = venueArea,
            eventDate = eventDate,
            guestCount = guestCount,
            estimatedBudget = budget,
            servicesRequired = services,
            description = description,
            biddingDeadline = "10 Days Left",
            status = EventStatus.BIDDING_OPEN,
            customerId = "CUST-01",
            customerName = "Priya Sharma",
            customerPhone = "+91 98765 43210",
            createdAt = "Just now",
            bidsCount = 0
        )
        _events.value = listOf(newEvent) + _events.value
        return newEvent
    }

    // 2. Submit Bid (Strict Quota Check: without credit don't give access)
    fun submitBid(
        eventId: String,
        price: Long,
        inclusions: List<String>,
        exclusions: List<String>,
        validityDays: Int,
        deliveryNotes: String
    ): Boolean {
        val sub = _vendorSubscription.value
        if (sub.remainingBids <= 0) {
            return false // Quota exhausted - strictly blocked!
        }

        // Deduct 1 bid credit
        _vendorSubscription.value = sub.copy(remainingBids = sub.remainingBids - 1)

        val newBid = BidItem(
            id = "BID-${200 + _bids.value.size + 1}",
            eventId = eventId,
            vendorId = _vendorProfile.value.id,
            vendorName = _vendorProfile.value.businessName,
            vendorRating = _vendorProfile.value.rating,
            vendorReviewCount = _vendorProfile.value.reviewCount,
            price = price,
            inclusions = inclusions,
            exclusions = exclusions,
            validityDays = validityDays,
            deliveryNotes = deliveryNotes,
            submittedAt = "Just now",
            status = BidStatus.SUBMITTED,
            isVerified = _vendorProfile.value.isVerified,
            isTopRated = _vendorProfile.value.isTopRated,
            isCustomerRecommended = _vendorProfile.value.isCustomerRecommended
        )

        _bids.value = _bids.value + newBid

        _events.value = _events.value.map {
            if (it.id == eventId) it.copy(bidsCount = it.bidsCount + 1) else it
        }

        return true
    }

    // 3. Shortlist Bid
    fun shortlistBid(bidId: String) {
        _bids.value = _bids.value.map {
            if (it.id == bidId) it.copy(status = BidStatus.SHORTLISTED) else it
        }
    }

    // 4. Accept / Book Bid
    fun acceptBid(bidId: String, eventId: String) {
        val targetBid = _bids.value.find { it.id == bidId }
        val targetEvent = _events.value.find { it.id == eventId }

        _bids.value = _bids.value.map {
            if (it.id == bidId) it.copy(status = BidStatus.ACCEPTED)
            else if (it.eventId == eventId && it.status != BidStatus.ACCEPTED) it.copy(status = BidStatus.REJECTED)
            else it
        }
        _events.value = _events.value.map {
            if (it.id == eventId) it.copy(status = EventStatus.BOOKED) else it
        }

        if (targetBid != null && targetEvent != null) {
            val advance = (targetBid.price * 0.30).toLong()
            val balance = targetBid.price - advance
            val newAgreement = EventServiceAgreement(
                agreementNumber = "AGR-${targetEvent.city.take(3).uppercase()}-2026-${(1000..9999).random()}",
                eventId = targetEvent.id,
                eventTitle = targetEvent.title,
                customerName = targetEvent.customerName,
                customerPhone = targetEvent.customerPhone,
                vendorName = targetBid.vendorName,
                vendorOwner = _vendorProfile.value.ownerName,
                vendorGstin = "36AABCU9876Q1Z2",
                eventDate = targetEvent.eventDate,
                eventVenue = "${targetEvent.venueArea}, ${targetEvent.city}",
                servicesScope = targetBid.inclusions.ifEmpty { targetEvent.servicesRequired },
                totalContractValue = targetBid.price,
                advancePaid = advance,
                remainingBalance = balance,
                milestones = listOf(
                    "30% Advance Deposit on Contract Signing (Locked via EventBid Escrow)",
                    "50% Mid-execution setup inspection on event date",
                    "20% Final post-event signoff & satisfaction release"
                ),
                cancellationTerms = "100% refund for cancellation 14+ days prior; 50% between 7-14 days; non-refundable within 7 days.",
                disputeClause = "Arbitration administered by EventBid Ombudsman in accordance with Indian Arbitration & Conciliation Act 1996.",
                executionDate = "Today",
                status = "EXECUTED & BINDING"
            )
            _agreements.value = listOf(newAgreement) + _agreements.value

            // Generate Booking Tax Invoice
            val tax = (targetBid.price * 0.18).toLong()
            val newInvoice = PlatformInvoice(
                invoiceNumber = "INV-2026-${(1000..9999).random()}",
                issueDate = "Today",
                dueDate = "Today",
                billedToName = targetEvent.customerName,
                billedToEmail = "priya.sharma@gmail.com",
                billedToCity = targetEvent.city,
                type = "CUSTOMER_BOOKING",
                items = listOf(
                    InvoiceLineItem("Event Procurement Booking: ${targetEvent.title} (${targetBid.vendorName})", "998599", 1, targetBid.price, targetBid.price)
                ),
                subTotal = targetBid.price,
                cgstAmount = tax / 2,
                sgstAmount = tax / 2,
                totalAmount = targetBid.price + tax,
                status = "PAID",
                paymentMode = "UPI Autopay / Escrow Locked",
                referenceId = "PAY-EB-${(10000..99999).random()}"
            )
            _invoices.value = listOf(newInvoice) + _invoices.value
        }
    }

    // 5. Vendor Document Upload (File or Camera)
    fun uploadVendorDocument(
        docType: VerificationDocType,
        docNumber: String,
        fileName: String,
        uploadSource: UploadSource = UploadSource.FILE
    ) {
        val doc = VerificationDocument(
            docType = docType,
            docNumber = docNumber,
            fileName = fileName,
            uploadedAt = "Today",
            status = VerificationStatus.UNDER_REVIEW,
            uploadSource = uploadSource
        )
        val currentDocs = _vendorProfile.value.verificationDocuments.filterNot { it.docType == docType }
        _vendorProfile.value = _vendorProfile.value.copy(
            verificationDocuments = currentDocs + doc
        )

        // Add to admin queue with complete vendor identity
        val submission = AdminVerificationSubmission(
            submissionId = "VERIF-${(100..999).random()}",
            vendorId = _vendorProfile.value.id,
            vendorName = _vendorProfile.value.businessName,
            ownerName = _vendorProfile.value.ownerName,
            email = _vendorProfile.value.email,
            phone = _vendorProfile.value.phone,
            businessType = _vendorProfile.value.businessType,
            city = _vendorProfile.value.primaryCity,
            docType = docType,
            docNumber = docNumber,
            fileName = fileName,
            uploadSource = uploadSource,
            submittedDate = "Today",
            status = VerificationStatus.UNDER_REVIEW
        )
        _adminVerificationQueue.value = listOf(submission) + _adminVerificationQueue.value
    }

    // 6. Admin Approve Verification
    fun approveVerification(submissionId: String) {
        _adminVerificationQueue.value = _adminVerificationQueue.value.map { item ->
            if (item.submissionId == submissionId) {
                item.copy(status = VerificationStatus.VERIFIED)
            } else item
        }

        val submission = _adminVerificationQueue.value.find { it.submissionId == submissionId }
        if (submission != null && submission.vendorId == _vendorProfile.value.id) {
            val updatedDocs = _vendorProfile.value.verificationDocuments.map { doc ->
                if (doc.docType == submission.docType) {
                    doc.copy(
                        status = VerificationStatus.VERIFIED,
                        verifiedAt = "Today",
                        expiryDate = "31 Dec 2027",
                        rejectionReason = null
                    )
                } else doc
            }
            val badges = _vendorProfile.value.trustBadges.toMutableSet()
            badges.add(TrustBadge.VERIFIED)
            _vendorProfile.value = _vendorProfile.value.copy(
                verificationDocuments = updatedDocs,
                trustBadges = badges,
                isVerified = true
            )
        }
    }

    // 7. Admin Reject / Disapprove Verification
    fun rejectVerification(submissionId: String, reason: String) {
        _adminVerificationQueue.value = _adminVerificationQueue.value.map { item ->
            if (item.submissionId == submissionId) {
                item.copy(status = VerificationStatus.REJECTED, rejectionReason = reason)
            } else item
        }

        val submission = _adminVerificationQueue.value.find { it.submissionId == submissionId }
        if (submission != null && submission.vendorId == _vendorProfile.value.id) {
            val updatedDocs = _vendorProfile.value.verificationDocuments.map { doc ->
                if (doc.docType == submission.docType) {
                    doc.copy(
                        status = VerificationStatus.REJECTED,
                        rejectionReason = reason
                    )
                } else doc
            }
            _vendorProfile.value = _vendorProfile.value.copy(verificationDocuments = updatedDocs)
        }
    }

    // 8. Admin Revoke / Expire Verification
    fun revokeVerification(vendorId: String) {
        if (vendorId == _vendorProfile.value.id) {
            val updatedDocs = _vendorProfile.value.verificationDocuments.map { doc ->
                doc.copy(status = VerificationStatus.EXPIRED, rejectionReason = "Verification expired / re-audit required")
            }
            val badges = _vendorProfile.value.trustBadges.toMutableSet()
            badges.remove(TrustBadge.VERIFIED)
            _vendorProfile.value = _vendorProfile.value.copy(
                verificationDocuments = updatedDocs,
                trustBadges = badges,
                isVerified = false
            )
        }
    }

    // 9. Send Chat Message
    fun sendMessage(
        eventId: String,
        eventTitle: String,
        text: String,
        attachmentName: String? = null,
        attachmentType: String? = null
    ) {
        val role = _currentRole.value
        val isCustomer = (role == UserRole.CUSTOMER)
        val newMsg = ChatMessage(
            id = "MSG-${System.currentTimeMillis()}",
            eventId = eventId,
            eventTitle = eventTitle,
            senderId = if (isCustomer) "CUST-01" else "VEND-01",
            senderName = if (isCustomer) "Priya Sharma" else "Dream Decor",
            senderRole = role,
            recipientId = if (isCustomer) "VEND-01" else "CUST-01",
            text = text,
            attachmentName = attachmentName,
            attachmentType = attachmentType,
            timestamp = "Just now",
            isRead = true,
            isFromCustomer = isCustomer
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    // 10. Upgrade / Purchase Subscription (Packages: 199, 299, 399, 499, 1999)
    fun upgradeSubscription(tier: SubscriptionTier) {
        _vendorSubscription.value = VendorSubscription(
            tier = tier,
            remainingViews = tier.maxOpportunityViews,
            remainingBids = tier.maxBids,
            billingCycleEnd = "30 Days from today",
            isActive = true
        )

        // Generate Subscription Tax Invoice
        val base = tier.monthlyPriceInr
        val tax = (base * 0.18).toLong()
        val invoice = PlatformInvoice(
            invoiceNumber = "INV-SUB-2026-${(1000..9999).random()}",
            issueDate = "Today",
            dueDate = "30 Days from today",
            billedToName = "${_vendorProfile.value.businessName} (${_vendorProfile.value.ownerName})",
            billedToEmail = _vendorProfile.value.email,
            billedToGstin = "36AABCU9876Q1Z2",
            billedToCity = _vendorProfile.value.primaryCity,
            type = "VENDOR_SUBSCRIPTION",
            items = listOf(
                InvoiceLineItem("Vendor Subscription — ${tier.planName} (${tier.maxOpportunityViews} Lead Views, ${tier.maxBids} Bids)", "998599", 1, base, base)
            ),
            subTotal = base,
            cgstAmount = tax / 2,
            sgstAmount = tax / 2,
            totalAmount = base + tax,
            status = "PAID",
            paymentMode = "UPI / NetBanking Instant",
            referenceId = "PAY-SUB-${base}-${(1000..9999).random()}"
        )
        _invoices.value = listOf(invoice) + _invoices.value
    }

    // 11. Purchase Campaign
    fun purchaseCampaign(campaignType: CampaignType, targetCity: String, targetCategory: String): CampaignItem {
        val newCampaign = CampaignItem(
            id = "CAMP-${(400..999).random()}",
            vendorId = _vendorProfile.value.id,
            vendorName = _vendorProfile.value.businessName,
            campaignType = campaignType,
            targetCity = targetCity,
            targetCategory = targetCategory,
            status = "Active",
            impressions = 0,
            clicks = 0,
            enquiries = 0,
            startDate = "Today",
            endDate = "${campaignType.durationDays} Days from today",
            spendInr = campaignType.priceInr
        )
        _campaigns.value = listOf(newCampaign) + _campaigns.value

        // Generate Campaign Invoice
        val base = campaignType.priceInr
        val tax = (base * 0.18).toLong()
        val invoice = PlatformInvoice(
            invoiceNumber = "INV-AD-2026-${(1000..9999).random()}",
            issueDate = "Today",
            dueDate = "Today",
            billedToName = _vendorProfile.value.businessName,
            billedToEmail = _vendorProfile.value.email,
            billedToGstin = "36AABCU9876Q1Z2",
            billedToCity = _vendorProfile.value.primaryCity,
            type = "CAMPAIGN_FEE",
            items = listOf(
                InvoiceLineItem("Promotional Campaign: ${campaignType.title} (${campaignType.durationDays} Days)", "998599", 1, base, base)
            ),
            subTotal = base,
            cgstAmount = tax / 2,
            sgstAmount = tax / 2,
            totalAmount = base + tax,
            status = "PAID",
            paymentMode = "UPI / Card",
            referenceId = "PAY-CAMP-${(10000..99999).random()}"
        )
        _invoices.value = listOf(invoice) + _invoices.value

        return newCampaign
    }
}


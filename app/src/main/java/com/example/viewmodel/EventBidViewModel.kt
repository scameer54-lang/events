package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.EventBidRepository
import com.example.model.*
import kotlinx.coroutines.flow.StateFlow

class EventBidViewModel(
    val repository: EventBidRepository = EventBidRepository()
) : ViewModel() {

    val currentRole: StateFlow<UserRole> = repository.currentRole
    val currentUser: StateFlow<AppUser?> = repository.currentUser
    val usersList: StateFlow<List<AppUser>> = repository.usersList
    val events: StateFlow<List<EventItem>> = repository.events
    val bids: StateFlow<List<BidItem>> = repository.bids
    val vendorProfile: StateFlow<VendorProfile> = repository.vendorProfile
    val vendorSubscription: StateFlow<VendorSubscription> = repository.vendorSubscription
    val campaigns: StateFlow<List<CampaignItem>> = repository.campaigns
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val adminVerificationQueue = repository.adminVerificationQueue
    val publicEvents: StateFlow<List<PublicEventListing>> = repository.publicEvents
    val myTickets: StateFlow<List<EventTicket>> = repository.myTickets
    val invoices: StateFlow<List<PlatformInvoice>> = repository.invoices
    val agreements: StateFlow<List<EventServiceAgreement>> = repository.agreements

    fun formatInr(amount: Long) = repository.formatInr(amount)

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
    }

    fun signInWithOtp(phone: String, otp: String): Boolean {
        return repository.signInWithOtp(phone, otp)
    }

    fun signInWithGoogle(name: String, email: String) {
        repository.signInWithGoogle(name, email)
    }

    fun registerAccount(name: String, email: String, phone: String, role: UserRole) {
        repository.registerAccount(name, email, phone, role)
    }

    fun signOutUser() {
        repository.signOutUser()
    }

    fun toggleUserPremium(userId: String) {
        repository.toggleUserPremium(userId)
    }

    fun resetUserSession(userId: String) {
        repository.resetUserSession(userId)
    }

    fun deleteUser(userId: String) {
        repository.deleteUser(userId)
    }

    fun bookEventTicket(event: PublicEventListing, ticketType: String, price: Long, qty: Int): EventTicket {
        return repository.bookTicket(event, ticketType, price, qty)
    }

    fun postEvent(
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
        return repository.createEvent(
            title = title,
            eventType = eventType,
            city = city,
            venueArea = venueArea,
            eventDate = eventDate,
            guestCount = guestCount,
            budget = budget,
            services = services,
            description = description
        )
    }

    fun submitBid(
        eventId: String,
        price: Long,
        inclusions: List<String>,
        exclusions: List<String>,
        validityDays: Int,
        deliveryNotes: String
    ): Boolean {
        return repository.submitBid(
            eventId = eventId,
            price = price,
            inclusions = inclusions,
            exclusions = exclusions,
            validityDays = validityDays,
            deliveryNotes = deliveryNotes
        )
    }

    fun shortlistBid(bidId: String) {
        repository.shortlistBid(bidId)
    }

    fun acceptBid(bidId: String, eventId: String) {
        repository.acceptBid(bidId, eventId)
    }

    fun uploadDocument(
        docType: VerificationDocType,
        docNumber: String,
        fileName: String,
        uploadSource: UploadSource = UploadSource.FILE
    ) {
        repository.uploadVendorDocument(docType, docNumber, fileName, uploadSource)
    }

    fun adminApproveVerification(submissionId: String) {
        repository.approveVerification(submissionId)
    }

    fun adminRejectVerification(submissionId: String, reason: String) {
        repository.rejectVerification(submissionId, reason)
    }

    fun adminRevokeVerification(vendorId: String) {
        repository.revokeVerification(vendorId)
    }

    fun sendChatMessage(
        eventId: String,
        eventTitle: String,
        text: String,
        attachmentName: String? = null,
        attachmentType: String? = null
    ) {
        repository.sendMessage(eventId, eventTitle, text, attachmentName, attachmentType)
    }

    fun upgradeSubscription(tier: SubscriptionTier) {
        repository.upgradeSubscription(tier)
    }

    fun purchaseCampaign(campaignType: CampaignType, city: String, category: String) {
        repository.purchaseCampaign(campaignType, city, category)
    }
}

# EventBid — Event E-Procurement & Bidding Platform (India)
## Comprehensive Technical & Product Specification (Deliverables 1 – 16)

---

### 1. Product Requirements Document (PRD)

#### 1.1 Executive Summary
EventBid is an e-procurement marketplace engineered specifically for India's ₹50,000+ Crore wedding and event industry. It connects event hosts (customers) with verified event vendors (decor, catering, photography, sound/lighting, venues, entertainment) through a transparent, competitive reverse-bidding system, backed by monthly vendor subscriptions and high-visibility promotional campaigns.

#### 1.2 Core Value Propositions
- **For Customers:** Post event requirements once; receive structured, itemized, binding quotations; compare verified credentials and trust badges side-by-side; negotiate via real-time event-linked chat; hire without middlemen commissions.
- **For Vendors:** Predictable monthly subscription with guaranteed opportunity access; zero commission on event volume; fair bidding rules preventing price undercutting wars; promotional campaign marketplace for seasonal surges.
- **For Platform & Admin:** High-margin recurring SaaS revenue (₹499 – ₹1,999/mo), incremental ad campaign revenue (₹1,099 – ₹5,199/slot), central verification authority (GSTIN/MSME validation).

---

### 2. MVP Feature List & Priorities (P0, P1, P2)

| Feature Area | Priority | Scope Details |
|---|---|---|
| Customer Event Posting | **P0** | Multi-step requirement wizard: Type, City, Venue, Date, Guest count, Budget, Services |
| Vendor Onboarding & Quota | **P0** | Profile registration, operating cities, monthly views & bid submission counter |
| Bidding Engine | **P0** | Price in ₹, Inclusions, Exclusions, Validity in days, server-side quota deduction |
| Quotations Comparison | **P0** | Side-by-side compare, shortlist status, accept/confirm booking terms |
| Vendor Verification System | **P0** | Upload GSTIN, MSME, PAN, Trade License; Admin Review queue (Approve/Reject) |
| Trust Badges | **P0** | Visual "Verified", "Top Rated", "Customer Recommended" badges with criteria modal |
| Real-time Event-Linked Chat | **P0** | Direct messaging tied to specific event requirement, file sharing (PDF/images), canned replies |
| Subscription Engine | **P0** | Starter (₹499/mo), Growth (₹999/mo), Premium (₹1,999/mo) with simulated Razorpay/UPI |
| Campaign Marketplace | **P1** | Fixed-price featured listings (7/15/30 days), banner placement, CTR/impression stats |
| Admin Dispute & Moderation | **P1** | Event post approval, vendor suspension, verification revocation & expiration handling |
| Escrow Milestone Payouts | **P2** | Automated bank split transfers, multi-vendor combined contracts, AI vendor score |

---

### 3. User Personas & User Stories with Acceptance Criteria

#### Persona A: Priya Sharma (Customer / Bride-to-be, Hyderabad)
* **Goal:** Wants a grand marigold mandap and fairy light entrance arch at Taj Krishna for 450 guests within ₹2,00,000 budget.
* **User Story:** As an organizer, I want to compare quotes from government-verified decorators so that I don't risk last-minute vendor no-shows.
* **Acceptance Criteria:**
  1. Must show vendor GSTIN verified status badge.
  2. Quotations must state validity period and package exclusions.
  3. Direct messaging must be enabled upon shortlisting.

#### Persona B: Rajesh Kumar (Vendor / Managing Director, Dream Decor, Bangalore)
* **Goal:** Wants regular corporate and wedding decoration leads without paying 20% commission to event aggregators.
* **User Story:** As a vendor, I want to know my remaining bid quota and receive instant updates when shortlisted.
* **Acceptance Criteria:**
  1. Quota decrements strictly upon successful bid submission.
  2. Bids can be submitted only within event bidding window.
  3. Can attach portfolio PDF decks and stage lighting photos in chat.

#### Persona C: Operations Desk (EventBid Admin)
* **Goal:** Verify vendor legitimacy, audit submitted documents, review customer feedback.
* **User Story:** As an admin, I want to review GSTIN certificates against MCA data and approve or reject with reasons.
* **Acceptance Criteria:**
  1. Document status updates to `VERIFIED` or `REJECTED` with audit log.
  2. Revoking vendor removes the "Verified" badge immediately across all live quotations.

---

### 4. Database Schema (PostgreSQL / Relational Model)

```sql
-- Storing money as integer paise (1 INR = 100 paise)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'VENDOR', 'ADMIN', 'OPERATIONS')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE vendor_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(150) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    primary_city VARCHAR(50) NOT NULL,
    operating_cities TEXT[] NOT NULL,
    rating NUMERIC(2,1) DEFAULT 5.0,
    review_count INT DEFAULT 0,
    completed_events INT DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    is_top_rated BOOLEAN DEFAULT FALSE,
    is_customer_recommended BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE vendor_verification_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    doc_type VARCHAR(50) NOT NULL CHECK (doc_type IN ('GSTIN', 'UDYAM_MSME', 'PAN_CARD', 'TRADE_LICENSE', 'BANK_CHEQUE')),
    doc_number VARCHAR(100) NOT NULL,
    file_storage_url TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNDER_REVIEW' CHECK (status IN ('NOT_SUBMITTED', 'UNDER_REVIEW', 'VERIFIED', 'REJECTED', 'EXPIRED')),
    rejection_reason TEXT,
    verified_at TIMESTAMP WITH TIME ZONE,
    expiry_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID REFERENCES users(id) ON DELETE RESTRICT,
    title VARCHAR(200) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    venue_area VARCHAR(150) NOT NULL,
    event_date DATE NOT NULL,
    guest_count INT NOT NULL,
    estimated_budget_paise BIGINT NOT NULL,
    services_required TEXT[] NOT NULL,
    description TEXT NOT NULL,
    bidding_deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'BIDDING_OPEN' CHECK (status IN ('PENDING_APPROVAL', 'BIDDING_OPEN', 'SHORTLISTED', 'BOOKED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE bids (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID REFERENCES events(id) ON DELETE CASCADE,
    vendor_id UUID REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    price_paise BIGINT NOT NULL,
    inclusions TEXT[] NOT NULL,
    exclusions TEXT[] NOT NULL,
    validity_days INT NOT NULL,
    delivery_notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'SHORTLISTED', 'ACCEPTED', 'REJECTED')),
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT unique_vendor_per_event_bid UNIQUE(event_id, vendor_id)
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    plan_tier VARCHAR(30) NOT NULL CHECK (plan_tier IN ('STARTER', 'GROWTH', 'PREMIUM')),
    monthly_price_paise INT NOT NULL,
    remaining_views INT NOT NULL,
    remaining_bids INT NOT NULL,
    billing_cycle_end TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID REFERENCES events(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE RESTRICT,
    recipient_id UUID REFERENCES users(id) ON DELETE RESTRICT,
    message_text TEXT NOT NULL,
    attachment_url TEXT,
    attachment_type VARCHAR(20),
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vendor_id UUID REFERENCES vendor_profiles(id) ON DELETE CASCADE,
    campaign_type VARCHAR(50) NOT NULL,
    target_city VARCHAR(50) NOT NULL,
    target_category VARCHAR(50) NOT NULL,
    price_paise BIGINT NOT NULL,
    impressions INT DEFAULT 0,
    clicks INT DEFAULT 0,
    enquiries INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);
```

---

### 5. Vendor Trust Criteria & Verification Rules

1. **"Verified" (Green Shield):**
   - Requires verified GSTIN with active GST status on the Goods & Services Tax Network portal.
   - Requires MSME Udyam registration or valid Trade License.
   - Annual re-verification required; automatic expiry after 365 days.
   - If revoked, the badge is stripped immediately from all quotes and search listings.
2. **"Top Rated" (Gold Star):**
   - Minimum 15 completed, customer-reviewed bookings on EventBid.
   - Sustained cumulative rating of 4.8★ or higher.
3. **"Customer Recommended" (Indigo Heart/Thumb):**
   - 98%+ customer re-hire recommendation rating.
   - Zero unresolved customer disputes in the preceding 6 months.

---

### 6. India-Focused Budget & Stack Recommendation

- **Stack:** Jetpack Compose (Android) + Next.js / TypeScript Web + Supabase / PostgreSQL + Razorpay API + WhatsApp Business Cloud API.
- **Monthly Cloud Run / Hosting:** ₹2,500 – ₹4,000 / month on GCP Cloud Run / Supabase Pro.
- **Estimated Payback Period:** With 50 paying vendors at ₹999/mo (₹49,950 MRR), infrastructure costs are covered in Month 1 with 85%+ gross profit margins.

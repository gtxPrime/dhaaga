# ShilpSetu — Complete E-Commerce Feature Document
### Smart India Hackathon 2026 | AI-Powered Artisan Commerce Platform
> **"From village craft to global cart — in your own language, in under 5 minutes."**

---

## Table of Contents

1. [Platform Overview & Architecture](#1-platform-overview--architecture)
2. [Seller (Artisan) App — Full Feature Set](#2-seller-artisan-app--full-feature-set)
3. [Buyer App — Full Feature Set](#3-buyer-app--full-feature-set)
4. [Admin Web Panel — Full Feature Set](#4-admin-web-panel--full-feature-set)
5. [Core E-Commerce Engine](#5-core-e-commerce-engine)
6. [Cherry Features (AI-Powered Differentiators)](#6-cherry-features-ai-powered-differentiators)
7. [Gemini API Integration Map](#7-gemini-api-integration-map)
8. [Full Tech Stack](#8-full-tech-stack)
9. [Database Schema (Expanded)](#9-database-schema-expanded)
10. [Impact Goals & Success Metrics](#10-impact-goals--success-metrics)
11. [Implementation Roadmap](#11-implementation-roadmap)

---

## 1. Platform Overview & Architecture

### 1.1 Problem Statement
India has over **7 million registered artisans, weavers, and handicraft makers**. Despite government support through fairs (Shilp Samagam, Surajkund Mela, Dilli Haat), artisans have no reliable digital sales channel between events — not because markets don't exist, but because the barrier to entering digital commerce is too high.

| Pain Point | Root Cause | Impact |
|---|---|---|
| Cannot photograph products professionally | No studio, no skills, no equipment | Listings look uncompetitive |
| Cannot write product descriptions | Low English literacy, no copywriting knowledge | Poor SEO, low discoverability |
| Cannot price competitively | No access to market benchmarks | Either undersell or lose buyers |
| Cannot navigate GeM/ONDC portals | Complex UI, English-only, multi-step | Exclusion from govt marketplaces |
| Cannot reach B2B buyers year-round | Dependent on physical fair schedules | Feast-or-famine income cycle |

### 1.2 The Three-Interface Philosophy

```
┌──────────────────────────────────────────────────────────────────┐
│                      SHILPSETU PLATFORM                          │
├──────────────┬───────────────────────┬───────────────────────────┤
│  SELLER APP  │      BUYER APP        │    ADMIN WEB PANEL        │
│  Flutter     │      Flutter          │    React 18               │
│  Android     │      Android          │    Firebase Hosting       │
│  Artisan UI  │      Consumer UI      │    Government/Ops UI      │
├──────────────┴───────────────────────┴───────────────────────────┤
│                   FIREBASE (100% Serverless)                     │
│  Auth │ Firestore │ Storage │ Cloud Functions │ FCM │ Hosting     │
├──────────────────────────────────────────────────────────────────┤
│  Analytics │ Crashlytics │ Remote Config │ Performance │ App Dist │
├──────────────────────────────────────────────────────────────────┤
│     External APIs (called only from Cloud Functions)             │
│  Gemini API │ Bhashini │ SerpAPI │ Razorpay │ Delhivery           │
└──────────────────────────────────────────────────────────────────┘
```

### 1.3 User Personas

| Persona | App | Literacy Level | Primary Language | Key Needs |
|---|---|---|---|---|
| Artisan / Seller | Seller App | Low-medium | Regional (Hindi, Bengali, Tamil etc.) | List products fast, earn fairly |
| Urban Consumer | Buyer App | High | English/Hindi | Authentic handmade products, easy checkout |
| B2B Buyer | Buyer App | High | English | Bulk orders, verified quality, GI tagged |
| Platform Admin | Web Panel | High | English | Manage users, analytics, disputes |
| Govt Officer | Web Panel | High | English | Policy data, artisan welfare metrics |

---

## 2. Seller (Artisan) App — Full Feature Set

### 2.1 Onboarding & Authentication

- **WhatsApp OTP Login** — Phone number as primary identity, OTP via Twilio Verify
- **Voice-first Profile Setup** — Speak your name, craft type, and location in your language (Bhashini STT)
- **Aadhaar-linked Verification** (optional) — For trusted seller badge and GeM eligibility
- **Udyam / MSME Registration Upload** — Unlock B2B features
- **Language Selection** — 22 scheduled Indian languages, persisted to profile
- **Biometric App Lock** — Fingerprint/Face unlock for app security
- **Multi-device Sync** — Same account works on tablet + phone

### 2.2 Product Management

#### 2.2.1 Listing Creation (The 5-Minute Flow)
1. **Camera Capture / Gallery Upload** — Multi-photo (up to 10 images)
2. **AI Image Studio** — Gemini Vision auto-enhances photos (see Section 6)
3. **Voice Description** — Artisan speaks in their language → Bhashini STT → Gemini extracts structured data
4. **Auto-fill Attributes** — Craft type, material, color, size, technique, region auto-extracted
5. **Smart Pricing** — Market comparison with Amazon/Flipkart/Meesho/GeM (SerpAPI)
6. **GI Tag Auto-detect** — Region + craft matched against 400+ GI database
7. **One-tap Publish** — Listing goes live with SEO title, 3 image variants, pricing

#### 2.2.2 Product Catalog Features
- **Bulk Upload** — CSV or batch photos for artisans with large inventories
- **Product Variants** — Size, color, material variants under one listing
- **Stock Management** — Set quantity, low-stock alerts, auto-delist on zero stock
- **Draft Saving** — Save incomplete listings, resume anytime
- **Listing Templates** — Save product templates for recurring craft types
- **Duplicate & Edit** — Clone a listing to create similar products fast
- **Product Status** — Draft / Active / Paused / Sold / GeM Submitted / ONDC Listed
- **Expiry / Limited Edition** — Time-bound listings for festive editions
- **Minimum Order Quantity** — Set MOQ for wholesale buyers
- **Custom Attributes** — Artisan-specific fields (e.g., dyeing technique, loom type)

#### 2.2.3 Photo & Media Management
- **AI Background Removal** — rembg (U2-Net) isolates product from cluttered background
- **3 Auto-generated Image Variants** — White studio / Lifestyle mockup / Festive seasonal
- **Real-ESRGAN Upscaling** — WhatsApp-compressed images upscaled 4x
- **CLIP Quality Gating** — Low-quality photos rejected with reason and retake prompt
- **360° Product View** — Multi-angle photos stitched into swipeable gallery
- **Video Demo Upload** — Short product demo video (up to 60 seconds)
- **Auto-Generated Video Reel** — 15-second social media reel (FFmpeg + Gemini)

### 2.3 Seller Dashboard (Home Screen)

- **Earnings Overview** — Today / This Week / This Month / All Time with charts
- **Order Summary Widget** — Pending / Confirmed / Shipped / Delivered counts
- **Live Listing Count** — Active listings at a glance
- **Shilpi Score Card** — Trust score with component breakdown and improvement tips
- **Price Alerts** — Listings priced >15% below market highlighted in red
- **Notifications Centre** — New orders, buyer inquiries, price alerts, GI tag news
- **Quick Actions** — Add Product / Check Orders / Price Check / Share Store
- **Mela Mode Toggle** — Switch to offline-first mode at fairs

### 2.4 Order Management

- **Order Inbox** — All orders with status, buyer info, product, amount
- **Order Timeline** — Visual status tracker: Placed → Confirmed → Packed → Shipped → Delivered
- **Accept / Reject Orders** — With reason (out of stock, custom unavailable, etc.)
- **Packing Slip Generator** — Auto-generated printable packing slip (PDF)
- **Shipping Label** — Auto-printed label with buyer address and order ID barcode
- **Logistics Integration** — Delhivery / Shiprocket / India Post API for pickup scheduling
- **COD Management** — Cash on Delivery tracking with verification photo upload
- **Bulk Order Handling** — B2B large orders with custom fulfilment timeline
- **Cancellation & Refund Flow** — Seller-side cancellation with reason and auto-refund trigger
- **Delivery Confirmation** — Photo upload proof of handover to courier
- **Returns Management** — Accept/dispute returns with photo evidence

### 2.5 Seller Financials & Payments

- **Earnings Wallet** — Platform wallet with real-time balance
- **UPI Payouts** — Auto-settle to UPI ID on order completion (T+2 days)
- **Bank Account Linking** — NEFT/RTGS payout option
- **Commission Breakdown** — Transparent fee: platform fee (%), payment gateway fee (%), GST
- **GST Invoice Generation** — Auto-generated compliant GST invoice for each sale
- **TDS Certificate** — Auto-generated for sellers above threshold
- **Annual Earnings Report** — Downloadable PDF for tax filing
- **Advance Payment Option** — 50% advance on B2B bulk orders
- **Dispute Resolution** — Raise payment disputes with evidence

### 2.6 Store & Profile

- **Custom Storefront URL** — `shilpsetu.in/store/artisan-name`
- **Artisan Story Section** — Kahaani AI-generated cultural narrative (Section 6)
- **Portfolio Gallery** — Curated showcase of best works
- **Craft Heritage Badge** — Displayed craft type, region, years of experience
- **GI Certified Badge** — Gold badge on verified GI products
- **Shilpi Score Display** — Public trust score visible to buyers
- **Social Links** — Instagram / YouTube / Facebook profile links
- **Business Hours** — Set availability for custom orders
- **Followers** — Buyers can follow artisans for new product notifications
- **Artisan Certificate Upload** — Ministry of Textiles, KVIC, state crafts council certificates

### 2.7 Analytics & Insights

- **Product Performance** — Views, wishlists, inquiries, conversions per listing
- **Traffic Sources** — Direct / ONDC / GeM / Social share
- **Search Keyword Ranking** — What keywords buyers use to find products
- **Competitor Pricing Tracker** — Anonymous comparison with similar products
- **Peak Sales Periods** — When buyers are most active (time/day heatmap)
- **Geographic Reach** — Map showing where buyers are located
- **Return Rate** — Product-wise return analysis
- **Revenue Forecast** — AI-predicted next month earnings based on trends

### 2.8 Marketing & Promotion Tools

- **Auto-generated Social Posts** — Gemini creates caption + hashtags for Instagram/Facebook
- **Video Reel Share** — 15-second product reel (Instagram Reels / WhatsApp Status format)
- **Festival Sale Planner** — AI suggests when to run discounts (Diwali, Eid, Christmas)
- **Coupon Creator** — Generate discount codes for repeat buyers
- **Refer & Earn** — Earn credit for referring other artisans to the platform
- **Share Store Button** — One-tap copy storefront link for WhatsApp/Instagram sharing
- **GeM One-Tap Submission** — Submit compliant listing to GeM portal (Section 6)
- **ONDC Distribution** — Auto-list on all ONDC buyer apps (Meesho, Paytm, etc.)

### 2.9 Seller Support

- **In-App Help Center** — Searchable FAQ in 22 languages
- **Voice Support Chat** — Speak query in regional language, get spoken answer (Gemini + Bhashini TTS)
- **Video Tutorials** — Short how-to videos in regional languages
- **Grievance Portal** — Raise ticket with order/product ID, track resolution
- **Community Forum** — Connect with other artisans in your craft category
- **Government Schemes Advisor** — AI shows applicable schemes (PM Vishwakarma, SFURTI, etc.)

---

## 3. Buyer App — Full Feature Set

### 3.1 Discovery & Browse

- **Personalised Home Feed** — Gemini-powered recommendations based on browse history and preferences
- **Trending in Your City** — Locally trending handmade products
- **New Arrivals** — Latest listings from followed artisans
- **Featured Collections** — Curated thematic collections (e.g., "Warli Art", "Handloom Silks", "Tribal Jewellery")
- **GI Certified Products** — Filter for government-verified authentic crafts
- **Artisan Spotlight** — Weekly featured artisan story with their products
- **Festival Collections** — Seasonal curation (Diwali gifts, Holi hampers, Wedding season)
- **Flash Sales** — Time-limited discounts with countdown timer
- **Category Browsing** — Paintings / Textiles / Pottery / Jewellery / Woodwork / Metalwork / Leather / Food & Spices

### 3.2 Search & Filtering

- **Semantic Search** — "Blue Warli painting for living room" → Gemini Embeddings match exact intent
- **Voice Search** — Speak what you want in Hindi/English, Bhashini STT converts to query
- **Image Search** — Upload a photo, find similar handmade products (Gemini Vision)
- **Multi-filter System**:
  - Price range slider
  - Craft type (multi-select)
  - Region / State of origin
  - Material (cotton, silk, bamboo, terracotta, etc.)
  - GI Tag certified only
  - Seller rating (Shilpi Score)
  - Shipping time (Express / Standard / Economy)
  - Custom order available
  - In stock / Pre-order
- **Sort Options** — Relevance / Price (low-high, high-low) / Newest / Best Rated / Most Popular
- **Saved Searches** — Save frequent search queries with notifications for new results
- **Search History** — Recent searches with quick re-search

### 3.3 Product Detail Page

- **High-resolution Image Gallery** — Swipeable, zoomable, 360° view
- **Craft Story** — Kahaani AI-generated cultural narrative about the product
- **Artisan Profile Card** — Click through to full artisan storefront
- **Shilpi Score Badge** — Buyer confidence indicator
- **GI Tag Certificate** — View official GI certificate if applicable
- **Pricing Breakdown** — Product price + platform fee + shipping = total
- **Delivery Estimate** — Pin-code based delivery date (Delhivery API)
- **Size Guide** — Where applicable (textiles, jewellery)
- **Material & Care Instructions** — AI-generated from product attributes
- **Similar Products** — pgvector similarity-matched alternatives
- **"Other Works by This Artisan"** — Cross-sell carousel
- **Buyer Reviews & Photos** — Star ratings with verified purchase photos
- **Q&A Section** — Ask seller questions, public Q&A visible to all buyers
- **Wishlist / Save** — Heart icon saves to wishlist
- **Share Product** — Share link to product (WhatsApp, Instagram, copy link)
- **Price History Chart** — See how price has changed over time
- **Custom Order Request** — "Contact Artisan" for bespoke commissions
- **Wholesale Enquiry** — For bulk buyers (B2B tab visible above MOQ)

### 3.4 Cart & Checkout

- **Smart Cart** — Multi-seller cart with consolidated checkout
- **Cart Persistence** — Cart saved across devices and sessions
- **Save for Later** — Move items from cart to wishlist
- **Coupon / Promo Code** — Apply discount codes
- **Gifting Mode** — Add gift message + gift wrap option (extra charge)
- **Checkout Flow**:
  1. Address selection / entry
  2. Delivery slot selection (Express / Standard)
  3. Payment method selection
  4. Review order summary
  5. Place order with one tap
- **Address Book** — Save multiple delivery addresses
- **Google Maps Address Picker** — Pin location for accurate delivery
- **Guest Checkout** — Buy without account (OTP verification)
- **Order Confirmation** — In-app + SMS + Email confirmation with order ID

### 3.5 Payment Methods

- **UPI** — PhonePe, GPay, BHIM, Paytm UPI
- **UPI AutoPay** — For subscription boxes / recurring orders
- **Credit/Debit Card** — Razorpay/Stripe PG
- **Net Banking** — All major Indian banks
- **EMI** — No-cost EMI on orders above ₹3,000 (Bajaj Finserv / bank EMI)
- **Pay Later** — Simpl / LazyPay Buy Now Pay Later
- **Cash on Delivery** — Available for orders up to ₹5,000
- **Platform Wallet** — Refunds credited, spend in future orders
- **Reward Points** — Earn 1 point per ₹10 spent, redeem in future
- **International Cards** — For NRI/overseas buyers (Stripe)

### 3.6 Orders & Tracking

- **My Orders** — All orders with status
- **Real-time Tracking** — Delhivery/Shiprocket live shipment map
- **Order Timeline** — Placed → Confirmed → Packed → Dispatched → Out for Delivery → Delivered
- **Push Notifications** — Status updates at each stage
- **SMS/WhatsApp Alerts** — Alternative to push notifications
- **Download Invoice** — GST invoice PDF
- **Easy Returns** — 7-day return window with photo proof
- **Refund Tracker** — Track refund status and expected credit date
- **Cancel Order** — Before dispatch with instant refund

### 3.7 Post-Purchase

- **Review & Rate** — Star rating + text review + photo upload
- **Review Guidelines** — AI moderation of reviews (Gemini Vision for photos)
- **Review Response** — Artisan can reply to reviews
- **Order Again** — One-tap reorder of past purchases
- **Product Received Alert** — "Did your order arrive?" prompt after delivery date
- **Gift Review** — Special review flow for gifted items

### 3.8 Buyer Profile & Personalisation

- **Profile Setup** — Name, photo, preferences, saved addresses
- **Interest Tags** — Select craft categories you love (used for personalisation)
- **Wishlist** — Multiple named wishlists (e.g., "Diwali Gifts", "Home Decor")
- **Following** — Follow artisans for new product notifications
- **Purchase History** — All past orders with re-buy option
- **Reward Points Balance** — Points earned, spent, available
- **Referral Program** — Share link, earn ₹50 credit per new buyer
- **Notification Preferences** — Choose what alerts you want
- **Privacy Settings** — Control data sharing preferences

### 3.9 Social & Community Features

- **Buyer Collections** — Curate and share a themed collection of products
- **Community Board** — Public photo wall of buyers using artisan products
- **Artisan Ambassador Program** — Top buyers get exclusive early access and discounts
- **"Spotted in Wild" Stories** — Share photos using your handmade purchases

### 3.10 B2B Buyer Features

- **Business Profile** — Register as B2B buyer with org details and sourcing criteria
- **Bulk Quote Request** — Request custom pricing for quantities above MOQ
- **Sample Order** — Order 1-2 pieces before placing bulk order
- **Udyog Mitra Matchmaking** — AI matches buyer requirements to best-fit artisans
- **Credit Terms** — Net-30 payment terms for verified businesses
- **Purchase Orders** — Upload PO document for formal procurement
- **GST Billing** — B2B GST invoices with GSTIN of both parties
- **Dedicated Account Manager** — For enterprise buyers (>₹5L/year)
- **Contract Templates** — Standard supply agreement templates
- **Quality Certification** — Request pre-shipment quality inspection

---

## 4. Admin Web Panel — Full Feature Set

> Built as a React web application accessible at `admin.shilpsetu.in`

### 4.1 Dashboard Overview

- **Real-time Platform KPIs** — Total GMV, Active Sellers, Active Buyers, Orders Today, Avg Order Value
- **Revenue Chart** — Platform commission earned (daily/weekly/monthly/YoY)
- **Growth Metrics** — New seller registrations, new buyer signups, listing growth
- **Geographic Heat Map** — Seller and buyer concentration across India
- **Category Performance** — Top-selling craft categories with revenue share
- **SLA Monitor** — Order fulfilment SLA compliance (% delivered on time)
- **System Health** — API response times, error rates, queue depth, server metrics

### 4.2 Seller Management

- **Seller List** — Searchable, filterable table of all registered sellers
- **Seller Profile View** — Full profile, listings, orders, Shilpi Score, payment history
- **Verification Queue** — Review and approve Aadhaar / MSME / craft council document uploads
- **KYC Status** — Track KYC completion across all sellers
- **Seller Performance** — Fulfillment rate, response time, return rate per seller
- **Suspend / Delist** — Deactivate sellers with reason code
- **Seller Notes** — Internal notes on seller accounts
- **Bulk Actions** — Mass approve/reject/message sellers
- **Export Data** — CSV export of seller data for government reporting
- **Payout Management** — Review, approve, and trigger seller payouts
- **Commission Configuration** — Set per-category commission rates

### 4.3 Buyer Management

- **Buyer List** — All registered buyers with purchase history summary
- **Buyer Profile** — Full purchase history, reviews, disputes, contact details
- **Fraud Detection** — Flag suspicious accounts (abnormal return rate, payment chargebacks)
- **Customer Segments** — Segment buyers by spend tier (Bronze/Silver/Gold/Platinum)
- **Loyalty Management** — Configure reward point rules and redemption ratios
- **Support Ticket View** — All buyer support tickets linked to buyer profile

### 4.4 Product & Catalog Management

- **Listing Review Queue** — New listings awaiting QC review (AI pre-scored, human final check)
- **Content Moderation** — Flag and review AI-moderated listings (misleading descriptions, wrong category)
- **Bulk Listing Actions** — Approve/reject/feature/delist multiple listings
- **Category Management** — Create/edit/merge craft categories and subcategories
- **Featured Listings** — Manually pin listings to homepage collections
- **GI Tag Administration** — Verify, approve, and revoke GI badges
- **Price Monitoring** — Flag listings with prices far outside market range (possible fraud/error)
- **Image Policy Enforcement** — Auto-flagged poor-quality images for admin review

### 4.5 Order Management

- **Order Dashboard** — All platform orders with status, value, seller, buyer
- **Order Timeline** — Full event log for any order
- **Dispute Management** — Buyer/Seller disputes with evidence, resolution tools
- **Escalation Queue** — Unresolved disputes escalated after SLA breach
- **Refund Management** — Approve/reject refund requests
- **COD Reconciliation** — Manage cash-on-delivery payment reconciliation with logistics
- **Shipping Configuration** — Set shipping rate tables, courier partner priority

### 4.6 Financial Management

- **Revenue Dashboard** — Gross Merchandise Value, Net Revenue, Commission Earned
- **Payout Ledger** — All seller payouts with status
- **Payment Gateway Reconciliation** — Match PG settlements with platform records
- **GST Filing Dashboard** — Aggregate GST data for platform's own filings
- **Escrow Management** — View funds in escrow per order status
- **Fee Configuration** — Platform fee, payment gateway fee, GST settings

### 4.7 Analytics & Reports

- **Sales Analytics** — GMV trends, AOV, category mix, geographic split
- **Seller Analytics** — Top sellers by GMV, fastest-growing, highest Shilpi Score
- **Buyer Analytics** — Repeat purchase rate, LTV, acquisition channel
- **Product Analytics** — Best sellers, most wishlisted, highest view-to-purchase rate
- **Search Analytics** — Top search queries, zero-result queries, search-to-purchase funnel
- **Pricing Analytics** — Aggregate pricing trends per craft category (exportable for MSDE/KVIC)
- **GI Tag Impact Report** — Revenue premium for GI-tagged products vs non-GI
- **Government Reporting** — Pre-formatted reports for Ministry of Textiles, KVIC, MSDE

### 4.8 GeM & ONDC Administration

- **GeM Submission Queue** — All pending/approved/rejected GeM submissions
- **ONDC Network Status** — Product sync health with ONDC network
- **GeM Compliance Monitor** — Track GeM mandatory field completion rates
- **ONDC Order Routing** — Orders received via ONDC buyer apps

### 4.9 AI & Content Configuration

- **Gemini Prompt Management** — Edit and A/B test AI prompts for descriptions, stories, pricing
- **Kahaani Craft Knowledge Base** — Add/edit craft heritage entries for story generation
- **GI Database** — Manage the GI registry database (add new GI tags as government registers them)
- **Keyword Library** — Manage SEO keyword database per craft category
- **Image Template Manager** — Upload/edit lifestyle and festive background templates for AI Image Studio
- **Music Library** — Manage royalty-free music tracks for video reel generator

### 4.10 Marketing & Campaigns

- **Campaign Manager** — Create platform-wide sale campaigns (Diwali Sale, Republic Day)
- **Push Notification Blasts** — Send targeted push notifications to buyer segments
- **WhatsApp Broadcast** — Send WhatsApp messages to seller segments
- **Coupon Management** — Create/monitor promotional coupon codes
- **Homepage Curation** — Drag-and-drop homepage banner and collection editor
- **A/B Testing** — Run product recommendation algorithm experiments
- **Email Campaign** — Newsletter management for B2B buyers

### 4.11 Platform Configuration

- **Commission Rate Settings** — Per-category, per-seller-tier commission management
- **Shipping Partner Config** — API keys and priority settings for Delhivery, Shiprocket, India Post
- **Payment Gateway Config** — Razorpay/Stripe settings, currency support
- **Language & Localisation** — Add/edit translation strings for new language support
- **Notification Templates** — Edit WhatsApp/SMS/Push notification message templates
- **Feature Flags** — Toggle platform features on/off per region or user segment
- **API Key Management** — Manage third-party API keys (Gemini, Bhashini, SerpAPI etc.)
- **Maintenance Mode** — Put platform in maintenance with custom message

### 4.12 Government & Impact Dashboard

- **Artisan Welfare Metrics** — Income increase per artisan, digital literacy improvement
- **Geographic Impact Map** — Artisan density, sales, and income gain per district
- **Scheme Uptake** — How many artisans benefited from govt scheme recommendations
- **GeM Penetration** — % of eligible artisans with active GeM listing
- **ONDC Network Contribution** — Total GMV routed through ONDC
- **Export to Ministry** — One-click export of impact data for parliamentary reporting

---

## 5. Core E-Commerce Engine

### 5.1 Product Catalog

- **Taxonomy Engine** — 3-level category hierarchy (Category → Subcategory → Craft Type)
- **Attribute Schema** — Flexible EAV model for craft-specific attributes
- **Multi-currency Support** — INR primary, USD/GBP/AED for NRI buyers
- **Multi-language Listings** — Every listing stored in English + Hindi + regional language
- **SEO Schema** — Auto-generated JSON-LD structured data (Google Shopping eligible)
- **Canonical URLs** — Proper URL structure for SEO
- **Sitemap Auto-generation** — For all active listings

### 5.2 Search Engine

- **Full-text Search** — PostgreSQL tsvector + GIN index for fast text search
- **Semantic Search** — pgvector cosine similarity on Gemini embedding of queries
- **Faceted Filtering** — Multi-dimensional filtering with result counts
- **Spell Correction** — "Madubani" → "Madhubani" auto-corrected
- **Synonym Expansion** — "Pottery" → also matches "Terracotta", "Earthenware"
- **Search Ranking** — Relevance × Shilpi Score × Listing Quality Score
- **Zero-result Recovery** — Suggest related categories or popular items when no results

### 5.3 Recommendation Engine

- **Collaborative Filtering** — "Buyers who liked this also bought..."
- **Content-based** — pgvector similarity between product embeddings
- **Gemini-powered Personalisation** — User preference model updated with each session
- **Trending Boost** — Products with >20% more views than usual in last 24h get boosted
- **Recency Boost** — New listings get temporary visibility boost
- **GI Tag Boost** — GI-certified products ranked higher in results

### 5.4 Pricing Engine

- **Dynamic Market Pricing** — SerpAPI scrapes Amazon, Flipkart, Meesho, GeM daily
- **ML Price Prediction** — scikit-learn Random Forest trained on 50,000+ handicraft listings
- **GI Premium Uplift** — Auto-applied 20-40% premium for GI-tagged products
- **Competitive Positioning** — Show artisan where they sit vs market (premium/mid/value)
- **Price History** — Store and display price change history per listing
- **Bulk Discount Engine** — Auto-calculate tiered bulk pricing (MOQ-based)
- **Festive Surge Pricing** — Alert artisans when similar products see festive demand spike

### 5.5 Logistics Engine

- **Multi-carrier Integration** — Delhivery, Shiprocket, India Post, BlueDart
- **Carrier Selection Logic** — Best carrier selected by: price × delivery time × serviceability
- **Pin-code Serviceability** — Real-time check if delivery address is serviceable
- **Shipping Rate Calculator** — Weight-based rate calculation per carrier
- **Packing Material Suggestions** — Fragile item → suggest bubble wrap; textiles → poly bag
- **COD Verification** — OTP-based COD verification at delivery
- **Returns Pickup** — Schedule reverse pickup from buyer's address
- **Tracking Webhooks** — Real-time status updates from courier partners

### 5.6 Payment Engine

- **Payment Gateway** — Razorpay (primary), Stripe (international)
- **Escrow Model** — Funds held until delivery confirmed → auto-release T+2
- **Split Payouts** — Platform commission auto-deducted; remainder to seller wallet
- **Refund Engine** — Auto-refund to original payment method on cancellation
- **COD Reconciliation** — Cash reconciled after courier partner settlement
- **International Payments** — USD/GBP/AED via Stripe
- **Payment Links** — Shareable payment link for off-platform orders
- **UPI AutoPay** — Subscription model for curated monthly hamper boxes

### 5.7 Review & Trust System

- **Verified Purchase Reviews** — Reviews only from confirmed buyers
- **Photo Reviews** — Buyers upload product photos with reviews
- **AI Review Summary** — Gemini generates "Buyer Highlights" from aggregate reviews
- **Fake Review Detection** — ML model flags suspicious review patterns
- **Review Response** — Sellers can publicly respond to reviews
- **Upvote Reviews** — Buyers can mark helpful reviews
- **Review Incentive** — Earn 10 reward points for submitting a photo review

### 5.8 Notification System

- **Push Notifications** — FCM (Firebase Cloud Messaging) for Android
- **WhatsApp Notifications** — Twilio/Meta Cloud API for order/pricing alerts
- **SMS Fallback** — Twilio SMS for users without internet
- **In-app Notification Centre** — All notifications in one inbox
- **Email Notifications** — B2B buyers and admin communications
- **Notification Preferences** — Granular control per notification type

---

## 6. Cherry Features (AI-Powered Differentiators)

### 🍒 Cherry 1: Gemini AI Image Studio

**Problem:** Artisans photograph products on floors in cluttered workshops with bad lighting.

**Solution:** Gemini Vision API transforms any photo into a marketplace-ready image.

**Flow:**
1. Raw JPEG/PNG from camera/gallery uploaded
2. **Gemini Vision** analyzes image quality, suggests retake if needed
3. Real-ESRGAN upscales WhatsApp-compressed images 4x
4. rembg (U2-Net) isolates product from background
5. Pillow + OpenCV: auto white-balance, contrast normalization, sharpening
6. **3 variants generated:**
   - White/Amazon-style studio background
   - Lifestyle mockup (wooden shelf for pottery, fabric for textiles)
   - Festive seasonal background (auto-selected by calendar proximity)
7. **Gemini Vision QC Gate** — Scores image quality; rejects blurry/dark/occluded photos
8. Output: 3 professional JPEGs on Cloudinary CDN

**API Used:** Gemini 1.5 Flash (image analysis), Real-ESRGAN (Replicate API)

---

### 🍒 Cherry 2: Multilingual Voice Cataloger (Bhashini)

**Problem:** Artisans can't write product descriptions in English.

**Solution:** Speak in your language → structured listing in seconds.

**Flow:**
1. Artisan speaks product description in regional language (voice note)
2. Bhashini ULCA IndicConformer ASR → text in regional language
3. Bhashini NMT → translated to English
4. **Gemini Pro** extracts structured JSON: {craft_type, material, color[], size, technique, region, gi_candidate, title_en, title_hi, description_en, description_hi, tags[]}
5. Gemini generates 3 description variants: marketplace title (60 chars) / product card (150 chars) / SEO description (400 chars)
6. Bhashini TTS converts confirmation back to artisan's language

**Languages:** 22 scheduled Indian languages (Hindi, Bengali, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Odia, Punjabi + 12 more)

---

### 🍒 Cherry 3: GI Tag Auto-Detector ("Asli Pehchaan")

**Problem:** <5% of eligible artisans know about their GI tags, missing 20-40% price premium.

**Solution:** Auto-detect GI eligibility from product craft type + artisan region.

**Data Source:** GI Registry database from CGPDTM (400+ registered GI tags)

**Flow:**
1. Craft type extracted from voice cataloger
2. Fuzzy string match (RapidFuzz) vs GI craft types in database
3. Region match: artisan's district/state vs GI geographic coverage
4. If match score >85%: **Gemini Pro** verifies with 1-shot classification prompt
5. Gold GI Certified badge added to storefront
6. Pricing engine applies GI premium (+20-40%)
7. Bot message in artisan's language: "Aapka {craft} {state} ka GI tag ke liye eligible hai!"

---

### 🍒 Cherry 4: Kahaani — Cultural Story Generator

**Problem:** Premium buyers purchase a story, a heritage, a connection — not just a product.

**Solution:** AI generates rich cultural narrative personalised to artisan + product.

**Flow:**
1. Product listing created → story generation triggered
2. Vector search (pgvector) on 500+ Indian craft knowledge base
3. Top 3 relevant heritage chunks retrieved
4. **Gemini Pro RAG prompt:** "Given this craft knowledge and this artisan's details, write a 100-word buyer-facing story in English and Hindi"
5. Output: English story + Hindi story + 3 social media captions
6. Displayed as "The Story" section on storefront

**Sample Output (Warli Art, Palghar, Maharashtra):**
> "This Warli painting emerges from the hands of Savita Dhodi, a third-generation tribal artist from Palghar district. For over 2,500 years, Warli artists have used rice paste on mud walls to document the rhythms of harvest and community. When you bring this painting home, you preserve a voice."

---

### 🍒 Cherry 5: Shilpi Score — Seller Trust Rating

**Problem:** B2B buyers placing bulk orders need trust signals before committing.

**Solution:** A composite trust score (0-100) visible on every artisan's storefront.

| Score Component | Weight | Measurement |
|---|---|---|
| Listing Quality | 25% | Gemini Vision image quality + description completeness |
| GI Tag Status | 15% | Verified GI tag on at least 1 listing |
| Order Fulfilment | 20% | % of orders fulfilled on time |
| Response Time | 10% | Avg time to reply to buyer inquiries |
| Buyer Ratings | 20% | Average star rating from completed orders |
| Profile Completeness | 10% | Photo, bio, village, craft type, experience filled |

---

### 🍒 Cherry 6: Mandi Intelligence — Weekly Price Pulse

**Problem:** Artisans price 30-40% below market because they have no data.

**Solution:** Weekly WhatsApp report with live market prices.

**Flow:**
1. Every Monday 8am IST — Celery cron job triggers
2. SerpAPI re-scrapes prices for all active listings across Amazon/Flipkart/Meesho/GeM
3. Compare artisan's current price vs market median
4. If >15% below: flag for price-raise suggestion
5. WhatsApp message + Bhashini TTS voice note in artisan's language
6. "Price update karna chahte hain? Reply 1 for YES" → one-tap auto-update

---

### 🍒 Cherry 7: Udyog Mitra — B2B Buyer Matchmaking

**Problem:** Artisans depend on physical fair schedules to meet bulk buyers.

**Solution:** Vector similarity matching between products and registered B2B buyers.

**Flow:**
1. New product listed → Gemini Embedding generated (text-embedding-004)
2. pgvector cosine similarity against all registered buyer embeddings
3. Buyers with similarity >0.78 shown to artisan as "Interested Buyers"
4. Contact revealed only after mutual interest confirmation (prevents spam)
5. Weekly buyer digest of new matching products

---

### 🍒 Cherry 8: GeM One-Tap Submission

**Problem:** GeM portal is complex, English-only, multi-step — <2% of eligible artisans are on GeM.

**Solution:** One tap in the app submits a compliant GeM listing.

**Flow:**
1. Shilpi Score >65 + GI tag or verified craft + GSTIN/Udyam required
2. Artisan taps "Submit to GeM" on any active listing
3. ShilpSetu auto-fills GeM mandatory fields from product DB
4. HSN code auto-assigned (Chapter 46/57/58/63 for handicrafts)
5. Gemini validates completeness before submission
6. GeM API submission → bot notifies artisan when approved
7. Parallel: ONDC SDK submission → visible on all ONDC buyer apps

---

### 🍒 Cherry 9: Sahaayak — AI Negotiation Coach

**Problem:** Artisans cave immediately when buyers haggle, losing significant margin.

**Solution:** AI coach embedded in WhatsApp bot — forward a buyer's message, get a smart counter.

**Example:**
- Buyer: "Bhai ₹400 mein 5 piece doge?"
- Artisan forwards to ShilpSetu bot
- **Gemini Pro** analyzes: buyer's offer, artisan's price, market range, GI status, bulk signals
- Bot responds: "Kehna: Sir, ek piece ka rate ₹480 hai. 5 piece ke liye ₹2,200 dunga — aur packaging free. GI certified maal hai. Amazon pe yahi ₹550 mein bik raha hai."

---

### 🍒 Cherry 10: Vernacular Video Reel Generator

**Problem:** Word-of-mouth is the primary artisan marketing channel — but it's not digital.

**Solution:** Auto-generate 15-second product reel for Instagram/WhatsApp Status.

**Flow:**
1. 3 AI-enhanced product images + title + price + artisan name
2. 5 pre-designed templates per craft category (textiles, pottery, painting, jewellery, woodwork)
3. Hindi product name overlay (Noto Sans Devanagari font) + price + "Handmade in [Region]"
4. Regional folk music auto-selected (Rajasthani folk, Bengali baul, Tamil carnatic)
5. FFmpeg on Modal.com: slideshow + ken-burns + text overlay + music
6. Output: 1080x1920 MP4 (Instagram Reels format)
7. 15 seconds: 4s per image + 3s end card with QR code to storefront

---

### 🍒 Cherry 11: Mela Mode — Offline-First Sync

**Problem:** Physical fairs have poor connectivity. Artisans can't list products on-site.

**Solution:** Photograph and queue all products offline; auto-sync when connectivity restored.

**Flow:**
1. ConnectivityPlus plugin detects weak/no network
2. Mela Mode banner auto-appears
3. Artisan photographs and queues products locally (Flutter Drift / SQLite)
4. Local queue: status PENDING → UPLOADING → PROCESSING → DONE
5. WorkManager background sync when connectivity restored
6. Smallest files uploaded first for weak connections
7. Progress shown: "Uploading 4 of 7 products... 3 queued"

---

### 🍒 Cherry 12: Gemini Vision Product Authenticity Check

**Problem:** Buyers cannot verify if a product is genuinely handmade vs machine-made.

**Solution:** Gemini Vision analyzes product photos for handmade authenticity markers.

**Flow:**
1. Artisan uploads product photo
2. **Gemini Vision** checks for: natural imperfections, brush stroke patterns, weave irregularities, hand-finishing marks characteristic of the claimed craft type
3. "Authenticity Confidence Score" assigned (0-100)
4. If >80: "Handmade Verified" badge added to listing
5. Suspicious listings (machine-made patterns) flagged for admin review

**API:** Gemini 1.5 Pro Vision

---

### 🍒 Cherry 13: Gemini-powered Government Scheme Advisor

**Problem:** Artisans are unaware of applicable government welfare schemes.

**Solution:** AI matches artisan profile to applicable schemes and guides application.

**Flow:**
1. Artisan profile complete (craft type, state, income, registration)
2. **Gemini Pro** matches profile against database of 50+ government schemes (PM Vishwakarma, SFURTI, MAHC, National Handicraft Development Programme)
3. In artisan's language: "Aapke liye 3 schemes available hain..."
4. Step-by-step application guide with required documents list
5. Reminders for scheme application deadlines

---

### 🍒 Cherry 14: Gemini Smart Caption & Hashtag Generator

**Problem:** Artisans don't know how to write social media captions to grow their audience.

**Solution:** Gemini generates platform-optimised captions and hashtags for each product.

**Flow:**
1. Product listing data → **Gemini Pro** → 3 caption variants per platform:
   - Instagram: Aesthetic, storytelling, 30 hashtags
   - Facebook: Community-focused, shareable
   - WhatsApp Status: Short punchy message + price + storefront link
2. Artisan selects variant and posts from in-app share sheet
3. Caption language: English + Hindi toggle

---

### 🍒 Cherry 15: AI Customer Support Chatbot (Sahayak Bot)

**Problem:** Artisans can't communicate with buyers in English.

**Solution:** In-app AI chat that helps seller communicate with buyers.

**Flow:**
1. Buyer sends message in English to artisan
2. **Gemini Pro** translates to artisan's regional language
3. Artisan types/speaks reply in regional language
4. **Gemini Pro** translates to English and formats professionally
5. Bhashini TTS can read buyer's message aloud to artisan
6. Suggested replies based on common questions (delivery time, customisation, wholesale)

---

## 7. Gemini API Integration Map

| Feature | Gemini Model | Input | Output |
|---|---|---|---|
| AI Image Studio | Gemini 1.5 Flash | Product photo | Quality score, enhancement suggestions |
| Product Authenticity Check | Gemini 1.5 Pro Vision | Product photo | Handmade verification score |
| Voice Cataloger NLP | Gemini 1.5 Pro | Translated text from Bhashini | Structured JSON attributes |
| Description Generation | Gemini 1.5 Pro | Structured attributes | 3 description variants |
| Kahaani Story Generator | Gemini 1.5 Pro | Craft heritage chunks + artisan data | Cultural narrative |
| Pricing Explanation | Gemini 1.5 Flash | Market price data | Hindi/regional language explanation |
| Sahaayak Negotiation Coach | Gemini 1.5 Pro | Buyer message + product context | Counter-offer script |
| GI Tag Verification | Gemini 1.5 Flash | Craft + region match | Verification classification |
| Review Summariser | Gemini 1.5 Flash | All reviews text | "Buyer Highlights" summary |
| Social Caption Generator | Gemini 1.5 Pro | Product listing | Instagram/Facebook/WhatsApp captions |
| Government Scheme Advisor | Gemini 1.5 Pro | Artisan profile | Eligible schemes + application guide |
| Buyer-Seller Translation | Gemini 1.5 Flash | Message text | Translated message |
| Semantic Search Embeddings | text-embedding-004 | Query / Product text | 768-dim embedding vector |
| Photo Caption for Video Reel | Gemini 1.5 Flash | Product info | Punchy video text overlay |
| Fake Review Detection | Gemini 1.5 Flash | Review text + metadata | Authenticity classification |

---

## 8. Full Tech Stack (Firebase Edition)

| Layer | Technology | Purpose | Cost |
|---|---|---|---|
| Embeddings | **Gemini text-embedding-004** | Semantic search + matchmaking | Per token |
| Image AI | rembg + Real-ESRGAN | Background removal + upscaling | Free (Replicate) |
| Voice STT | Bhashini ULCA | Regional language speech-to-text | Free (govt API) |
| Translation | Bhashini NMT | Regional language → English/Hindi | Free (govt API) |
| TTS | Bhashini TTS | Text to regional language voice | Free (govt API) |
| Price Scraping | SerpAPI | Amazon, Flipkart, GeM price data | Free (100/mo) |
| Logistics | Delhivery / Shiprocket | Order fulfilment + tracking | Per shipment |
| Payment | Razorpay | UPI, cards, EMI, wallets | 2% per transaction |
| Intl. Payment | Stripe | USD/GBP/AED payments | 2.9% + 30¢ |
| Video | FFmpeg (Modal.com) | Product reel generation | Free tier |
| GeM | GeM Seller API (sandbox) | Government marketplace submission | Free |
| ONDC | ONDC Seller App SDK | Open network distribution | Free |
| Deployment | Railway.app / Render.com | Backend hosting | Free tier |
| CI/CD | GitHub Actions | Automated test + deploy | Free |
| Monitoring | Sentry + Logfire | Error tracking + observability | Free tier |
| Maps | Google Maps SDK | Address picker, delivery map | Pay-per-use |
| FCM | Firebase Cloud Messaging | Push notifications (Android) | Free |

---

## 9. Database Schema (Expanded)

### Core Tables

```sql
-- Users (Artisan Sellers)
users (
  user_id UUID PRIMARY KEY,
  phone_number VARCHAR(15) UNIQUE NOT NULL,
  name VARCHAR(100),
  language_pref VARCHAR(10) DEFAULT 'hi',
  village VARCHAR(100),
  district VARCHAR(100),
  state VARCHAR(50),
  craft_types TEXT[],
  aadhaar_verified BOOLEAN DEFAULT FALSE,
  udyam_reg_no VARCHAR(50),
  gem_seller_id VARCHAR(50),
  gstin VARCHAR(15),
  shilpi_score INTEGER DEFAULT 0,
  profile_photo_url VARCHAR(500),
  storefront_slug VARCHAR(100) UNIQUE,
  bio TEXT,
  years_experience INTEGER,
  wallet_balance DECIMAL(12,2) DEFAULT 0,
  upi_id VARCHAR(100),
  bank_account JSON,
  total_earnings DECIMAL(12,2) DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
)

-- Products
products (
  product_id UUID PRIMARY KEY,
  user_id UUID FK → users,
  title_en VARCHAR(200),
  title_hi VARCHAR(200),
  title_regional TEXT,
  description_en TEXT,
  description_hi TEXT,
  craft_type VARCHAR(100),
  material VARCHAR(100),
  color TEXT[],
  size_cm VARCHAR(50),
  technique VARCHAR(100),
  region VARCHAR(100),
  gi_tag VARCHAR(100),
  gi_verified BOOLEAN DEFAULT FALSE,
  authenticity_score INTEGER,
  price_suggested DECIMAL(10,2),
  price_listed DECIMAL(10,2),
  price_min DECIMAL(10,2),  -- bulk pricing floor
  moq INTEGER DEFAULT 1,
  image_urls TEXT[],
  video_reel_url VARCHAR(500),
  story_en TEXT,
  story_hi TEXT,
  social_captions JSON,
  hsn_code VARCHAR(10),
  stock_quantity INTEGER,
  status ENUM('draft','active','paused','sold','gem_submitted','ondc_listed'),
  gem_listing_id VARCHAR(100),
  ondc_product_id VARCHAR(100),
  embedding vector(768),
  view_count INTEGER DEFAULT 0,
  wishlist_count INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
)

-- Buyers
buyers (
  buyer_id UUID PRIMARY KEY,
  phone_number VARCHAR(15) UNIQUE NOT NULL,
  email VARCHAR(200),
  name VARCHAR(100),
  buyer_type ENUM('consumer','b2b'),
  org_name VARCHAR(200),
  gstin VARCHAR(15),
  craft_interests TEXT[],
  min_order_qty INTEGER,
  max_budget_per_unit DECIMAL(10,2),
  preferred_regions TEXT[],
  reward_points INTEGER DEFAULT 0,
  embedding vector(768),
  created_at TIMESTAMP DEFAULT NOW()
)

-- Orders
orders (
  order_id UUID PRIMARY KEY,
  product_id UUID FK → products,
  buyer_id UUID FK → buyers,
  seller_id UUID FK → users,
  quantity INTEGER,
  unit_price DECIMAL(10,2),
  total_amount DECIMAL(12,2),
  platform_fee DECIMAL(10,2),
  seller_payout DECIMAL(10,2),
  payment_method VARCHAR(50),
  payment_status ENUM('pending','paid','failed','refunded'),
  razorpay_order_id VARCHAR(100),
  delivery_address JSON,
  shipping_carrier VARCHAR(50),
  tracking_id VARCHAR(100),
  status ENUM('pending','confirmed','packed','shipped','delivered','cancelled','returned'),
  buyer_rating INTEGER,
  buyer_review TEXT,
  review_photo_urls TEXT[],
  gift_message TEXT,
  is_b2b BOOLEAN DEFAULT FALSE,
  packing_slip_url VARCHAR(500),
  created_at TIMESTAMP DEFAULT NOW()
)

-- GI Registry
gi_tags (
  gi_id UUID PRIMARY KEY,
  gi_name VARCHAR(200),
  registration_no VARCHAR(50) UNIQUE,
  craft_types TEXT[],
  state VARCHAR(50),
  districts TEXT[],
  description TEXT,
  registered_year INTEGER,
  price_premium_pct DECIMAL(5,2)
)

-- Craft Knowledge Base (for Kahaani)
craft_heritage (
  heritage_id UUID PRIMARY KEY,
  craft_type VARCHAR(100),
  history_text TEXT,
  technique_desc TEXT,
  cultural_significance TEXT,
  key_regions TEXT[],
  artisan_communities TEXT[],
  source VARCHAR(200),
  embedding vector(768)
)
```

---

## 10. Impact Goals & Success Metrics

| Impact Goal | Current State | Target (12 months) | Metric |
|---|---|---|---|
| Digital presence | 0 artisans per fair have online listings after event | 80% of onboarded artisans have active listings | % with active listing |
| Listing time | 60-90 min to create a listing manually | Under 5 minutes per product | Avg time product → live |
| Price realisation | Artisans price 30-40% below market | Within 10% of market median | Price vs market benchmark |
| GI tag awareness | <5% of eligible artisans know their GI tags | 50% of eligible artisans GI-flagged automatically | % auto-identified |
| GeM penetration | <2% of eligible artisans on GeM | 20% of active users with ≥1 GeM listing | GeM submissions |
| Annual income | Avg ₹85,000/year (NCAER data) | 20% income increase via digital channel | Income delta (survey) |
| Digital literacy | Cannot navigate any e-commerce platform | Can manage WhatsApp bot independently after 1 week | Self-sufficient usage rate |
| B2B connections | 0 digital B2B buyer connections per artisan | Avg 2 qualified B2B leads per active artisan/month | B2B matches made |
| Buyer retention | N/A (new platform) | 40% of buyers place 2nd order within 60 days | Repeat purchase rate |
| Review rate | N/A | 30% of delivered orders have a review | Review submission rate |

---

## 11. Implementation Roadmap

### SIH Hackathon (60-hour Sprint)

| Phase | Hours | Features | Owner |
|---|---|---|---|
| Phase 0: Setup | 0-4h | Repo, FastAPI skeleton, PostgreSQL schema, Flutter project, Gemini API key, Bhashini API key | Full team |
| Phase 1: Core MVP | 4-16h | WhatsApp onboarding FSM, Photo receive + rembg, Bhashini STT → Gemini description, Basic listing | Backend + Bot |
| Phase 2: Pricing | 16-24h | SerpAPI price scraping, Price suggestion logic, Pricing reply to artisan, Redis cache | Backend |
| Phase 3: Buyer App | 16-28h | Flutter buyer UI: home, product detail, cart, checkout, order tracking | Flutter dev |
| Phase 4: Seller App | 16-28h | Flutter seller UI: dashboard, listings, orders, analytics, profile | Flutter dev |
| Phase 5: AI Cherries | 28-40h | GI detector, Kahaani story generator, Shilpi Score, B2B matchmaking, Sahaayak negotiation | Backend + AI |
| Phase 6: GeM + Video | 40-50h | GeM sandbox submission, ONDC SDK, FFmpeg video reel, Mandi Intelligence cron | Full team |
| Phase 7: Admin Panel | 40-50h | React admin web panel: seller mgmt, order mgmt, analytics, AI config | Frontend dev |
| Phase 8: Polish | 50-60h | UI/UX, error handling, demo data seeding, pitch deck, video demo | Full team |

### Post-SIH Roadmap (3-12 months)

| Milestone | Timeline | Features |
|---|---|---|
| Beta Launch | Month 1-2 | 100 artisan pilots across 5 craft clusters |
| Payment Integration | Month 2-3 | Razorpay live, escrow, payouts |
| Logistics Integration | Month 3-4 | Delhivery, Shiprocket live |
| ONDC Live | Month 4-5 | ONDC production network integration |
| GeM Production | Month 5-6 | GeM live API (after sandbox validation) |
| B2B Portal | Month 6-8 | Buyer self-registration, enterprise features |
| Scale | Month 9-12 | 10,000 artisans, ministry partnership |

---

### Team Roles

| Role | Responsibilities |
|---|---|
| Backend Lead | FastAPI, PostgreSQL schema, Celery tasks, GeM/ONDC integration, pricing service |
| AI/ML Engineer | Gemini API prompts, Bhashini integration, rembg pipeline, pgvector matchmaking, GI detector |
| Flutter Developer | Full seller + buyer app UI/UX, Drift offline DB, WebSocket, WorkManager |
| Bot Developer | Twilio/Meta webhook, FSM implementation, WhatsApp conversation flows, Bhashini TTS |
| Full-Stack / DevOps | Railway deployment, CI/CD, SerpAPI scraping, FFmpeg video service, Redis config |
| UI/UX + Presenter | Figma designs, demo script, pitch deck, impact data, video recording |

---

> **"ShilpSetu meets artisans where they already are — WhatsApp — and grows with them into a full digital business. From a voice note in Bhojpuri to a live GeM listing in under 5 minutes. No English required. No technical skills required. This is not a feature. This is access."**

---

*Document Version: 1.0 | SIH 2026 | ShilpSetu — Virtual Business Manager for Indian Artisans*
*Prepared: August 2026 | Classification: Internal Technical Reference*

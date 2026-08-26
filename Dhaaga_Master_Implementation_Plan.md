# Dhaaga — Final Master Implementation Document
### Smart India Hackathon 2026 | Complete Build Plan
> **"Connecting Hands to Markets"** — *Dhaaga, the thread that binds artisans to the world.*

---

## Brand Identity

| Token | Value | Usage |
|---|---|---|
| **Primary** | `#D4521A` — Deep Saffron Orange | Buttons, CTAs, active states |
| **Secondary** | `#F5A623` — Warm Gold | Highlights, badges, stars |
| **Background** | `#FFF8EE` — Cream White | App background, cards |
| **Accent** | `#2BB5A0` — Teal | Icons, tags, chat bubbles |
| **Success** | `#4CAF50` — Growth Green | Order success, GI badge |
| **Text Dark** | `#3E1F00` — Deep Brown | Headings, body text |
| **Card BG** | `#FDEBD0` — Light Peach | Product cards, list items |
| **Dividers** | `#C8922A` — Muted Gold | Borders, separators |
| **Font** | `Poppins` (Latin) + `Noto Sans Devanagari` | Bilingual support |
| **Motifs** | Rangoli borders, thread/paisley patterns | Decorative elements |

---

## Table of Contents

1. [The Unified App Concept](#1-the-unified-app-concept)
2. [Complete Screen Map](#2-complete-screen-map)
3. [Dynamic Home Page (Seller + Buyer)](#3-dynamic-home-page)
4. [Privacy Model — Hidden Until Deal Done](#4-privacy-model)
5. [Bulk Buy + In-App Chat](#5-bulk-buy--in-app-chat)
6. [Mock Payment + Order Tracking](#6-mock-payment--order-tracking)
7. [Admin Web Panel](#7-admin-web-panel)
8. [Onboarding & Language Selection](#8-onboarding--language-selection)
9. [UI Design System](#9-ui-design-system)
10. [Phase-wise Development Plan](#10-phase-wise-development-plan)
11. [Firebase Free Tier Confirmation](#11-firebase-free-tier-confirmation)

---

## 1. The Unified App Concept

There is **ONE Flutter Android app** — `dhaaga.apk`.
When you log in, the app detects your role (`seller` or `buyer`) from Firestore and **dynamically switches** what you see.

```
SAME APP — TWO MODES

┌─────────────────────────────────────────────────────────┐
│                   HOME PAGE (identical)                 │
│  Search bar  │  [Categories]  │  [Featured]            │
│  ─── Product Grid (same for both) ───────────────────   │
│  [Product Card] [Product Card] [Product Card]           │
│  [Product Card] [Product Card] [Product Card]           │
└──────────────────────────┬──────────────────────────────┘
                           │
               ┌───────────┴───────────┐
               │                       │
        SELLER MODE               BUYER MODE
               │                       │
    Bottom nav tabs:          Bottom nav tabs:
    Home                      Home
    My Listings               Wishlist
    Add Product               Cart
    Dashboard                 My Orders
    Profile                   Profile
```

### How role is detected
```dart
final userDoc = await FirebaseFirestore.instance
    .collection('users')
    .doc(FirebaseAuth.instance.currentUser!.uid)
    .get();
final role = userDoc.data()?['role']; // "seller" | "buyer"
// Router switches bottom nav + FAB accordingly
```

---

## 2. Complete Screen Map

### 2.1 Onboarding Flow (One-time)
```
Splash Screen
    └── Language Selection (22 languages — ONE TIME ONLY)
            └── Role Selection: "I'm an Artisan" | "I'm a Buyer"
                    └── Phone Number Entry
                            └── OTP Verification (Firebase Auth)
                                    └── Profile Setup
                                            └── HOME
```

### 2.2 Seller Screens
```
HOME (shared product grid)
├── Search Results
├── Product Detail Page
│   └── Bulk Enquiry → Chat Screen
│
├── MY LISTINGS tab
│   ├── Active Listings Grid
│   ├── Draft Listings
│   ├── Sold Items
│   └── ONDC / GeM Submitted
│
├── ADD PRODUCT tab
│   ├── Camera / Gallery
│   ├── Image Studio (as-is OR AI enhance)
│   ├── Voice / Text Description
│   ├── AI Attribute Extraction (loading)
│   ├── Review & Edit Form
│   ├── Pricing (market comparison)
│   └── Publish → (Storefront + ONDC)
│
├── DASHBOARD tab
│   ├── Earnings Card (today / week / month)
│   ├── Orders Inbox
│   │   ├── Order Detail
│   │   │   ├── Accept / Reject
│   │   │   ├── Mark as Packed
│   │   │   └── Mark as Shipped (tracking ID)
│   ├── Analytics (views, conversions)
│   ├── Pricing Alerts (15% below market)
│   ├── Shilpi Score Card
│   └── B2B Match Notifications
│
├── PROFILE tab
│   ├── Artisan Storefront Preview
│   ├── GI Tag Status
│   ├── Wallet Balance + Payout History
│   ├── GeM Submission
│   ├── Govt Scheme Advisor
│   └── Settings (language, notifications)
│
└── CHAT tab
    └── All Conversations (buyer enquiries)
```

### 2.3 Buyer Screens
```
HOME (shared product grid)
├── Search (text / voice / image)
├── Category Browse
├── GI Certified filter
├── Product Detail Page
│   ├── Image Gallery (swipeable, zoomable)
│   ├── The Story (Kahaani AI-generated)
│   ├── Artisan Card (Shilpi Score — no contact)
│   ├── Reviews + Photos
│   ├── Similar Products
│   ├── [ADD TO CART]
│   └── [BULK ENQUIRY → Chat]
│
├── WISHLIST tab
│
├── CART tab
│   ├── Cart Items
│   ├── Address Entry
│   ├── Payment Selection (mock)
│   └── Order Placed Success
│
├── MY ORDERS tab
│   ├── Active Orders
│   │   └── Track Order (demo timeline, real-time)
│   └── Past Orders (rate & review)
│
├── PROFILE tab
│   ├── Edit Profile
│   ├── Addresses
│   ├── Reward Points
│   ├── Language Settings
│   └── Followed Artisans
│
└── CHAT tab
    └── All Conversations (with sellers, post-enquiry)
```

---

## 3. Dynamic Home Page

The Home Page is **identical in layout** for both roles. The product grid, search, and categories work the same. What changes is only the action buttons on cards.

### Product Card — Seller Mode
```
┌─────────────────────┐
│  [Product Image]    │
│  ════════════ [Edit]│  <- Edit button (seller only)
│  Warli Painting     │
│  Rs. 850            │
│  4.8 stars          │
│  [Edit] [Pause]     │  <- Seller actions
└─────────────────────┘
```

### Product Card — Buyer Mode
```
┌─────────────────────┐
│  [Product Image]    │
│  ══════════ [Heart] │  <- Wishlist button
│  Warli Painting     │
│  Rs. 850            │
│  4.8 stars          │
│  [Add to Cart]      │  <- Buyer action
└─────────────────────┘
```

### Home Page Full Layout
```
App Bar: [Dhaaga logo]  [Bell]  [Globe]  [Profile]
         Saffron gradient background

Search: [Search products, artisans, crafts... (voice icon)]

Categories: Paintings | Textiles | Pottery | Jewellery | Woodwork |
            Leather   | Metal    | Bamboo  | Food      | All >

SELLER ONLY strip: "Today: Rs.2,400 earned  |  3 new orders  [->]"

TRENDING THIS WEEK            [See all ->]
[Card] [Card] [Card] [Card]  <-- horizontal scroll

GI CERTIFIED PICKS            [See all ->]
[Card] [Card] [Card]

FEATURED ARTISAN
Savita Dhodi — Warli Art, Palghar
[View Store ->]

ALL PRODUCTS         [Filter] [Sort]
[Card] [Card]
[Card] [Card]
... infinite scroll (Firestore pagination)
```

---

## 4. Privacy Model — Hidden Until Deal Done

> **No real phone numbers, emails, or addresses are ever shown** until an order is placed and delivered.

| Information | Before Enquiry | In Chat | Order Placed | Order Delivered |
|---|---|---|---|---|
| Artisan name | Shown | Shown | Shown | Shown |
| Village/State | Shown | Shown | Shown | Shown |
| Shilpi Score | Shown | Shown | Shown | Shown |
| Phone number | HIDDEN | HIDDEN | HIDDEN | Shown |
| Email | HIDDEN | HIDDEN | HIDDEN | Shown |
| Buyer delivery address | N/A | HIDDEN | Seller sees | Shown |
| Buyer full name | HIDDEN | First name only | Shown | Shown |

### Firestore Security Rules for Privacy
```javascript
match /buyers/{buyerId} {
  allow read: if request.auth.uid == buyerId
    || isAdmin()
    || hasDeliveredOrder(request.auth.uid, buyerId);
}

function hasDeliveredOrder(sellerId, buyerId) {
  return exists(/databases/$(database)/documents/orders/
    some_order_between_seller_and_buyer_with_status_delivered);
}
```

---

## 5. Bulk Buy + In-App Chat

### 5.1 The Flow
```
PRODUCT DETAIL PAGE
        │
  [Bulk Enquiry]  <- visible on every product
        │
        ▼
BULK ENQUIRY FORM
  Quantity: [____] pieces
  Message:  [Tell the artisan...]
  [Start Chat with Artisan]
        │
        ▼
  CHAT SCREEN (WhatsApp-style)
```

### 5.2 Chat Screen Layout
```
AppBar: [<-] Savita Dhodi    [Online]    [Order Button]
        Warli Painting | Rs.850/pc

Messages area:
  [Buyer bubble]  2:30 PM
  "Hi! I need 50 pieces for corporate gifting."

                    [Seller bubble]  2:35 PM
  "Namaste! For 50 pcs: Rs.720/pc + free packaging"

  [DEAL CARD — sent by seller]
  ┌─────────────────────────────┐
  │  50 pieces x Rs.720 = Rs.36,000  │
  │  Free packaging                  │
  │  Delivery: 15 days               │
  │  [Accept & Pay]  [Counter Offer] │
  └─────────────────────────────┘

  [AI Suggest button for seller: "Sahaayak"]

Input bar: [Mic] [Type message...] [Attach] [Send]
```

### 5.3 Firestore Chat Schema
```
/chats/{chatId}     <- chatId = sellerId_buyerId_productId
  sellerId, buyerId, productId
  productSnapshot: {title, image, price}
  status: "open" | "deal_in_progress" | "order_placed"
  lastMessage, lastMessageAt
  unreadCount: {seller: 0, buyer: 2}

  /messages/{msgId}
    senderId, text, imageUrl, audioUrl
    type: "text" | "image" | "deal_card" | "system"
    dealCard: {qty, pricePerUnit, total, delivery}
    read: boolean, createdAt
```

### 5.4 Chat -> Order
```
Seller sends Deal Card
  -> Buyer sees [Accept & Pay] button
  -> Opens Payment Screen (pre-filled with deal details)
  -> After mock payment: order created in Firestore
  -> Chat gets "ORDER PLACED" system message
  -> Chat status = "order_placed"
  -> Contact details unlocked only after "delivered"
```

---

## 6. Mock Payment + Order Tracking

### 6.1 Payment Methods (all simulated)
```
UPI:         Enter any UPI ID (e.g. test@upi)
             -> 2s "Waiting for approval" animation
             -> Success

Card:        Enter any 16-digit number + expiry + CVV
             -> 1.5s processing animation
             -> Success

Net Banking: Select any bank
             -> Instant success

COD:         No payment screen
             -> Order placed immediately

Wallet:      Balance shown as Rs.0
             -> Success in demo
```

### 6.2 mockPlaceOrder Cloud Function (no Razorpay)
```javascript
// Calculates amounts, writes to Firestore, sends FCM to seller
// orderId = "SIH" + timestamp + random
// No payment gateway involved AT ALL
// isMockPayment: true flag stored on order document
// To add real Razorpay later: replace only this function
```

### 6.3 Order Tracking Screen (Buyer, real-time)
```
Order #SIH2026ABC

[Confirmed]     Mon 25 Aug 10:30 AM  <- green checkmark
  Your order was accepted

[Being Prepared] Mon 25 Aug 02:00 PM <- orange dot
  Artisan is packing your item

[Shipped]        --- not yet ---      <- grey dot
  Expected: Tue 26 Aug

[Out for Delivery] --- not yet ---   <- grey dot

[Delivered]      --- not yet ---     <- grey dot

Tracking ID: DEMO8294756391
Carrier: Delhivery (Demo)

[Chat with Artisan]   [Cancel Order]
```

> **Real-time:** Admin updates status in web panel → Firestore listener in Flutter updates screen instantly.

---

## 7. Admin Web Panel

> React 18 app deployed on Firebase Hosting at `admin.dhaaga.in`  
> Login: email + password with `admin: true` custom Firebase claim

### Navigation
```
ADMIN PANEL
├── Dashboard          (live KPIs via Firestore)
├── Sellers            (list, verify docs, suspend)
├── Buyers             (list, fraud detection)
├── Products           (moderate, feature, remove)
├── Orders             (ALL orders, status management)
│   └── Order Detail   -> [Update Status] [Add Note]
├── Chat Reports       (flagged conversations)
├── GeM Queue          (pending submissions)
├── ONDC Status        (sync health)
├── Campaigns          (push notifications, banners)
├── Config             (commission %, feature flags)
└── Admin Accounts
```

### Order Status Update (Admin)
```
ORDER #SIH2026ABC
Product: Warli Painting x1 | Rs.941
Buyer: Rahul S., Bengaluru
Seller: Savita Dhodi, Palghar

Current Status: Being Prepared

UPDATE STATUS: [Shipped v]
Tracking ID:   [DEMO8294756391]
Carrier:       [Delhivery (Demo)]
Note:          [Handed to courier]

[Save & Notify Buyer]
```
On save: Firestore update → Flutter app updates tracking screen live → FCM push to buyer.

---

## 8. Onboarding & Language Selection

### 8.1 Screen 1 — Splash (2 seconds)
Dhaaga logo animation. Thread unspooling. Auto-advances.

### 8.2 Screen 2 — Language (ONE TIME ONLY)
```
Dhaaga logo at top

"Choose your language"
"अपनी भाषा चुनें"

Grid of language buttons (3 columns):
Hindi     Bengali   Tamil
Telugu    Marathi   Gujarati
Kannada   Malayalam Odia
Punjabi   English   Urdu
Assamese  Kashmiri  Nepali
Sanskrit  Maithili  Dogri
Konkani   Manipuri  Santali   Bhojpuri

[Continue ->]
```
Language saved to: `SharedPreferences` (instant) + Firestore `users.languagePref`.  
**Never shown again** — checked via `prefs.getString('lang')` on launch.

### 8.3 Screen 3 — Role Select
```
Who are you?

[Artisan Card]           [Buyer Card]
I make & sell crafts     I buy handmade crafts
```

### 8.4 Screen 4 — Phone OTP (Firebase Auth)
```
+91 [phone number]
[Send OTP]
[_ _ _ _ _ _]
[Verify & Continue]
```

### 8.5 Screen 5 — Profile Setup
- Seller: Name, Village, State, Craft Types, Profile Photo
- Buyer: Name, City, Craft Interests (multi-select)
→ [Complete -> HOME]

---

## 9. UI Design System

### 9.1 Design Principles
1. **Indian warmth** — Saffron/gold from Dhaaga logo
2. **Clean & scannable** — products front and centre
3. **Bilingual ready** — Noto Sans Devanagari for Hindi/regional text
4. **Micro-animations** — card scale, shimmer loading, success confetti
5. **Rangoli accents** — thin decorative borders on section headers
6. **Accessible** — minimum 4.5:1 contrast on all text

### 9.2 Color Tokens
```dart
class DhaagaColors {
  static const primary    = Color(0xFFD4521A);  // Saffron Orange
  static const secondary  = Color(0xFFF5A623);  // Warm Gold
  static const background = Color(0xFFFFF8EE);  // Cream
  static const accent     = Color(0xFF2BB5A0);  // Teal
  static const success    = Color(0xFF4CAF50);  // Green
  static const textDark   = Color(0xFF3E1F00);  // Deep Brown
  static const cardBg     = Color(0xFFFDEBD0);  // Light Peach
  static const divider    = Color(0xFFC8922A);  // Muted Gold
  static const error      = Color(0xFFD32F2F);

  static const primaryGradient = LinearGradient(
    colors: [Color(0xFFD4521A), Color(0xFFF5A623)],
    begin: Alignment.topLeft, end: Alignment.bottomRight,
  );
}
```

### 9.3 Typography
```dart
// Headings: Poppins Bold/SemiBold — deep brown
// Body: Poppins Regular — deep brown
// Hindi/Regional text: Noto Sans Devanagari
// Prices: Poppins Bold — saffron orange
// Tags/Chips: Poppins Medium — teal
```

### 9.4 Key Animations
- Product card: `ScaleTransition` on tap (0.97 → 1.0, 100ms)
- Loading: Shimmer in saffron/gold gradient
- Order success: Lottie confetti (saffron + gold particles)
- Payment processing: Animated thread/spool (Lottie, matches logo)
- Chat bubbles: `SlideTransition` from right/left
- Tab switch: `IndexedStack` with opacity fade

### 9.5 App Bar
```
Saffron gradient background (#D4521A → #F5A623)
[Dhaaga logo small]  dhaaga  [Bell badge]  [Globe]  [Avatar]
White text + white icons on gradient
```

---

## 10. Phase-wise Development Plan

```
Phase 0: Setup           0-4 hrs    Firebase + Flutter scaffold
Phase 1: Auth & Home     4-14 hrs   Login, language, home screen
Phase 2: Seller Core     14-26 hrs  Add product, AI image, voice cataloger
Phase 3: Buyer Core      26-36 hrs  Browse, product detail, cart, payment
Phase 4: Chat & Orders   36-44 hrs  Real-time chat, bulk buy, order tracking
Phase 5: AI Cherry       44-54 hrs  GI detector, Kahaani, pricing engine
Phase 6: Admin Panel     54-60 hrs  React web panel, order management
```

---

### Phase 0: Setup (0–4 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| Create Firebase project `dhaaga-sih2026` | Firebase Console | DevOps | 15m |
| Enable Auth, Firestore, Storage, Functions, FCM, Hosting, Analytics, Crashlytics | Console | DevOps | 15m |
| Flutter project: `flutter create dhaaga` | Flutter CLI | Flutter Dev | 15m |
| Add all FlutterFire packages to pubspec.yaml | - | Flutter Dev | 20m |
| Run `flutterfire configure` | FlutterFire CLI | Flutter Dev | 15m |
| Init Cloud Functions (Node.js 20) | Firebase CLI | Backend Dev | 30m |
| Init Firebase Hosting (React admin panel) | Firebase CLI | Frontend Dev | 20m |
| GitHub repo + CI/CD (`firebase-action`) | GitHub Actions | DevOps | 30m |
| Seed Firestore: GI tags (400+), Craft Heritage (50) | Node script | Backend | 30m |
| Store all API secrets in Secret Manager | Cloud Console | DevOps | 20m |

**Deliverable:** App opens on device. Firebase connected. Firestore seeded.

---

### Phase 1: Auth & Home (4–14 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| Splash screen + Dhaaga logo animation (Lottie) | Flutter | Flutter Dev | 30m |
| Language selection screen (22 languages grid) | SharedPrefs + Firestore | Flutter Dev | 1h |
| Role selection screen (Seller/Buyer cards) | Firestore `users.role` | Flutter Dev | 30m |
| Phone OTP entry screen | Firebase Auth | Flutter Dev | 45m |
| OTP verify + user doc creation in Firestore | `auth.onCreate` Cloud Function | Backend Dev | 45m |
| Profile setup screen (seller variant) | Firestore `users` | Flutter Dev | 45m |
| Profile setup screen (buyer variant) | Firestore `users` | Flutter Dev | 30m |
| Home screen scaffold + saffron AppBar | Flutter | Flutter Dev | 45m |
| Category chips (horizontal scroll, teal) | Flutter | Flutter Dev | 30m |
| Product grid from Firestore (paginated) | Firestore + StreamBuilder | Flutter Dev | 1.5h |
| Product card component (role-aware buttons) | Flutter | Flutter Dev | 1h |
| Seller-only earnings strip above grid | Firestore aggregation | Flutter Dev | 30m |
| Bottom nav (5 tabs, role-aware icons) | Flutter + role check | Flutter Dev | 45m |

**Deliverable:** Full onboarding flow working. Home page shows real Firestore products.

---

### Phase 2: Seller Core (14–26 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| My Listings tab (active/draft/sold tabs) | Firestore query `sellerId` | Flutter Dev | 1h |
| Add Product screen — camera + gallery | `image_picker` + Firebase Storage | Flutter Dev | 1h |
| Image Studio: "Upload As-Is" vs "AI Enhance" | Flutter UI | Flutter Dev | 45m |
| `enhanceProductImage` Cloud Function | CF + Gemini Vision + Replicate | Backend Dev | 2h |
| Before/After comparison slider | Flutter | Flutter Dev | 45m |
| Voice recording screen (hold-to-record) | `record` package + Firebase Storage | Flutter Dev | 1h |
| `processVoiceInput` Cloud Function | CF + Bhashini STT + NMT + Gemini | Backend Dev | 2h |
| Attribute review + edit form | Flutter form | Flutter Dev | 1h |
| Pricing suggestion (market comparison bar) | `getPriceSuggestion` CF + SerpAPI | Backend + Flutter | 1.5h |
| Publish product → Firestore write | Firestore | Flutter Dev | 30m |
| `onProductCreate` trigger (embeddings + Kahaani + GI) | CF + Gemini | Backend Dev | 1h |
| Seller Dashboard: earnings card + chart | Firestore + fl_chart | Flutter Dev | 1h |
| Orders inbox (seller side, accept/reject) | Firestore `orders` | Flutter Dev | 1h |
| Mark as Packed / Shipped UI | Firestore update | Flutter Dev | 30m |
| Seller profile + storefront preview | Firestore + Storage | Flutter Dev | 1h |

**Deliverable:** Artisan can add product with AI help, view listings, manage orders.

---

### Phase 3: Buyer Core (26–36 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| Text search (Firestore compound query) | Firestore | Flutter Dev | 1h |
| Voice search (Bhashini STT → search) | CF + Bhashini | Backend + Flutter | 1h |
| Filter bottom sheet (category, price, GI) | Firestore compound + Flutter | Flutter Dev | 1.5h |
| Product detail page full layout | Flutter | Flutter Dev | 1.5h |
| Image gallery (swipeable + zoomable) | `photo_view` package | Flutter Dev | 30m |
| Kahaani story section (collapsible card) | Firestore `storyEn` | Flutter Dev | 30m |
| Artisan card (Shilpi Score, no contact) | Firestore `users` partial read | Flutter Dev | 30m |
| Reviews section + add review form | Firestore subcollection | Flutter Dev | 1h |
| Add to Cart (Riverpod state + Firestore) | Riverpod + Firestore | Flutter Dev | 30m |
| Cart screen (multi-item, price totals) | Flutter | Flutter Dev | 1h |
| Address entry + save to Firestore | Firestore `buyers.addresses` | Flutter Dev | 1h |
| Payment screen (mock — UPI/Card/COD) | Flutter UI | Flutter Dev | 1h |
| `mockPlaceOrder` Cloud Function | CF + Firestore + FCM | Backend Dev | 1h |
| Order success screen + Lottie confetti | Flutter + Lottie | Flutter Dev | 30m |
| My Orders tab (buyer) | Firestore query | Flutter Dev | 30m |
| Order tracking screen (timeline, real-time) | Firestore `.snapshots()` listener | Flutter Dev | 1h |
| Wishlist heart button + wishlist screen | Firestore subcollection | Flutter Dev | 30m |

**Deliverable:** Buyer can search, view, buy with mock payment, track order live.

---

### Phase 4: Chat & Orders (36–44 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| Chat list screen (all conversations) | Firestore `chats` query | Flutter Dev | 1h |
| Chat screen — WhatsApp-style bubble UI | Flutter + Firestore real-time | Flutter Dev | 2h |
| Send text message to Firestore | Firestore `messages` write | Flutter Dev | 30m |
| FCM push on new message | `onMessageCreate` CF + FCM | Backend Dev | 1h |
| Bulk Enquiry form → opens/creates chat | Flutter + Firestore | Flutter Dev | 1h |
| Deal Card message type (seller sends offer) | Firestore `type: deal_card` | Flutter + Backend | 1h |
| Accept Deal → Payment Screen (pre-filled) | Flutter navigation | Flutter Dev | 30m |
| "AI Suggest" button for seller (Sahaayak) | `getNegotiationScript` CF + Gemini | Backend Dev | 1h |
| Contact reveal rule (after delivery) | Firestore security rules | Backend Dev | 30m |
| Read receipts (double tick) | Firestore `read` field | Flutter Dev | 30m |

**Deliverable:** Real-time chat, bulk buy negotiation, deal cards, orders from chat.

---

### Phase 5: AI Cherry Features (44–54 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| GI Tag detector display (from `onProductCreate`) | CF + Firestore | Backend Dev | 1h |
| GI badge on product card + detail | Flutter | Flutter Dev | 30m |
| Kahaani story display (generated on create) | Firestore | Flutter Dev | 30m |
| Shilpi Score weekly compute (Cloud Scheduler) | CF + Cloud Scheduler | Backend Dev | 1h |
| Shilpi Score display on artisan card | Flutter | Flutter Dev | 30m |
| B2B matchmaking (Gemini embeddings) | `runB2BMatchmaking` CF | Backend Dev | 1.5h |
| B2B match notification | FCM + notification screen | Flutter + Backend | 30m |
| Mandi Intelligence weekly cron | Cloud Scheduler + CF + SerpAPI | Backend Dev | 1.5h |
| Price alert notification UI | Flutter | Flutter Dev | 30m |
| Govt Scheme Advisor screen | `getGovernmentSchemes` CF + Gemini | Backend + Flutter | 1h |
| Authenticity score display on product | Firestore field | Flutter Dev | 30m |
| Video reel generator (FFmpeg via Modal.com) | `generateVideoReel` CF | Backend Dev | 1.5h |
| Social captions share sheet | `generateSocialCaptions` CF + `share_plus` | Flutter Dev | 30m |

**Deliverable:** All 15 cherry features functional and demonstrable for SIH judges.

---

### Phase 6: Admin Panel (54–60 hrs)

| Task | Service | Owner | Time |
|---|---|---|---|
| React 18 app scaffold in `admin-panel/` | Vite + React | Frontend Dev | 30m |
| Firebase SDK + admin auth setup | Firebase npm | Frontend Dev | 30m |
| Admin login page (email + password) | Firebase Auth | Frontend Dev | 30m |
| Dashboard: live KPIs (Firestore real-time) | Firestore | Frontend Dev | 1h |
| Sellers list + search + profile view | Firestore | Frontend Dev | 1h |
| Orders list + search + filter by status | Firestore | Frontend Dev | 30m |
| Order detail + status update dropdown | Firestore write | Frontend Dev | 1h |
| FCM push to buyer on status update | `updateOrderStatus` CF | Backend Dev | 30m |
| Product moderation queue | Firestore | Frontend Dev | 1h |
| GeM pending queue view | Firestore filter | Frontend Dev | 30m |
| Push notification broadcast | CF + FCM topic | Backend Dev | 30m |
| Deploy to Firebase Hosting | Firebase CLI | DevOps | 15m |

**Deliverable:** Admin logs in, manages all orders, updates shipment status, moderates products.

---

## 11. Firebase Free Tier Confirmation

> All 100% on Firebase Spark (free) plan for SIH demo.

| Service | Free Limit | Our Usage | Cost |
|---|---|---|---|
| Firebase Auth Phone OTP | Unlimited | ~50 users | Rs. 0 |
| Firestore reads | 50,000/day | Demo scale | Rs. 0 |
| Firestore writes | 20,000/day | Demo scale | Rs. 0 |
| Firestore storage | 1 GB | < 100 MB | Rs. 0 |
| Firebase Storage | 5 GB stored | ~500 images | Rs. 0 |
| Firebase Storage transfer | 1 GB/day | Demo scale | Rs. 0 |
| Cloud Functions | 2M calls/month | Demo scale | Rs. 0 |
| FCM push notifications | Unlimited | Always free | Rs. 0 |
| Firebase Hosting | 10 GB | Admin panel | Rs. 0 |
| Firebase Analytics | Unlimited | Always free | Rs. 0 |
| Crashlytics | Unlimited | Always free | Rs. 0 |
| Remote Config | Unlimited | Always free | Rs. 0 |
| App Distribution | Unlimited | Always free | Rs. 0 |

| External API | Free Tier | Cost |
|---|---|---|
| Gemini 1.5 Flash | 15 RPM, 1M TPM free | Rs. 0 |
| Gemini 1.5 Pro | 2 RPM, 32K TPD free | Rs. 0 |
| Gemini Embeddings | 1,500 req/day free | Rs. 0 |
| Bhashini | Free (Government API) | Rs. 0 |
| SerpAPI | 100 free/month | Rs. 0 |
| Replicate (rembg) | 500 free/month | Rs. 0 |
| Modal.com (FFmpeg) | $30 free credit | Rs. 0 |
| Payments | Mock (no gateway) | Rs. 0 |

**TOTAL SIH DEMO COST: Rs. 0**

---

## Day 0 Checklist

```
Before starting to code:

Firebase:
[ ] Create project: dhaaga-sih2026
[ ] Enable: Auth (Phone), Firestore, Storage, Functions,
            FCM, Hosting, Analytics, Crashlytics, Remote Config
[ ] Set Firestore rules (from Firebase Architecture doc)
[ ] Set Storage rules

API Keys (all free, all instant except Bhashini):
[ ] GEMINI_API_KEY     -> aistudio.google.com
[ ] BHASHINI_API_KEY   -> bhashini.gov.in/ulca (24-48h wait — do this FIRST)
[ ] BHASHINI_USER_ID   -> same portal
[ ] SERPAPI_KEY        -> serpapi.com
[ ] REPLICATE_TOKEN    -> replicate.com
[ ] MODAL_API_KEY      -> modal.com

All keys -> Google Cloud Secret Manager
Access via: defineSecret("KEY_NAME") in Cloud Functions
```

---

*Document: FINAL | Dhaaga (ShilpSetu) | SIH 2026*
*Brand: Dhaaga — Connecting Hands to Markets*
*Stack: Flutter Android + Firebase (everything) + Gemini API + Bhashini*
*Demo Cost: Rs. 0*

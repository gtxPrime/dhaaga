# ShilpSetu — Firebase Architecture Document
### Smart India Hackathon 2026 | 100% Firebase Backend
> **"One Firebase project. Three apps. Zero servers to manage."**

---

## Table of Contents

1. [Firebase Service Map](#1-firebase-service-map)
2. [Architecture Diagram](#2-architecture-diagram)
3. [Firebase vs Previous Stack](#3-firebase-vs-previous-stack)
4. [Firestore Data Model](#4-firestore-data-model)
5. [Firebase Cloud Functions Catalog](#5-firebase-cloud-functions-catalog)
6. [Firebase Security Rules](#6-firebase-security-rules)
7. [Firebase Storage Structure](#7-firebase-storage-structure)
8. [Gemini API via Firebase](#8-gemini-api-via-firebase)
9. [External APIs (What Firebase Can't Do)](#9-external-apis-what-firebase-cant-do)
10. [Full Tech Stack (Firebase Edition)](#10-full-tech-stack-firebase-edition)
11. [Flutter Firebase Setup](#11-flutter-firebase-setup)
12. [Admin Panel Firebase Setup](#12-admin-panel-firebase-setup)
13. [Cost Estimate](#13-cost-estimate)
14. [Environment Variables & Config](#14-environment-variables--config)

---

## 1. Firebase Service Map

Every piece of the ShilpSetu backend maps to a Firebase service:

| Need | Firebase Service | Details |
|---|---|---|
| **Authentication** | Firebase Auth | Phone OTP (WhatsApp fallback via Twilio), Google Sign-In for buyers, Anonymous for guests |
| **Primary Database** | Cloud Firestore | All structured data — users, products, orders, reviews, GI tags |
| **Real-time Updates** | Firestore Listeners | Order status, inventory, price alerts — no WebSocket server needed |
| **File Storage** | Firebase Storage | Product images, videos, voice notes, PDFs (invoices, packing slips) |
| **Background AI Processing** | Cloud Functions (Node.js / Python) | Gemini calls, image processing, pricing scrapes, video reel generation |
| **Scheduled Jobs (Cron)** | Cloud Scheduler → Cloud Functions | Weekly Mandi Intelligence, Monday price pulse, review reminders |
| **Push Notifications** | Firebase Cloud Messaging (FCM) | Order updates, price alerts, new match notifications |
| **In-app Notifications** | Firestore `notifications` collection | In-app notification centre |
| **Admin Web Panel** | Firebase Hosting | React app served at `admin.shilpsetu.in` |
| **Buyer Storefront** | Firebase Hosting | SEO-optimised Next.js storefront |
| **Analytics** | Firebase Analytics | Buyer behaviour, product views, funnel analysis |
| **Error Tracking** | Firebase Crashlytics | Android app crashes |
| **Remote Config** | Firebase Remote Config | Feature flags, A/B test configs, commission rates |
| **Performance** | Firebase Performance | App startup time, network latency monitoring |
| **Beta Testing** | Firebase App Distribution | Distribute test APKs to team + judges |
| **Vector Search** | Firestore + Cloud Functions | Store embeddings as arrays, compute cosine similarity in Cloud Functions |
| **Caching** | Firestore Cache + Client-side | Offline persistence for Mela Mode |
| **Offline Sync** | Firestore Offline Persistence | Flutter's built-in Firestore offline mode (replaces Drift/SQLite) |
| **Task Queue** | Cloud Tasks | Retry failed AI processing jobs |
| **Secrets Management** | Firebase App Check + Secret Manager | API keys secured, app integrity verified |

---

## 2. Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        SHILPSETU PLATFORM                              │
├─────────────────┬──────────────────────┬──────────────────────────────┤
│   SELLER APP    │     BUYER APP         │    ADMIN WEB PANEL           │
│   Flutter/Dart  │     Flutter/Dart       │    React + Firebase SDK      │
│   (Android)     │     (Android)          │    Firebase Hosting          │
└────────┬────────┴──────────┬────────────┴──────────────┬──────────────┘
         │                   │                             │
         └───────────────────┼─────────────────────────────┘
                             │  Firebase SDK (flutterfire)
                             ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        FIREBASE PROJECT                                │
│                                                                        │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │ Firebase    │  │  Cloud       │  │  Firebase    │  │ Firebase  │  │
│  │ Auth        │  │  Firestore   │  │  Storage     │  │ FCM       │  │
│  │ Phone OTP   │  │  (NoSQL DB)  │  │  Images/     │  │ Push      │  │
│  │ Google      │  │  Real-time   │  │  Videos/PDFs │  │ Notifs    │  │
│  └─────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│                                                                        │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │ Cloud       │  │  Cloud       │  │  Firebase    │  │ Firebase  │  │
│  │ Functions   │  │  Scheduler   │  │  Hosting     │  │ Analytics │  │
│  │ (AI/Logic)  │  │  (Cron Jobs) │  │  (Web Panel) │  │           │  │
│  └─────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│                                                                        │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │ Remote      │  │  Crashlytics │  │  App         │  │ App Check │  │
│  │ Config      │  │  (Errors)    │  │  Distribution│  │ (Security)│  │
│  │ (Flags/A/B) │  │              │  │  (APK share) │  │           │  │
│  └─────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │  Cloud Functions call external APIs
                                  ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     EXTERNAL SERVICES                                  │
│  Gemini API  │  Bhashini  │  SerpAPI  │  Razorpay  │  Delhivery       │
│  (AI/LLM)    │  (Voice)   │  (Prices) │  (Payments)│  (Logistics)     │
│  GeM API     │  ONDC SDK  │  Twilio   │  Maps SDK  │  FFmpeg(Modal)   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Firebase vs Previous Stack

| Old Stack | Firebase Replacement | Notes |
|---|---|---|
| FastAPI (Python) | Cloud Functions (Node.js) | Event-driven, auto-scaled, no server management |
| PostgreSQL | Cloud Firestore | NoSQL, real-time, offline-capable |
| pgvector | Firestore arrays + CF cosine similarity | Store embedding as `number[]`, compute in Cloud Functions |
| Redis (cache) | Firestore client cache + Remote Config | Offline persistence built into Flutter SDK |
| Redis (sessions) | Firebase Auth JWT | Firebase manages session tokens automatically |
| Celery (task queue) | Cloud Tasks + Cloud Functions | Retry logic, backoff built-in |
| Celery Beat (cron) | Cloud Scheduler | Fully managed cron → triggers Cloud Functions |
| WebSocket | Firestore real-time listeners | `.snapshots()` stream in Flutter — same or better UX |
| Nginx | Firebase Hosting | Auto-CDN, SSL, global edge network |
| S3 / Cloudinary | Firebase Storage | Auto-CDN, Firebase Security Rules |
| Sentry | Firebase Crashlytics | Built-in, zero config |
| Logfire | Firebase Performance | Built-in, zero config |
| GitHub Actions | Firebase CLI + GitHub Actions | Firebase provides deploy CLI |
| Drift (SQLite) | Firestore offline persistence | `FirebaseFirestore.instance.settings(persistenceEnabled: true)` |
| WorkManager | Firestore offline + Cloud Functions | Sync happens automatically when connectivity restored |
| Twilio OTP | Firebase Auth (Phone) | Built-in — no Twilio needed for auth |
| JWT (manual) | Firebase ID Tokens | Auto-managed, auto-refreshed |
| Railway.app | Not needed | Firebase is serverless |

---

## 4. Firestore Data Model

> Firestore is a **document-collection** NoSQL database. Below is the full collection hierarchy.

### 4.1 Collection: `users` (Artisan Sellers)

```
/users/{userId}
  ├── uid: string                    (Firebase Auth UID)
  ├── phoneNumber: string            ("+91XXXXXXXXXX")
  ├── name: string
  ├── languagePref: string           ("hi" | "bn" | "ta" | "te" | ...)
  ├── village: string
  ├── district: string
  ├── state: string
  ├── craftTypes: string[]           (["Madhubani", "Warli"])
  ├── aadhaarVerified: boolean
  ├── udyamRegNo: string
  ├── gemSellerId: string
  ├── gstin: string
  ├── shilpiScore: number            (0-100, computed by CF)
  ├── profilePhotoUrl: string        (Firebase Storage URL)
  ├── storefrontSlug: string         ("artisan-name-village")
  ├── bio: string
  ├── yearsExperience: number
  ├── walletBalance: number          (in paise, integer)
  ├── upiId: string
  ├── bankAccount: map               ({accountNo, ifsc, bankName})
  ├── totalEarnings: number
  ├── fcmToken: string               (for push notifications)
  ├── embedding: number[]            (768-dim Gemini embedding of profile)
  ├── createdAt: timestamp
  └── updatedAt: timestamp

  Subcollection: /users/{userId}/products (reference only, actual in /products)
  Subcollection: /users/{userId}/notifications
```

### 4.2 Collection: `products`

```
/products/{productId}
  ├── sellerId: string               (ref to /users/{uid})
  ├── titleEn: string
  ├── titleHi: string
  ├── titleRegional: string
  ├── descriptionEn: string
  ├── descriptionHi: string
  ├── craftType: string
  ├── material: string
  ├── color: string[]
  ├── sizeCm: string
  ├── technique: string
  ├── region: string
  ├── giTag: string | null
  ├── giVerified: boolean
  ├── authenticityScore: number      (0-100, Gemini Vision)
  ├── priceSuggested: number
  ├── priceListed: number
  ├── priceMin: number               (bulk floor price)
  ├── moq: number                    (minimum order quantity)
  ├── imageUrls: string[]            (Firebase Storage URLs)
  ├── videoReelUrl: string | null
  ├── storyEn: string                (Kahaani AI-generated)
  ├── storyHi: string
  ├── socialCaptions: map            ({instagram: string, facebook: string, whatsapp: string})
  ├── hsnCode: string
  ├── stockQuantity: number
  ├── status: string                 ("draft"|"active"|"paused"|"sold"|"gem_submitted"|"ondc_listed")
  ├── gemListingId: string | null
  ├── ondcProductId: string | null
  ├── embedding: number[]            (768-dim, for semantic search & B2B matching)
  ├── viewCount: number
  ├── wishlistCount: number
  ├── avgRating: number
  ├── reviewCount: number
  ├── isFeatured: boolean
  ├── festiveTag: string | null      ("diwali" | "eid" | "christmas")
  ├── createdAt: timestamp
  └── updatedAt: timestamp

  Subcollection: /products/{productId}/reviews
  Subcollection: /products/{productId}/questions
  Subcollection: /products/{productId}/priceHistory
```

### 4.3 Collection: `buyers`

```
/buyers/{buyerId}
  ├── uid: string                    (Firebase Auth UID)
  ├── phoneNumber: string
  ├── email: string
  ├── name: string
  ├── buyerType: string              ("consumer" | "b2b")
  ├── orgName: string | null
  ├── gstin: string | null
  ├── craftInterests: string[]
  ├── preferredRegions: string[]
  ├── moqRequirement: number
  ├── maxBudgetPerUnit: number
  ├── rewardPoints: number
  ├── fcmToken: string
  ├── embedding: number[]            (for B2B buyer matchmaking)
  ├── createdAt: timestamp
  └── updatedAt: timestamp

  Subcollection: /buyers/{buyerId}/wishlists
  Subcollection: /buyers/{buyerId}/addresses
  Subcollection: /buyers/{buyerId}/notifications
```

### 4.4 Collection: `orders`

```
/orders/{orderId}
  ├── productId: string
  ├── buyerId: string
  ├── sellerId: string
  ├── quantity: number
  ├── unitPrice: number
  ├── totalAmount: number
  ├── platformFee: number
  ├── sellerPayout: number
  ├── paymentMethod: string
  ├── paymentStatus: string          ("pending"|"paid"|"failed"|"refunded")
  ├── razorpayOrderId: string
  ├── razorpayPaymentId: string
  ├── deliveryAddress: map
  ├── shippingCarrier: string
  ├── trackingId: string
  ├── status: string                 ("pending"|"confirmed"|"packed"|"shipped"|"delivered"|"cancelled"|"returned")
  ├── isB2b: boolean
  ├── giftMessage: string | null
  ├── estimatedDelivery: timestamp
  ├── packingSlipUrl: string | null  (Firebase Storage PDF)
  ├── buyerRating: number | null
  ├── buyerReview: string | null
  ├── reviewPhotoUrls: string[]
  ├── createdAt: timestamp
  └── updatedAt: timestamp

  Subcollection: /orders/{orderId}/statusHistory   (full event log)
```

### 4.5 Collection: `giTags`

```
/giTags/{giId}
  ├── giName: string
  ├── registrationNo: string
  ├── craftTypes: string[]
  ├── state: string
  ├── districts: string[]
  ├── description: string
  ├── registeredYear: number
  └── pricePremiumPct: number        (20-40)
```

### 4.6 Collection: `craftHeritage` (Kahaani knowledge base)

```
/craftHeritage/{heritageId}
  ├── craftType: string
  ├── historyText: string
  ├── techniqueDesc: string
  ├── culturalSignificance: string
  ├── keyRegions: string[]
  ├── artisanCommunities: string[]
  ├── source: string
  └── embedding: number[]            (for RAG retrieval)
```

### 4.7 Collection: `notifications`

```
/notifications/{userId}/items/{notifId}
  ├── type: string                   ("order_update"|"price_alert"|"new_match"|"gi_detected"|"scheme_alert")
  ├── title: string
  ├── body: string
  ├── data: map                      (orderId, productId, etc.)
  ├── read: boolean
  └── createdAt: timestamp
```

### 4.8 Collection: `priceCache`

```
/priceCache/{craftType_material_size}
  ├── minPrice: number
  ├── maxPrice: number
  ├── medianPrice: number
  ├── platformData: map              ({amazon, flipkart, meesho, gem})
  ├── giPremiumPrice: number
  └── cachedAt: timestamp            (TTL: 24 hours enforced in CF)
```

### 4.9 Collection: `platform` (Admin Config)

```
/platform/config
  ├── commissionRates: map           ({default: 0.08, textiles: 0.06, ...})
  ├── featuredListingIds: string[]
  ├── activeCampaigns: map[]
  ├── maintenanceMode: boolean
  └── updatedAt: timestamp

/platform/stats                      (aggregated by Cloud Functions)
  ├── totalSellers: number
  ├── totalBuyers: number
  ├── totalOrders: number
  ├── totalGmv: number
  ├── activeListings: number
  └── updatedAt: timestamp
```

### 4.10 Collection: `b2bBuyers`

```
/b2bBuyers/{buyerId}
  ├── orgName: string
  ├── contactEmail: string
  ├── craftInterests: string[]
  ├── minOrderQty: number
  ├── maxBudgetPerUnit: number
  ├── preferredRegions: string[]
  └── embedding: number[]
```

---

## 5. Firebase Cloud Functions Catalog

All backend logic lives in Cloud Functions. Written in **Node.js 20** (some AI-heavy ones in Python 3.12).

### 5.1 Auth Triggers

| Function | Trigger | Action |
|---|---|---|
| `onUserCreate` | `auth.user().onCreate` | Create `/users/{uid}` or `/buyers/{uid}` document, set default language, send welcome FCM |
| `onUserDelete` | `auth.user().onDelete` | Soft-delete user data, cancel pending orders, release reserved stock |

### 5.2 Firestore Triggers

| Function | Trigger | Action |
|---|---|---|
| `onProductCreate` | `products/{id}` created | Generate Gemini embedding → store in product doc; trigger Kahaani story generation; trigger GI tag detection; trigger B2B matchmaking |
| `onProductUpdate` | `products/{id}` updated | Re-generate embedding if title/description changed; re-run GI check if region changed |
| `onOrderCreate` | `orders/{id}` created | Send FCM to seller ("New order!"); send FCM to buyer (confirmation); trigger packing slip PDF generation; deduct stock from product |
| `onOrderStatusChange` | `orders/{id}` updated (status field) | FCM to buyer on each status change; trigger payout when delivered; trigger Shiprocket pickup on "packed" |
| `onReviewCreate` | `products/{id}/reviews/{r}` created | Update product avgRating; generate Gemini "Buyer Highlights" summary; fake review ML check; award 10 reward points to buyer |
| `onShilpiScoreUpdate` | `users/{id}` updated | Recalculate Shilpi Score from all components; check GeM eligibility threshold |

### 5.3 HTTPS Callable Functions (called directly from app)

| Function | Called By | Action |
|---|---|---|
| `generateProductDescription` | Seller App | Input: voice transcript + product photo URL → Bhashini translate → Gemini extract attributes → return structured JSON |
| `enhanceProductImage` | Seller App | Input: Storage path → Real-ESRGAN upscale → rembg background removal → 3 variants → return new Storage URLs |
| `getPriceSuggestion` | Seller App | Input: craft_type, material, size → check Firestore priceCache → if stale: SerpAPI scrape → ML blend → return price range |
| `detectGITag` | Cloud Function (onProductCreate) | Input: craft_type, state, district → fuzzy match GI database → Gemini verify → return gi_tag or null |
| `generateKahaani` | Cloud Function (onProductCreate) | Input: productId → vector search craftHeritage → Gemini RAG → write story to product doc |
| `generateVideoReel` | Seller App | Input: productId → fetch 3 images from Storage → FFmpeg (Modal.com) → store reel in Storage → update product doc |
| `generateSocialCaptions` | Seller App | Input: productId → Gemini Pro → 3 caption variants (Instagram/Facebook/WhatsApp) → write to product doc |
| `submitToGeM` | Seller App | Input: productId → validate eligibility → auto-fill GeM API payload → submit → write gemListingId to product doc |
| `submitToONDC` | Cloud Function | Input: productId → ONDC SDK → write ondcProductId to product doc |
| `createRazorpayOrder` | Buyer App | Input: productId, quantity, address → create Razorpay order → return order_id + key |
| `verifyRazorpayPayment` | Buyer App | Input: razorpay_order_id, payment_id, signature → verify → create /orders/{id} doc → trigger order flow |
| `getNegotiationScript` | Seller App / Bot | Input: buyerMessage, productId → Gemini Pro → counter-offer script in artisan language |
| `getGovernmentSchemes` | Seller App | Input: userId → Gemini Pro → match profile to scheme DB → return list of applicable schemes |
| `runB2BMatchmaking` | Cloud Function (onProductCreate) | Input: productId → embedding similarity vs b2bBuyers → notify top matches |
| `getAuthenticityScore` | Cloud Function (onProductCreate) | Input: imageUrl → Gemini Vision → handmade verification score → write to product doc |
| `translateMessage` | Seller App | Input: text, sourceLang, targetLang → Bhashini NMT → return translation |
| `processBhashiniVoice` | Seller App | Input: audio file Storage path → Bhashini STT → Bhashini NMT → return English text |
| `requestShiprocketPickup` | Cloud Function (order status) | Input: orderId → Shiprocket API → create pickup → write tracking_id to order |
| `generatePackingSlip` | Cloud Function (onOrderCreate) | Input: orderId → PDF.js → write PDF to Storage → update order doc with packingSlipUrl |
| `checkDeliverabilityAndRate` | Buyer App | Input: pincode, weight → Shiprocket serviceability API → return carriers + rates |

### 5.4 Scheduled Functions (Cloud Scheduler)

| Function | Schedule | Action |
|---|---|---|
| `weeklyMandiIntelligence` | Every Monday 08:00 IST | For each active seller: re-scrape prices for all listings; compare to listed price; if >15% below market: send FCM + WhatsApp alert with one-tap update |
| `weeklyB2BBuyerDigest` | Every Monday 09:00 IST | For each B2B buyer: find new matching products from past week → send email digest |
| `refreshPriceCache` | Every day 06:00 IST | Refresh stale price cache entries (>24h old) for top 100 product types |
| `recomputeShilpiScores` | Every Sunday 23:00 IST | Recompute all seller Shilpi Scores from fresh data |
| `cleanExpiredSessions` | Every day 00:00 IST | Remove expired FCM tokens, clean up temp Storage files |
| `festiveCollectionBuilder` | Every day 06:00 IST | Check calendar; if within 2 weeks of festival: add festiveTag to relevant products |
| `reviewReminders` | Every day 10:00 IST | 3 days after delivery: FCM to buyer asking for review |

### 5.5 Storage Triggers

| Function | Trigger | Action |
|---|---|---|
| `onProductImageUpload` | `products/{uid}/raw/{filename}` created | Auto-trigger image enhancement pipeline → store enhanced versions in `products/{uid}/enhanced/` |
| `onVoiceNoteUpload` | `voice/{uid}/{filename}` created | Auto-trigger Bhashini STT → store transcript in Firestore |

---

## 6. Firebase Security Rules

### 6.1 Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Users: read own, write own, admin reads all
    match /users/{userId} {
      allow read: if request.auth != null &&
                    (request.auth.uid == userId || isAdmin());
      allow write: if request.auth != null &&
                     request.auth.uid == userId;
    }

    // Products: public read for active, write only by seller
    match /products/{productId} {
      allow read: if resource.data.status == 'active' ||
                    (request.auth != null &&
                     request.auth.uid == resource.data.sellerId);
      allow create: if request.auth != null &&
                      request.resource.data.sellerId == request.auth.uid;
      allow update: if request.auth != null &&
                      resource.data.sellerId == request.auth.uid;
      allow delete: if isAdmin();

      // Reviews: authenticated buyers only
      match /reviews/{reviewId} {
        allow read: if true;
        allow create: if request.auth != null &&
                        request.resource.data.buyerId == request.auth.uid;
        allow update, delete: if isAdmin();
      }
    }

    // Orders: buyer or seller of that order
    match /orders/{orderId} {
      allow read: if request.auth != null &&
                    (request.auth.uid == resource.data.buyerId ||
                     request.auth.uid == resource.data.sellerId ||
                     isAdmin());
      allow create: if request.auth != null;
      allow update: if request.auth != null &&
                      (request.auth.uid == resource.data.sellerId ||
                       isAdmin());
    }

    // Buyers: read own, admin reads all
    match /buyers/{buyerId} {
      allow read, write: if request.auth != null &&
                           request.auth.uid == buyerId;
    }

    // GI Tags: public read, admin write
    match /giTags/{giId} {
      allow read: if true;
      allow write: if isAdmin();
    }

    // Craft Heritage: public read, admin write
    match /craftHeritage/{id} {
      allow read: if true;
      allow write: if isAdmin();
    }

    // Price Cache: authenticated read, CF write only
    match /priceCache/{cacheId} {
      allow read: if request.auth != null;
      allow write: if false; // Only Cloud Functions write this
    }

    // Platform config: admin only
    match /platform/{doc} {
      allow read: if request.auth != null;
      allow write: if isAdmin();
    }

    // Notifications: own only
    match /notifications/{userId}/items/{notifId} {
      allow read, write: if request.auth != null &&
                           request.auth.uid == userId;
    }

    function isAdmin() {
      return request.auth != null &&
             request.auth.token.admin == true;
    }
  }
}
```

### 6.2 Firebase Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    // Product images: public read, seller write
    match /products/{sellerId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null &&
                     request.auth.uid == sellerId &&
                     request.resource.size < 10 * 1024 * 1024 && // 10MB max
                     request.resource.contentType.matches('image/.*');
    }

    // Voice notes: seller only
    match /voice/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null &&
                           request.auth.uid == userId &&
                           request.resource.size < 5 * 1024 * 1024; // 5MB max
    }

    // Video reels: public read, CF write only
    match /reels/{productId}/{allPaths=**} {
      allow read: if true;
      allow write: if false; // Only Cloud Functions write
    }

    // Invoices & slips: buyer or seller of that order
    match /documents/orders/{orderId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if false; // Only Cloud Functions write
    }

    // Profile photos
    match /profiles/{userId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null &&
                     request.auth.uid == userId &&
                     request.resource.size < 2 * 1024 * 1024; // 2MB max
    }
  }
}
```

---

## 7. Firebase Storage Structure

```
Firebase Storage Bucket
│
├── /products/
│   └── /{sellerId}/
│       ├── /raw/           ← Original uploads (temp, cleaned up by CF)
│       ├── /enhanced/      ← AI-processed variants
│       │   ├── {productId}_white.jpg
│       │   ├── {productId}_lifestyle.jpg
│       │   └── {productId}_festive.jpg
│       └── /gallery/       ← Additional product photos
│
├── /reels/
│   └── /{productId}/
│       └── reel_1080x1920.mp4
│
├── /voice/
│   └── /{userId}/          ← Voice note uploads (temp, deleted after transcription)
│
├── /profiles/
│   └── /{userId}/
│       └── avatar.jpg
│
├── /documents/
│   └── /orders/
│       └── /{orderId}/
│           ├── packing_slip.pdf
│           ├── invoice.pdf
│           └── shipping_label.pdf
│
└── /admin/
    └── /exports/           ← CSV/PDF exports for government reporting
```

---

## 8. Gemini API via Firebase

Firebase Extensions and Cloud Functions both support calling the Gemini API directly.

### 8.1 Firebase Extension: `firestore-genai-chatbot`
Use the official **"Gemini Chatbot with Cloud Firestore"** Firebase Extension for:
- Sahaayak AI Negotiation Coach (conversational)
- AI Customer Support Chat
- Government Scheme Advisor

### 8.2 Direct Gemini API Calls from Cloud Functions

```javascript
// Cloud Function example: generateProductDescription
const { GoogleGenerativeAI } = require("@google/generative-ai");

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

exports.generateProductDescription = functions.https.onCall(async (data, context) => {
  const { translatedText, craftType, region } = data;

  const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

  const prompt = `
    You are a product cataloger for Indian handicrafts.
    Extract structured attributes and generate listings.
    Return ONLY valid JSON with keys:
    craft_type, material, color[], size, technique, region,
    gi_candidate (bool), title_en, title_hi, description_en,
    description_hi, tags[]

    INPUT: "${translatedText}"
    CRAFT TYPE HINT: ${craftType}
    REGION: ${region}
  `;

  const result = await model.generateContent(prompt);
  const text = result.response.text();
  return JSON.parse(text);
});
```

### 8.3 Gemini Embeddings for Vector Search

```javascript
// Generate embedding and store in Firestore
const embeddingModel = genAI.getGenerativeModel({ model: "text-embedding-004" });

async function generateAndStoreEmbedding(productId, text) {
  const result = await embeddingModel.embedContent(text);
  const embedding = result.embedding.values; // number[]

  await admin.firestore()
    .collection('products')
    .doc(productId)
    .update({ embedding });
}

// Cosine similarity search (in Cloud Function)
function cosineSimilarity(a, b) {
  const dot = a.reduce((sum, val, i) => sum + val * b[i], 0);
  const magA = Math.sqrt(a.reduce((sum, val) => sum + val * val, 0));
  const magB = Math.sqrt(b.reduce((sum, val) => sum + val * val, 0));
  return dot / (magA * magB);
}

async function findSimilarProducts(queryEmbedding, limit = 10) {
  const productsSnap = await admin.firestore()
    .collection('products')
    .where('status', '==', 'active')
    .get();

  const scored = productsSnap.docs.map(doc => ({
    id: doc.id,
    data: doc.data(),
    score: cosineSimilarity(queryEmbedding, doc.data().embedding || [])
  }));

  return scored
    .sort((a, b) => b.score - a.score)
    .slice(0, limit);
}
```

> **Note:** For scale (>10,000 products), migrate vector search to **Vertex AI Vector Search** (same Google Cloud ecosystem as Firebase).

### 8.4 Gemini Vision (Image Analysis)

```javascript
exports.checkProductAuthenticity = functions.https.onCall(async (data, context) => {
  const { imageUrl } = data;

  const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

  // Fetch image from Firebase Storage
  const imageData = await fetchImageAsBase64(imageUrl);

  const result = await model.generateContent([
    {
      inlineData: {
        mimeType: "image/jpeg",
        data: imageData
      }
    },
    `Analyze this product photo for handmade authenticity markers.
     Look for: natural imperfections, brush stroke patterns, weave irregularities,
     hand-finishing marks characteristic of Indian handicrafts.
     Return JSON: { authenticityScore: 0-100, isHandmade: boolean, markers: string[], warnings: string[] }`
  ]);

  return JSON.parse(result.response.text());
});
```

---

## 9. External APIs (What Firebase Can't Do)

These are called exclusively from **Cloud Functions** — API keys never exposed to client:

| Service | Purpose | Called From | Key Storage |
|---|---|---|---|
| **Gemini API** | All AI/LLM tasks | Cloud Functions | Firebase Secret Manager |
| **Bhashini ULCA** | STT, NMT, TTS in 22 Indian languages | Cloud Functions | Firebase Secret Manager |
| **SerpAPI** | Live price scraping (Amazon, Flipkart, Meesho, GeM) | Cloud Functions | Firebase Secret Manager |
| **Razorpay** | Payment processing, payouts, refunds | Cloud Functions | Firebase Secret Manager |
| **Delhivery / Shiprocket** | Logistics, tracking, pickup scheduling | Cloud Functions | Firebase Secret Manager |
| **GeM Seller API** | Government marketplace submission | Cloud Functions | Firebase Secret Manager |
| **ONDC SDK** | Open network listing | Cloud Functions | Firebase Secret Manager |
| **Replicate API** | Real-ESRGAN upscaling, rembg | Cloud Functions | Firebase Secret Manager |
| **Modal.com** | FFmpeg serverless video reel generation | Cloud Functions | Firebase Secret Manager |
| **Google Maps SDK** | Address picker, delivery map | Flutter app (client) | Firebase Remote Config |
| **Twilio (WhatsApp)** | WhatsApp notifications (optional — FCM is primary) | Cloud Functions | Firebase Secret Manager |

> **Security Note:** All API keys are stored in **Google Cloud Secret Manager** and accessed by Cloud Functions with `secretmanager.versions.access` IAM permission. The Flutter app never sees any API keys.

---

## 10. Full Tech Stack (Firebase Edition)

| Layer | Technology | Purpose |
|---|---|---|
| **Mobile App** | Flutter 3.x (Dart) | Android (primary) + iOS |
| **Firebase SDK** | FlutterFire (firebase_core, firebase_auth, cloud_firestore, firebase_storage, firebase_messaging, firebase_analytics, firebase_crashlytics, firebase_remote_config, firebase_performance, cloud_functions) | All Firebase services |
| **State Management** | Riverpod 2.x + Firebase streams | Real-time state from Firestore listeners |
| **Offline Mode** | Firestore offline persistence | Built-in — replaces Drift/SQLite |
| **Auth** | Firebase Auth (Phone OTP) | Zero-config phone authentication |
| **Database** | Cloud Firestore | NoSQL, real-time, scalable |
| **File Storage** | Firebase Storage | Images, videos, PDFs |
| **Backend Logic** | Cloud Functions (Node.js 20) | All server-side logic |
| **AI Processing** | Cloud Functions (Python 3.12) for heavy AI | Gemini, rembg, FFmpeg calls |
| **Scheduled Jobs** | Cloud Scheduler → Cloud Functions | Cron-style triggers |
| **Push Notifications** | Firebase Cloud Messaging | Order updates, price alerts |
| **Admin Web Panel** | React 18 + Firebase SDK | Hosted on Firebase Hosting |
| **AI / LLM** | Gemini 1.5 Pro / Flash | All AI features |
| **Embeddings** | Gemini text-embedding-004 | Semantic search + matching |
| **Voice** | Bhashini ULCA (via CF) | Regional language STT/TTS |
| **Translation** | Bhashini NMT (via CF) | 22 Indian languages |
| **Payments** | Razorpay (via CF) | UPI, cards, EMI, wallets |
| **Logistics** | Delhivery / Shiprocket (via CF) | Shipping + tracking |
| **Price Scraping** | SerpAPI (via CF) | Market price intelligence |
| **Image AI** | Replicate API (rembg, Real-ESRGAN) | Background removal + upscaling |
| **Video** | Modal.com + FFmpeg (via CF) | Product reel generation |
| **Error Tracking** | Firebase Crashlytics | Android crash reporting |
| **Analytics** | Firebase Analytics | User behaviour analytics |
| **Feature Flags** | Firebase Remote Config | A/B tests, feature toggles |
| **Performance** | Firebase Performance Monitoring | App performance |
| **APK Distribution** | Firebase App Distribution | Share builds with judges |
| **CI/CD** | GitHub Actions + Firebase CLI | Auto-deploy on push |

---

## 11. Flutter Firebase Setup

### 11.1 pubspec.yaml (Firebase dependencies)

```yaml
dependencies:
  flutter:
    sdk: flutter

  # Firebase Core
  firebase_core: ^2.27.0

  # Auth
  firebase_auth: ^4.17.5

  # Database
  cloud_firestore: ^4.15.5

  # Storage
  firebase_storage: ^11.6.5

  # Cloud Functions
  cloud_functions: ^4.6.6

  # Messaging (Push Notifications)
  firebase_messaging: ^14.7.15

  # Analytics
  firebase_analytics: ^10.8.5

  # Crashlytics
  firebase_crashlytics: ^3.4.15

  # Remote Config
  firebase_remote_config: ^4.3.15

  # Performance
  firebase_performance: ^0.9.3+16

  # State Management
  flutter_riverpod: ^2.5.1
  riverpod_annotation: ^2.3.4

  # UI
  go_router: ^14.1.4
  google_fonts: ^6.2.1
  cached_network_image: ^3.3.1
  image_picker: ^1.1.0
  camera: ^0.11.0+2
  fl_chart: ^0.67.0
  shimmer: ^3.0.0
  lottie: ^3.1.0

  # Payments
  razorpay_flutter: ^1.3.6

  # Maps
  google_maps_flutter: ^2.6.1
  geolocator: ^11.1.0

  # Utilities
  connectivity_plus: ^6.0.3
  path_provider: ^2.1.3
  share_plus: ^9.0.0
  url_launcher: ^6.2.5
  intl: ^0.19.0
  uuid: ^4.4.0
  record: ^5.1.0             # Voice recording
  audioplayers: ^6.0.0        # Voice playback
  file_picker: ^8.0.6
  pdf: ^3.11.0               # Packing slip PDF
  printing: ^5.13.0
```

### 11.2 Firebase Initialization (main.dart)

```dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  // Enable Firestore offline persistence (Mela Mode)
  FirebaseFirestore.instance.settings = const Settings(
    persistenceEnabled: true,
    cacheSizeBytes: Settings.CACHE_SIZE_UNLIMITED,
  );

  // Setup Crashlytics
  FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterFatalError;

  runApp(
    ProviderScope(
      child: ShilpSetuApp(),
    ),
  );
}
```

---

## 12. Admin Panel Firebase Setup

The Admin Web Panel is a **React 18** app deployed to **Firebase Hosting**.

```
admin-panel/
├── src/
│   ├── firebase.ts           ← Firebase config + Admin SDK init
│   ├── pages/
│   │   ├── Dashboard.tsx
│   │   ├── Sellers.tsx
│   │   ├── Buyers.tsx
│   │   ├── Products.tsx
│   │   ├── Orders.tsx
│   │   ├── Analytics.tsx
│   │   ├── GemONDC.tsx
│   │   ├── AI Config.tsx
│   │   └── Settings.tsx
│   └── components/
│       ├── Charts/
│       ├── Tables/
│       └── Maps/
├── firebase.json
└── .firebaserc
```

**Admin Custom Claims** — Set via Cloud Function after admin verification:

```javascript
// Cloud Function: setAdminClaim
exports.setAdminClaim = functions.https.onCall(async (data, context) => {
  if (!context.auth?.token.admin) throw new Error("Unauthorized");
  await admin.auth().setCustomUserClaims(data.uid, { admin: true });
});
```

---

## 13. Cost Estimate

### Firebase Spark (Free) Plan — Sufficient for SIH Demo

| Service | Free Tier | SIH Usage | Cost |
|---|---|---|---|
| Firebase Auth | Unlimited phone OTH | ~100 users | **₹0** |
| Cloud Firestore | 50K reads, 20K writes/day | Demo scale | **₹0** |
| Firebase Storage | 5 GB stored, 1 GB/day transfer | ~200 images | **₹0** |
| Cloud Functions | 2M invocations/month | Demo scale | **₹0** |
| Firebase Hosting | 10 GB storage, 360 MB/day | Admin panel | **₹0** |
| FCM | Unlimited | Notifications | **₹0** |
| Firebase Analytics | Unlimited | Always free | **₹0** |
| Crashlytics | Unlimited | Always free | **₹0** |

### External API Costs (SIH Demo)

| API | Free Tier | SIH Usage | Cost |
|---|---|---|---|
| Gemini 1.5 Flash | 15 RPM / 1M TPM free | AI features | **₹0** |
| Gemini 1.5 Pro | 2 RPM / 32K TPD free | Deep AI | **₹0** |
| Gemini Embeddings | 1500 RPD free | Search | **₹0** |
| Bhashini | Free (govt API) | Voice | **₹0** |
| SerpAPI | 100 free searches | Price scraping | **₹0** |
| Replicate | 500 free predictions | Image AI | **₹0** |
| Modal.com | $30 free credit | Video reels | **₹0** |
| Razorpay | Test mode | Payments | **₹0** |
| Shiprocket | Test mode | Logistics | **₹0** |

> **Total SIH Demo Cost: ₹0** — 100% on free tiers

---

## 14. Environment Variables & Config

All secrets stored in **Google Cloud Secret Manager** (not `.env` files):

```
GEMINI_API_KEY          → Cloud Secret Manager
BHASHINI_API_KEY        → Cloud Secret Manager
SERPAPI_KEY             → Cloud Secret Manager
RAZORPAY_KEY_ID         → Cloud Secret Manager
RAZORPAY_KEY_SECRET     → Cloud Secret Manager
SHIPROCKET_EMAIL        → Cloud Secret Manager
SHIPROCKET_PASSWORD     → Cloud Secret Manager
DELHIVERY_API_KEY       → Cloud Secret Manager
GEM_API_KEY             → Cloud Secret Manager
REPLICATE_API_TOKEN     → Cloud Secret Manager
MODAL_API_KEY           → Cloud Secret Manager
TWILIO_ACCOUNT_SID      → Cloud Secret Manager (optional)
TWILIO_AUTH_TOKEN       → Cloud Secret Manager (optional)
```

**firebase.json** (deploy config):

```json
{
  "firestore": {
    "rules": "firestore.rules",
    "indexes": "firestore.indexes.json"
  },
  "storage": {
    "rules": "storage.rules"
  },
  "functions": {
    "source": "functions",
    "runtime": "nodejs20"
  },
  "hosting": [
    {
      "target": "admin",
      "public": "admin-panel/build",
      "rewrites": [{ "source": "**", "destination": "/index.html" }]
    }
  ]
}
```

---

> **"Firebase gives ShilpSetu enterprise-grade infrastructure at zero cost for SIH — authentication, real-time database, file storage, serverless functions, push notifications, analytics, and crash reporting all from one Google Cloud console."**

---

*Document Version: 2.0 | Firebase Architecture | SIH 2026 | ShilpSetu*
*Updated: August 2026 | Classification: Internal Technical Reference*

# Dhaaga — Development Log
**Project**: Dhaaga / ShilpSetu | **Hackathon**: Smart India Hackathon 2026
**Language**: Kotlin + Jetpack Compose | **Backend**: Firebase (Firestore, Auth, Storage)

---

## ✅ Session 1 — Foundation & Prototype Screens
**Date**: 2026-08-26  
**Status**: Prototype Complete (Phase 0 + 1 + 2 partial)

---

### ✅ Major Milestones Completed

#### 1. Project Decision
- **Confirmed**: Native Kotlin Android (not Flutter)
- Read all 4 design docs: Master Plan, Feature Document, Firebase Architecture, Technical Workflows

#### 2. Android Project Setup
- **`android/settings.gradle.kts`** — Standalone Kotlin project (Flutter removed)
- **`android/build.gradle.kts`** — Root build with Compose + Firebase + Crashlytics plugins
- **`android/app/build.gradle.kts`** — App-level: Compose, Coil, Firebase full suite, Coroutines
- **`android/gradle/libs.versions.toml`** — Version catalog (AGP 9.2.1, Kotlin 2.3.21, Compose BOM 2026.05.01)
- **`android/gradle.properties`** — Gradle JVM settings
- **`android/gradle/wrapper/`** — Gradle wrapper (9.6.1) copied from working Lore project

#### 3. AndroidManifest
- **`AndroidManifest.xml`** — Permissions: INTERNET, CAMERA, READ_MEDIA_IMAGES, POST_NOTIFICATIONS, RECORD_AUDIO
- Configured for edge-to-edge transparent status bar

#### 4. Resources
- **`res/values/strings.xml`** — App name: "Dhaaga"
- **`res/values/colors.xml`** — Saffron launcher background
- **`res/values/themes.xml`** — Transparent status/nav bars for Compose
- **`res/xml/backup_rules.xml`** + **`data_extraction_rules.xml`** — Required XML

#### 5. Design System
- **`ui/theme/Color.kt`** — Full Dhaaga palette:
  - Saffron Primary: `#D4521A`
  - Gold Primary Light: `#F5A623`
  - Teal Accent: `#2BB5A0`
  - Cream Background: `#FFF8EE`
  - Peach Card: `#FDEBD0`
  - Deep Brown text, GI Badge Green, Error Red
- **`ui/theme/Type.kt`** — DhaagaTypography (all 13 Material3 text styles)
- **`ui/theme/Theme.kt`** — DhaagaTheme (light, edge-to-edge, transparent bars)

#### 6. Data Layer
- **`data/model/UserModel.kt`** — Full user model (seller/buyer, village, craft types, Shilpi Score, GI, wallet)
- **`data/model/ProductModel.kt`** — Full product model (GI Tag, authenticity score, price in paise, story)
- **`data/model/OrderModel.kt`** — Order + Address + CartItem models
- **`data/mock/MockData.kt`** — 6 real Indian craft products, 22 languages, mock orders, mock cart

#### 7. Navigation
- **`navigation/Routes.kt`** — All route constants with helpers (productDetail, orderTracking, etc.)

#### 8. State Management
- **`AppViewModel.kt`** — Central state: auth, products, wishlist, cart (add/remove/update quantity)

#### 9. Screens Built

| Screen | File | Status |
|--------|------|--------|
| Splash | `ui/splash/SplashScreen.kt` | ✅ Complete |
| Language Selection | `ui/onboarding/LanguageSelectionScreen.kt` | ✅ Complete |
| Role Selection | `ui/onboarding/RoleSelectionScreen.kt` | ✅ Complete |
| Phone OTP | `ui/onboarding/PhoneOtpScreen.kt` | ✅ Complete |
| Profile Setup | `ui/onboarding/ProfileSetupScreen.kt` | ✅ Complete |
| Home (Seller + Buyer) | `ui/home/HomeScreen.kt` | ✅ Complete |
| Product Detail | `ui/product/ProductDetailScreen.kt` | ✅ Complete |
| Seller Dashboard | `ui/seller/SellerDashboardScreen.kt` | ✅ Complete |
| My Listings | `ui/seller/MyListingsScreen.kt` | ✅ Complete |
| Add Product | `ui/seller/AddProductScreen.kt` | ✅ Complete |
| Cart | `ui/buyer/CartScreen.kt` | ✅ Complete |
| Wishlist | `ui/buyer/BuyerScreens.kt` | ✅ Complete |
| My Orders | `ui/buyer/BuyerScreens.kt` | ✅ Complete |
| Profile | `ui/profile/ProfileScreen.kt` | ✅ Complete |

#### 10. Main Activity + Navigation Graph
- **`MainActivity.kt`** — Full Compose NavHost with:
  - Splash → Language → Role → OTP → Profile → Home flow
  - Seller branch: Dashboard / My Listings / Add Product
  - Buyer branch: Wishlist / Cart / My Orders
  - Product Detail from any product tap
  - Profile / Logout flow

---

### 🔑 Key Design Decisions

1. **Based on working Lore project** — Used Lore's exact gradle version matrix (AGP 9.2.1, Kotlin 2.3.21) to avoid compatibility issues
2. **Mock data for prototype** — All data from `MockData.kt`, Firebase integration stubs ready
3. **OTP bypass for demo** — Any 6-digit code works in prototype mode; Firebase Auth integrated when google-services.json added
4. **Price in paise** — All monetary values stored as `Long` in paise (1/100 of rupee) per Indian fintech convention
5. **Coil for images** — Using Unsplash URLs in mock data for beautiful product photos in demo

---

### 🔧 Pending Before Phase 2

- [ ] Add real Firebase `google-services.json` (placeholder exists at `android/app/google-services.json`)
- [ ] Add launcher icon assets (mipmap folders — can borrow from Lore temporarily)
- [ ] Implement real Firebase Auth (phone OTP with `PhoneAuthProvider`)
- [ ] Connect Firestore reads/writes (replace MockData)
- [ ] Add Gemini AI image enhancement API call in AddProductScreen
- [ ] Add Bhashini voice-to-text in AddProductScreen
- [ ] Implement real payment (mock UPI flow for hackathon demo)
- [ ] Build Chat/messaging screen
- [ ] Implement GI Tag detection
- [ ] Add Search functionality (Firestore queries)

---

### 🏗️ Architecture Summary (Pure Native Android Project)

```
f:/Source Codes/Dhaaga/
├── app/
│   ├── src/main/
│   │   ├── java/com/dhaaga/app/
│   │   │   ├── MainActivity.kt         (NavHost, entry point)
│   │   │   ├── AppViewModel.kt         (central state)
│   │   │   ├── navigation/Routes.kt    
│   │   │   ├── data/
│   │   ├── model/             (User, Product, Order, Cart)
│   │   ├── mock/MockData.kt   (prototype data)
│   │   └── repository/        (ImageUploadRepository for Shared Hosting PHP API)
│   │   │   └── ui/
│   │   │       ├── theme/             (Color, Type, Theme)
│   │   │       ├── splash/
│   │   │       ├── onboarding/        (Language, Role, OTP, Profile)
│   │   │       ├── home/              (HomeScreen, ProductCard, BottomNavs)
│   │   │       ├── product/           (ProductDetailScreen)
│   │   │       ├── seller/            (Dashboard, Listings, AddProduct)
│   │   │       ├── buyer/             (Cart, Wishlist, Orders)
│   │   │       └── profile/           (ProfileScreen)
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   ├── build.gradle.kts
│   ├── google-services.json           (⚠️ placeholder — add real credentials)
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
├── .gitignore
└── gradle/
    ├── libs.versions.toml
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

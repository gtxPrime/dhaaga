# 🧵 Dhaaga v0.2.0 Release

> **Connecting Hands to Markets — From Village Craft to Global Cart in Under 5 Minutes.**

Version **0.2.0** of **Dhaaga (धागा • ShilpSetu)** brings major upgrades across AI Studio tooling, Google Sign-In authentication, complete buyer & seller commerce flows, 22-language vernacular localization, and modernized app branding.

---

### ✨ What's New in v0.2.0

#### 🔐 Authentication & Onboarding
* **Google Sign-In Integration:** Added Google One-Tap / Play Services authentication (`play-services-auth`) alongside phone OTP verification.
* **Streamlined Onboarding:** Refined artisan & buyer role onboarding with rich profile personalization, language selection, and state craft tagging.

#### 🪄 AI Studio & MagicHour Engine
* **MagicHour Service Integration:** Automated AI-powered product enhancements, high-fidelity studio background generation, and craft staging.
* **Animated Studio Experience:** Added interactive `StudioLoadingDialog` and real-time processing indicators in `AIStudioDialog`.
* **Smarter Gemini AI Prompts:** Enhanced voice-driven craft categorization, auto-description generation, and dynamic fair-price suggestions in `GeminiAIService`.

#### 🛍️ Buyer Experience & Marketplace
* **Rich Product Stories:** Cultural heritage narratives ("Kahaani"), craft origin details, and artisan profiles directly in `ProductDetailScreen`.
* **Enhanced Cart & Checkout:** Real-time coupon application, price breakdowns, and simulated UPI escrow payments in `CartScreen`.
* **Order Tracking & Discovery:** Dedicated buyer views for order history, real-time status updates, and category exploration in `BuyerScreens`.

#### 🧵 Seller Tools & Fair Pricing
* **Next-Gen Craft Listing:** Enhanced 5-step listing workflow in `AddProductScreen` with live preview, coupon builder, and auto-tagging.
* **Dynamic Pricing Cards:** Smart pricing guidance factoring material cost, artisan labor hours, and market fair-value (`DynamicPricingCard`).
* **Seller Hub:** Improved revenue analytics, craft listing state management, and stock visibility in `SellerDashboardScreen` and `MyListingsScreen`.

#### 🌐 Vernacular Localization (22 Languages)
* **Comprehensive Multi-Language Expansion:** Massively expanded dictionary in `AppLanguageManager` covering 22 official Indian languages with regional craft terms.

#### 🎨 Branding & Architecture
* **Adaptive App Launcher:** New vector adaptive icons and high-resolution mipmap assets (`ic_launcher.xml` / `ic_launcher_round.xml`).
* **Modernized Components:** Added reusable `CardAsyncImage`, `FontAwesomeIcons`, enhanced `NotionAvatar`, and updated palette tokens.

---

### 🧪 Quick Demo Logins

| Role | Login Method | Credential | Verification |
|:---|:---:|:---:|:---:|
| **Artisan (Kavita Devi - Madhubani)** | Phone OTP | `7668439019` | `123456` |
| **Buyer (Aarav Sharma - Connoisseur)** | Phone OTP | `7668439019` | `696969` |
| **Google Sign-In** | Google Account | *Any test account* | Google One-Tap |

---

### 📦 Installation & Release Artifacts

Download and install the signed APK attached below:
* **File:** `app-release.apk`
* **Version Name:** `0.2.0`
* **Version Code:** `2`
* **Signed with:** `alphaKey.jks`
* **Size:** ~20.75 MB (`21,756,906 bytes`)
* **Requirements:** Android 7.0+ (API 24+)
* **Target SDK:** Android 14+ (API 37)

```bash
adb install -r app-release.apk
```

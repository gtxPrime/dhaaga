# Dhaaga (धागा • ShilpSetu) - Release Changelog

## Version 0.2.0 (Patch Build / Milestone 2)

> **Connecting Hands to Markets: From Village Craft to Global Cart**

---

### Key Highlights & Major Additions

#### 1. GI Tag Authentication & Verification Engine
* **Government Registry Cross-Referencing:** Integrated `GITagRegistry.kt` covering official Indian Geographical Indications across textiles, handicrafts, handlooms, and terracotta.
* **Multimodal AI Verification:** Leveraged Gemini AI Vision and text models (`GeminiAIService.kt`) to automatically cross-reference craft titles, descriptions, materials, and regional imagery against official GI registry parameters.
* **Artisan GI Registry UI:** Added dedicated GI tag badge indicators, authenticity confidence scores, and an in-app searchable catalog of registered GI craft clusters.

#### 2. Interactive Mock Payment Gateway & Cloud Order System
* **Full-Flow Payment Simulation:** Introduced `MockPaymentDialog.kt` supporting simulated UPI apps (Google Pay, PhonePe, Paytm), Net Banking, Credit/Debit cards, and Cash on Delivery with realistic processing delays and instant haptic confirmation.
* **Dynamic Cloud Synchronization:** Orders are seamlessly written to and synchronized from Cloud Firestore (`orders` collection) in real time for both buyers and artisans.
* **Automated Stock Decrement & Cancellation Reversal:** Placing an order decrements product stock dynamically across marketplace feeds; cancelling an order automatically returns inventory to the artisan's active listing.

#### 3. Native GPS Geolocation Engine
* **High-Accuracy Workshop Location:** Implemented `LocationHelper.kt` utilizing native Android `LocationManager` and dual asynchronous/legacy background `Geocoder` to resolve user district, city, state, postal code, latitude, and longitude.
* **Live Marketplace Clustering:** Displays live artisan workshop and buyer delivery locations on the Home Screen header with animated real-time status indicators.
* **Regional Discovery Autofill:** Enabled one-tap GPS autofill in `AddProductScreen.kt` to authenticate artisan craft origin directly against official GI clusters.

#### 4. Hardened Runtime Permissions & Security
* **Location Permission Rationale:** Built `LocationPermissionDialog.kt` presenting a clear, transparent explanation of benefits (GI cluster matching, regional discovery, and shipping autofill) before requesting fine/coarse location permissions.
* **Android 13+ Notification Handling:** Added runtime `POST_NOTIFICATIONS` permission launcher in `MainActivity.kt` ensuring order confirmations and dispatch tracking notifications deliver smoothly on API 33+.
* **Graceful Failure Isolation:** All permission requests (Camera, Audio/Mic, Location, Notifications) include non-intrusive fallbacks and defensive exception handling to guarantee zero crashes if denied.

#### 5. Clean UI & Complete Emoji Purge
* **Enterprise Material Icons:** Replaced all informal emojis across UI screens, dialogs, button labels, toasts, and headers with official Jetpack Compose Material vector icons (`Icons.Default.*` and `Icons.AutoMirrored.*`).
* **AutoMirrored Directional Compliance:** Upgraded directional icons (`ArrowBack`, `Logout`, `List`, `MenuBook`, `Chat`) to `Icons.AutoMirrored` packages to support right-to-left (RTL) localized layouts cleanly.
* **Structured System Logging:** Converted all Logcat emoji prefixes to clean bracketed architectural tags: `[Location]`, `[Cache]`, `[User]`, `[Registry]`, `[Auth]`, `[Cloud]`, `[Firestore]`, `[Session]`, `[Cart]`, `[Language]`, `[Inventory]`, `[Orders]`, and `[Upload]`.

#### 6. Master Technical Architectural Report
* Generated comprehensive documentation: `Dhaaga_Master_Technical_Report.html` and `Dhaaga_Master_Technical_Report.pdf`.
* Contains deep-dive architectural blueprints, Firestore schema matrices, security rules analysis, AI pipeline breakdown, and a 6-person engineering task delegation roadmap.

---

### Commits Included in this Release

* `ec8225a` - `docs: add comprehensive architectural report and team delegation analysis`
* `445f10f` - `refactor(ui): purge emojis across UI and logs in favor of AutoMirrored Material icons`
* `c770185` - `feat(location): add native GPS geolocation engine and runtime permission handling`
* `3d6d59e` - `feat(ai): integrate GI Tag verification registry and multimodal AI cataloging`
* `b8fc796` - `feat(orders): implement mock payment gateway and dynamic cloud order tracking`

---

### Quick Verification Credentials

| Role | Method | Test Number | Verification Code |
| :--- | :---: | :---: | :---: |
| **Master Artisan** (Kavita Devi - Madhubani, Bihar) | Phone OTP | `7668439019` | `123456` |
| **Verified Buyer** (Aarav Sharma - Mumbai, Maharashtra) | Phone OTP | `7668439019` | `696969` |
| **Google Sign-In** | One-Tap | *Any active account* | Google Play Services |

---

### Release Verification & Build Artifacts

* **Release Package:** `app/build/outputs/apk/release/app-release.apk`
* **Version Name:** `0.2.0`
* **Version Code:** `2`
* **File Size:** `21,838,870 bytes` (~20.8 MB)
* **Signing Scheme:** Android APK Signature Scheme v2 (Verified: `true`)
* **Minimum SDK:** Android 7.0 (API 24)
* **Target SDK:** Android 15 (API 37)

```bash
adb install -r app-release.apk
```

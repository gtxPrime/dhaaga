# 🧵 Dhaaga (धागा • ShilpSetu) — v0.1.0 Prototype Release Notes

### *Connecting Hands to Markets — From Village Craft to Global Cart in Under 5 Minutes*

[![Release](https://img.shields.io/badge/Release-v0.1.0--prototype-blue?style=for-the-badge&logo=github)](https://github.com/gtxPrime/dhaaga/releases)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=android)](https://github.com/gtxPrime/dhaaga)
[![APK Size](https://img.shields.io/badge/APK%20Size-20.7%20MB-orange?style=for-the-badge&logo=googleplay)](https://github.com/gtxPrime/dhaaga)
[![SIH 2026](https://img.shields.io/badge/SIH-2026_Finalist-FF9933?style=for-the-badge&logo=target&logoColor=white)](https://sih.gov.in)

---

## 📋 Release Summary

| Attribute | Details |
|:---|:---|
| **Release Tag** | `v0.1.0-prototype` |
| **Release Title** | Dhaaga Prototype v0.1.0 — Multilingual Voice Commerce & AI Studio |
| **Build Number** | `1` |
| **Target Platforms** | Android 7.0 (API 24) to Android 16 (API 37) |
| **Application ID** | `com.dhaaga.app` |
| **Signed Keystore** | `alphaKey.jks` (`keyAlias: key0`) |
| **Signed APK** | `app/build/outputs/apk/release/app-release.apk` (`21,746,399 bytes` ~ `20.7 MB`) |
| **Git Commit** | `ad57f4d` |
| **Repository** | [https://github.com/gtxPrime/dhaaga](https://github.com/gtxPrime/dhaaga) |

---

## 🌟 Executive Overview

**Dhaaga (धागा • ShilpSetu)** is an AI-powered, multilingual, zero-barrier mobile commerce platform built for India’s **7 million+ traditional artisans, weavers, and craftspersons**, and domestic/international connoisseur buyers.

This prototype demonstrates a complete, end-to-end journey from rural craft creation to global discovery: an artisan can take a smartphone photo, speak a brief voice memo in their native language, enhance their product image using on-device ML segmentation, apply promotional discounts, and publish to a live cloud marketplace in **under 5 minutes**.

---

## 🚀 Key Features in this Release

### 1. 🗣️ Instant Pre-Warmed Voice Onboarding (`AppTtsManager`)
* **Zero Cold-Start Lag:** Pre-warms the native Android `TextToSpeech` engine on application startup (`MainActivity.onCreate`) so voice narration begins instantly upon screen mount.
* **Bilingual Guidance:** Delivers clear, conversational voice instructions in both **Hindi (हिंदी)** and **English** across all onboarding steps (Language Choice, Phone OTP, Role Selection, and Profile Setup).
* **Audio Controls:** Interactive sound-wave activity indicator, one-tap voice language toggle (`[हिंदी | English]`), and an audio mute/unmute switch.

### 2. 🌐 22 Scheduled Indian Languages (`AppLanguageManager`)
* Full multilingual dictionary supporting **all 22 Eighth-Schedule Indian languages** (Hindi, Bengali, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Odia, Punjabi, Assamese, etc.).
* Dynamic in-app locale switcher with reactive state flow that translates interface elements, craft categories, and product attributes on the fly.

### 3. 📸 AI Image Studio & Background Cutout
* **Google ML Kit Segmentation:** Integrated background cutout engine (`ImageSegmentationHelper`) that automatically isolates handmade crafts from cluttered workshop surroundings.
* **AI Framing Guide:** Visual guide assisting artisans in framing crafts with optimal lighting and aspect ratios.
* **Studio Filters & Lighting:** Backdrop presets (Pure Studio White, Warm Craft Texture, Modern Gallery, Lifestyle Stage) to generate professional e-commerce product shots.

### 4. 🧵 5-Step Artisan Listing Wizard (`AddProductScreen`)
1. **Photo & Studio Enhancer:** Camera capture, gallery picker, and ML background isolation.
2. **Smart Cataloging:** Bilingual craft titles, descriptions, and regional classification.
3. **Material & Authentic Specs:** Size, technique, region, and Geographical Indication (GI) registry validation.
4. **Fair-Trade Pricing & Promotions:** Direct sale prices with live buyer savings calculations + customized discount coupons.
5. **One-Tap Publish:** Immediate sync to the Home Screen marketplace, Seller Dashboard, and Cloud Firestore.

### 5. 🏷️ Robust Promotional Discounts & Coupons (`ProductDiscountCouponCard`)
* **Direct Sale Pricing:** Enables artisans to offer special sale prices with strike-through original prices and `-XX%` badges.
* **Coupons (% or ₹ Flat):** Supports percentage discounts or flat rupee price reductions.
* **Code Generator:** Generates branded coupon codes (e.g. `DHAAGA20`, `WARLI15`) or accepts custom codes.
* **Time Limits:** Configurable coupon duration ranging from **10 minutes (minimum)** to **3 months (maximum)**.
* **Usage Caps:** Restricts coupon redemptions (Unlimited, 10, 25, 50, 100 uses).

### 6. 🔍 Integrated Voice Search on Marketplace (`HomeScreen`)
* Native Android `SpeechRecognizer` launcher integrated directly into the Home search bar.
* Allows buyers and artisans to discover authentic regional crafts hands-free by speaking product titles, materials, or craft forms.

### 7. 🛍️ Connoisseur Buyer Experience & Storytelling
* **"Kahaani" Storytelling Engine:** Product detail pages render rich cultural narratives, tribal history, and artisan lineage alongside technical dimensions.
* **Available Offers & Coupons Card:** Interactive showcase with one-tap copy and live checkout validation.
* **Smart Cart & Escrow Checkout:** Multi-item cart management, live coupon deductions, delivery address selection, and simulated instant UPI/escrow payments.
* **Order History & Restocking:** Real-time order state progression (`Pending` ➔ `Confirmed` ➔ `Packed` ➔ `Shipped` ➔ `Delivered`) with one-tap cancellation that automatically restocks seller inventory.

### 8. ☁️ Bidirectional Cloud Sync & Data Persistence (`AppViewModel`)
* **Permanent Local Registry (`dhaaga_permanent_crafts`):** Dedicated local database decoupled from authentication session resets—craft listings and orders are never wiped upon logout.
* **Firestore Cloud Synchronization:** Automatic background sync (`syncLocalCraftsToCloud`) ensuring all crafts, inventory levels, and user profiles are stored in Firestore and synchronised across multiple devices.
* **Protected Serialization:** Data models (`ProductModel`, `UserModel`) annotated with `@IgnoreExtraProperties` and `@get:Exclude` to prevent cloud deserialization failures.

### 9. 👤 User Profile & Mobile Verification
* **Prominent Mobile Badging:** Displays the exact logged-in mobile number with a verified badge across both the bottom-navigation Profile Tab and the full Profile Screen.
* **Artisan Dashboard:** Real-time metrics tracking total sales, view counts, Shilpi Trust Score, and live craft statuses.

---

## 🔑 Demo Test Credentials

Use these pre-configured test credentials for instant evaluation without waiting for SMS gateways:

| Role | Mobile Number | OTP Code | Description |
|:---|:---:|:---:|:---|
| **🎨 Master Artisan** | `7668439019` | `123456` | Logs in as *Kavita Devi* (Madhubani Artisan, Bihar) with store management, craft listing, and dashboard access. |
| **🛍️ Craft Buyer** | `7668439019` | `696969` | Logs in as *Aarav Sharma* (Craft Connoisseur, Mumbai) with cart, wishlist, checkout, and order tracking. |

---

## 📦 Release Artifact & Installation

### Artifact Details:
* **File:** `app/build/outputs/apk/release/app-release.apk`
* **Size:** `20.7 MB` (`21,746,399 bytes`)
* **Signing Keystore:** `alphaKey.jks` (`key0`)
* **Build System:** Gradle 9.6.1 + AGP 8.9.0 + Kotlin 2.3.21

### ADB Installation Command:
```bash
adb install -r app/build/outputs/apk/release/app-release.apk
adb shell am start -n com.dhaaga.app/.MainActivity
```

---

## 🗺️ What’s Next (Roadmap towards v0.2.0)
- [ ] ONDC Beckn Protocol catalog export integration.
- [ ] Live Razorpay / PhonePe UPI payment gateway integration.
- [ ] Live Bhashini ASR pipeline for continuous conversational voice memo capture.
- [ ] Blockchain-backed authenticity certificates for GI-tagged crafts.

---

<div align="center">
  <sub>Built with ❤️ and Pride for India's Artisans • <b>Dhaaga © 2026</b></sub>
</div>

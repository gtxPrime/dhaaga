# Dhaaga (धागा • ShilpSetu) - Release Changelog

## Version 0.3.0 (Milestone 3 — High-Fidelity Cultural UI & Polish Edition)

> **Connecting Hands to Markets: Authentic Living Heritage from Indian Artisan Hubs**

---

### Key Highlights & Major Additions

#### 0. Multilingual AI Auto-Cataloger & Translation Engine Upgrade
* **Zero-Downtime Gemini Model Fleet:** Upgraded from deprecated/overloaded preview models to Google's high-availability production fleet (`gemini-flash-latest`, `gemini-3.1-flash-lite`, `gemini-3.6-flash`, `gemini-3.7-flash`).
* **Resolved 503 High Demand Error:** Direct API diagnosis revealed `gemini-3.6-flash` and older models were returning 503/404. Replaced the fallback chain with tested 200 OK models.
* **Thought Token Isolation:** Added `thinkingBudget: 0` to `generationConfig` and implemented `extractCandidateText` so thinking traces no longer collide with or corrupt JSON translation parsing.
* **Resilient Offline / Outage Fallback:** Implemented an intelligent local heuristic catalog builder with `GITagRegistry` keyword extraction, ensuring that artisans can always generate bilingual listings and GI mappings even during complete cloud disruptions.

#### 1. Complete UI Typography & Text-Wrapping Overhaul
* **No Awkward Line Wrapping:** Audited and resolved text wrapping across all screens. Enforced single-line boundaries with ellipsis on phone numbers, delivery addresses, and subtitle badges.
* **Profile Tab & Real Google Identity:** Replaced generic phone label with real **Google Account** details (`email`, profile photo, and Notion avatar). Refactored `ProfileOptionRow` with responsive weighted containers (`1.1f` on label, `0.9f` with `textAlign = TextAlign.End` on value).
* **Navigation Clearance:** Increased bottom scroll padding to `160.dp` across tab contents, ensuring buttons (such as "Log Out of Dhaaga") remain completely unobstructed above the floating bottom navigation bar.
* **Living Heritage Oral Archive:** Fixed language tab squeezing in `CraftHeritageDetailScreen.kt` where tabs were compressed into vertical character strips. Aligned "हिंदी" and "English" tabs horizontally with clean margins.
* **Hero Badges Alignment:** Replaced corner-pinned badges with a unified `SpaceBetween` row (`${craft.traditionAgeYears}+ Yrs Heritage` and `Process Video`), preventing badge collisions across diverse screen aspect ratios.

#### 2. Premium Cultural Terracotta & Charcoal Palette
* **Warm Artisan Theme:** Transitioned from dull green backgrounds to warm terracotta (`#C85A32`, `PaletteTerracotta`), deep charcoal, and clean slate surfaces.
* **Refined Tab Headers:** Upgraded `TabHeaderBlock` from a faded gradient to a warm horizontal terracotta brand gradient (`PaletteTerracotta` to `#C85A32`).
* **Lore Exact Floating Navigation Bar:** Centered elevated terracotta FAB with animated spring pop-effects, glow rings on cart additions, and crisp unclipped badge pill.
* **Payment Sheet UI:** Styled UPI selection pills (GPay, PhonePe, Paytm, BHIM) with single-line labels, clean borders, and terracotta active indicators.

#### 3. High-Resolution Craft Visual Assets & Offline Fallback Engine
* **Bundled High-Fidelity Craft Banners:** Generated and integrated bespoke local drawable assets:
  * `banner_warli_art.jpg` (Palghar, Maharashtra — 2,500-Year Sacred Geometric Lineage)
  * `banner_madhubani_art.jpg` (Mithila, Bihar — Kohbar & Natural Mineral Dye Frieze)
  * `banner_pashmina_loom.jpg` (Srinagar, Kashmir — Changthangi Cashmere Loom)
  * `banner_dhokra_metal.jpg` (Bastar, Chhattisgarh — 4,000-Year Lost-Wax Bell Metal)
  * `banner_blue_pottery.jpg` (Kot Jewar, Rajasthan — Clayless Quartz & Cobalt Glaze)
* **Resilient Image Interceptor:** Added model interceptor in `CardAsyncImage.kt` mapping stock photo URLs directly to bundled high-res craft drawables for instant, reliable offline rendering.

#### 4. Balanced 2x4 Product Catalog Grid
* **Uniform Card Dimensions:** Enforced strict `minLines = 2, maxLines = 2` on titles, `minLines = 1, maxLines = 1` on artisan regions, and fixed 34.dp action button heights across `ProductCardCreative`.
* **Balanced Inventory:** Added Handcrafted Ceramic Peacock Vase (`prod008`) to `MockData.kt` ensuring an even 8-item product grid with zero orphan cards.

#### 5. Docked Bottom Action Bar & Specification Alignments
* **Docked Product Detail Bar:** Converted the floating bottom bar in `ProductDetailScreen.kt` to a docked `Surface` with elevation and bottom navigation bar insets.
* **Oral History Audio Pill:** Updated audio player subtitle to `Marathi & Hindi • 1m 15s` with a compact `Listen` / `Playing...` pill.
* **Specifications Table:** Right-aligned all craft specifications (`textAlign = TextAlign.End`) for luxury e-commerce clarity.

---

### Release Verification & Build Artifacts

* **Release Package:** `app/build/outputs/apk/release/app-release.apk`
* **Version Name:** `0.2.0`
* **Version Code:** `2`
* **File Size:** `27,445,462 bytes` (~27.4 MB)
* **Signing Config:** Release Keystore (`alphaKey.jks`, alias `key0`)
* **Minimum SDK:** Android 7.0 (API 24)
* **Target SDK:** Android 15 (API 37)

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

---

## Version 0.2.0 (Milestone 2)

> **Connecting Hands to Markets: From Village Craft to Global Cart**

### Key Highlights
* **GI Tag Authentication & Verification Engine:** Government registry cross-referencing with multimodal AI validation.
* **Interactive Mock Payment Gateway:** UPI, Cards, NetBanking, and COD simulation with real-time Cloud Firestore updates.
* **Native GPS Geolocation Engine:** Native Android `LocationManager` and Geocoder resolving artisan clusters and delivery destinations.
* **Enterprise Material Icons:** Complete emoji purge in favor of `Icons.AutoMirrored` and Material vector sets.
* **Architectural Blueprint Documentation:** Generated master SIH 2026 technical report and engineering delegation matrix.

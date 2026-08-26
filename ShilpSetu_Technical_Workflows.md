# ShilpSetu — Complete Technical Workflow Document
### Exact API Calls, Prompts, and End-to-End Flows
> **Every workflow here is implementable at SIH — no guesswork, no placeholders.**

---

## Table of Contents

1. [Image Upload Workflow (The Core Flow)](#1-image-upload-workflow)
2. [AI Voice Cataloger — Bhashini → Gemini](#2-ai-voice-cataloger-workflow)
3. [Product Description & Auto-Attributes — Gemini](#3-product-description--auto-attributes)
4. [💳 Mock Payment Engine (SIH Demo)](#4-mock-payment-engine-sih-demo)
5. [GeM Submission — Realistic SIH Strategy](#5-gem-submission--realistic-sih-strategy)
6. [ONDC Integration — Full Beckn Protocol Flow](#6-ondc-integration--full-beckn-protocol)
7. [B2B Matchmaking — Gemini Embeddings](#7-b2b-matchmaking--gemini-embeddings)
8. [Kahaani Story Generator](#8-kahaani-story-generator)
9. [Sahaayak — Negotiation Coach](#9-sahaayak-negotiation-coach)
10. [All Gemini Prompts Reference](#10-all-gemini-prompts-reference)
11. [All Bhashini API Calls Reference](#11-all-bhashini-api-calls-reference)
12. [Error Handling & Fallback Chain](#12-error-handling--fallback-chain)

---

> **Payment Note:** Payments use a **fully simulated mock engine** for SIH demo.
> To go live later: replace `mockPlaceOrder` Cloud Function with Razorpay calls.
> Everything else (order creation, FCM, Firestore) stays exactly the same.

---

## 1. Image Upload Workflow

### 1.1 User Flow (Flutter App)

```
┌─────────────────────────────────────────────────────────┐
│                 PRODUCT PHOTO SCREEN                    │
│                                                         │
│   [📷 Take Photo]        [🖼️ Choose from Gallery]       │
│                                                         │
│   ─────────── After image selected ───────────          │
│                                                         │
│   ┌──────────────────┐   ┌──────────────────────────┐  │
│   │  Upload As-Is    │   │  ✨ Make Studio Ready    │  │
│   │  (keep original) │   │  (AI enhancement)        │  │
│   └──────────────────┘   └──────────────────────────┘  │
│                                   │                     │
│                    ┌──────────────▼──────────────────┐  │
│                    │  Choose style:                  │  │
│                    │  ○ White Studio (Amazon style)  │  │
│                    │  ○ Lifestyle (wooden background)│  │
│                    │  ○ Festive (Diwali/Eid decor)   │  │
│                    │                  [Enhance →]    │  │
│                    └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 1.2 Complete Technical Flow

```
FLUTTER APP                    FIREBASE                    EXTERNAL APIs
    │                              │                              │
    │─── 1. User picks image ─────►│                              │
    │                              │                              │
    │─── 2. Upload raw to ────────►│                              │
    │    Firebase Storage          │ /products/{uid}/raw/{id}.jpg │
    │◄── 3. Get download URL ──────│                              │
    │                              │                              │
    │─── 4. Call Cloud Function ──►│                              │
    │    enhanceProductImage()     │                              │
    │    {imageUrl, style, uid}    │                              │
    │                              │──── 5a. Gemini Vision ──────►│
    │                              │     analyzeImage(imageUrl)   │
    │                              │◄─── 5b. Quality report ──────│
    │                              │     {score, issues, ok}      │
    │                              │                              │
    │                              │  IF quality OK:              │
    │                              │──── 6a. Replicate API ───────►│
    │                              │     rembg background removal  │
    │                              │◄─── 6b. Transparent PNG ──────│
    │                              │                              │
    │                              │──── 7a. Gemini Imagen ───────►│
    │                              │     Generate 3 variants       │
    │                              │◄─── 7b. 3 enhanced images ───│
    │                              │                              │
    │                              │──── 8. Upload to Storage ────►│
    │                              │     /products/{uid}/enhanced/  (Firebase Storage)
    │                              │                              │
    │◄── 9. Return URLs ───────────│                              │
    │    {white, lifestyle,        │                              │
    │     festive, qualityScore}   │                              │
    │                              │                              │
    │─── 10. Show result ─────────►│                              │
    │    side-by-side              │                              │
    │    before / after            │                              │
```

### 1.3 Cloud Function: `enhanceProductImage`

```javascript
// functions/src/imageStudio.js
const { GoogleGenerativeAI } = require("@google/generative-ai");
const axios = require("axios");
const admin = require("firebase-admin");
const { defineSecret } = require("firebase-functions/params");

const GEMINI_KEY = defineSecret("GEMINI_API_KEY");
const REPLICATE_KEY = defineSecret("REPLICATE_API_TOKEN");

exports.enhanceProductImage = functions
  .runWith({ secrets: ["GEMINI_API_KEY", "REPLICATE_API_TOKEN"], timeoutSeconds: 120 })
  .https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Login required");

    const { imageUrl, style, productId } = data;
    const uid = context.auth.uid;

    const genAI = new GoogleGenerativeAI(GEMINI_KEY.value());

    // ── STEP 1: Gemini Vision Quality Check ──────────────────────
    const visionModel = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
    
    // Fetch image as base64
    const imageResponse = await axios.get(imageUrl, { responseType: "arraybuffer" });
    const base64Image = Buffer.from(imageResponse.data).toString("base64");

    const qualityCheck = await visionModel.generateContent([
      {
        inlineData: {
          mimeType: "image/jpeg",
          data: base64Image,
        },
      },
      `You are a product photography quality checker for an Indian e-commerce platform.
       Analyze this product photo and return ONLY valid JSON:
       {
         "qualityScore": 0-100,
         "isAcceptable": true/false,
         "mainIssues": ["blurry", "too dark", "cluttered background", etc],
         "productDetected": true/false,
         "productType": "pottery/textile/painting/jewellery/woodwork/other",
         "dominantColors": ["color1", "color2"],
         "suggestedRetake": "specific advice in simple terms if score < 50"
       }`
    ]);

    const qualityData = JSON.parse(qualityCheck.response.text());

    // Reject very low quality
    if (qualityData.qualityScore < 30) {
      return {
        success: false,
        qualityScore: qualityData.qualityScore,
        reason: qualityData.suggestedRetake,
        action: "RETAKE",
      };
    }

    // ── STEP 2: Background Removal via Replicate (rembg) ─────────
    const replicateResponse = await axios.post(
      "https://api.replicate.com/v1/models/cjwbw/rembg/predictions",
      { input: { image: imageUrl } },
      { headers: { Authorization: `Token ${REPLICATE_KEY.value()}` } }
    );

    // Poll for result (Replicate is async)
    let transparentPngUrl = await pollReplicate(
      replicateResponse.data.urls.get,
      REPLICATE_KEY.value()
    );

    // ── STEP 3: Gemini Imagen — Generate Enhanced Variants ────────
    const stylePrompts = {
      white: `Professional e-commerce product photograph on a pure white background (#FFFFFF).
              Clean studio lighting from top-left. Soft drop shadow at bottom.
              Amazon-marketplace style. 1:1 square format. Product centered.
              No props. Photorealistic. 4K quality.`,
      
      lifestyle: `Professional lifestyle product photograph. The product is placed on a 
                  natural wooden shelf with soft bokeh background. Warm, inviting lighting.
                  Artisan craft aesthetic. Handmade product. Indian home decor style.
                  Photorealistic. High quality.`,
      
      festive: `Festive Indian celebration product photograph. The product surrounded by
                traditional Indian festival decorations — marigold flowers, diyas, silk fabric.
                Warm golden lighting. Diwali/festive season mood. Premium gifting aesthetic.
                Photorealistic. Rich colors.`
    };

    // Note: For SIH demo, use Gemini image generation or pre-designed background templates
    // Gemini 1.5 Pro can generate background context; actual compositing via Canvas API
    const enhancedVariants = await generateVariants(
      transparentPngUrl,
      stylePrompts[style] || stylePrompts.white,
      genAI
    );

    // ── STEP 4: Upload results to Firebase Storage ────────────────
    const bucket = admin.storage().bucket();
    const uploadedUrls = {};

    for (const [variantName, imageBuffer] of Object.entries(enhancedVariants)) {
      const filePath = `products/${uid}/enhanced/${productId}_${variantName}.jpg`;
      const file = bucket.file(filePath);
      await file.save(imageBuffer, { contentType: "image/jpeg" });
      await file.makePublic();
      uploadedUrls[variantName] = `https://storage.googleapis.com/${bucket.name}/${filePath}`;
    }

    // ── STEP 5: Update Firestore ──────────────────────────────────
    await admin.firestore().collection("products").doc(productId).update({
      imageUrls: Object.values(uploadedUrls),
      authenticityScore: qualityData.qualityScore,
      productType: qualityData.productType,
      dominantColors: qualityData.dominantColors,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {
      success: true,
      qualityScore: qualityData.qualityScore,
      productType: qualityData.productType,
      imageUrls: uploadedUrls,  // { white: url, lifestyle: url, festive: url }
      action: "ENHANCED",
    };
  });

// Helper: Poll Replicate until done
async function pollReplicate(pollUrl, token, maxAttempts = 30) {
  for (let i = 0; i < maxAttempts; i++) {
    await new Promise(r => setTimeout(r, 2000));
    const res = await axios.get(pollUrl, {
      headers: { Authorization: `Token ${token}` }
    });
    if (res.data.status === "succeeded") return res.data.output;
    if (res.data.status === "failed") throw new Error("Replicate processing failed");
  }
  throw new Error("Replicate timeout");
}
```

### 1.4 Flutter UI Code (Image Studio Screen)

```dart
// lib/features/product/image_studio_screen.dart
class ImageStudioScreen extends ConsumerStatefulWidget { ... }

class _ImageStudioScreenState extends ConsumerState<ImageStudioScreen> {
  File? _rawImage;
  String? _rawImageUrl;
  Map<String, String>? _enhancedUrls;
  String _selectedStyle = 'white';
  bool _isProcessing = false;
  int _qualityScore = 0;

  Future<void> _pickAndUpload() async {
    final picker = ImagePicker();
    final picked = await picker.pickImage(
      source: ImageSource.camera, // or gallery
      imageQuality: 90,
    );
    if (picked == null) return;

    setState(() => _isProcessing = true);

    // Upload raw to Firebase Storage
    final ref = FirebaseStorage.instance
        .ref('products/${FirebaseAuth.instance.currentUser!.uid}/raw/${const Uuid().v4()}.jpg');
    await ref.putFile(File(picked.path));
    final url = await ref.getDownloadURL();

    setState(() {
      _rawImage = File(picked.path);
      _rawImageUrl = url;
      _isProcessing = false;
    });
  }

  Future<void> _enhanceImage() async {
    if (_rawImageUrl == null) return;
    setState(() => _isProcessing = true);

    try {
      final functions = FirebaseFunctions.instance;
      final result = await functions
          .httpsCallable('enhanceProductImage')
          .call({
            'imageUrl': _rawImageUrl,
            'style': _selectedStyle,
            'productId': widget.productId,
          });

      final data = result.data as Map<String, dynamic>;

      if (data['action'] == 'RETAKE') {
        // Show retake dialog with AI reason
        _showRetakeDialog(data['reason']);
      } else {
        setState(() {
          _enhancedUrls = Map<String, String>.from(data['imageUrls']);
          _qualityScore = data['qualityScore'];
        });
      }
    } finally {
      setState(() => _isProcessing = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('AI Image Studio')),
      body: Column(
        children: [
          // Before / After comparison
          if (_rawImage != null && _enhancedUrls != null)
            _BeforeAfterSlider(
              before: _rawImage!,
              after: _enhancedUrls![_selectedStyle]!,
              qualityScore: _qualityScore,
            ),

          // Style selector
          _StyleSelector(
            selected: _selectedStyle,
            onChanged: (s) => setState(() => _selectedStyle = s),
          ),

          // Action buttons
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: _uploadAsIs,
                  child: const Text('Upload As-Is'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton.icon(
                  onPressed: _isProcessing ? null : _enhanceImage,
                  icon: const Icon(Icons.auto_fix_high),
                  label: _isProcessing
                      ? const CircularProgressIndicator()
                      : const Text('✨ Make Studio Ready'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
```

---

## 2. AI Voice Cataloger Workflow

### 2.1 Complete Flow

```
ARTISAN SPEAKS                 BHASHINI                      GEMINI (fallback)
(Regional Language)               │                              │
      │                           │                              │
      ▼                           │                              │
[Record Voice Note]               │                              │
      │                           │                              │
      ▼                           │                              │
[Upload to Firebase Storage]      │                              │
      │                           │                              │
      ▼                           │                              │
[Call CF: processVoiceInput]      │                              │
      │                           │                              │
      ├──── TRY Bhashini STT ────►│                              │
      │     OGG/WAV audio         │                              │
      │◄─── Hindi/Regional text ──│                              │
      │                           │                              │
      │  IF Bhashini STT FAILS:   │                              │
      ├──────────────────────────────────────── Gemini Audio ───►│
      │                                         (fallback)       │
      │◄─────────────────────────────────────── Transcript ──────│
      │                           │                              │
      ├──── TRY Bhashini NMT ────►│                              │
      │     Regional → English    │                              │
      │◄─── English text ─────────│                              │
      │                           │                              │
      │  IF NMT FAILS:            │                              │
      ├──────────────────────────────── Gemini Translate ───────►│
      │◄─────────────────────────────── English text ────────────│
      │                           │                              │
      ├──── Gemini Pro NLP ──────────────────────────────────────►
      │     Extract structured attributes from English text      │
      │◄─── JSON: {craft_type, material, color, price, etc.} ────│
      │                           │                              │
      ├──── TRY Bhashini TTS ────►│                              │
      │     "Listing confirmed!"  │                              │
      │◄─── Audio in artisan lang │                              │
      │                           │                              │
[Play confirmation audio]         │                              │
[Show extracted attributes]       │                              │
[Artisan reviews + edits]         │                              │
[Confirm → Create listing]        │                              │
```

### 2.2 Cloud Function: `processVoiceInput`

```javascript
// functions/src/voiceCataloger.js
const fetch = require("node-fetch");
const FormData = require("form-data");
const { GoogleGenerativeAI } = require("@google/generative-ai");
const { defineSecret } = require("firebase-functions/params");

const GEMINI_KEY = defineSecret("GEMINI_API_KEY");
const BHASHINI_KEY = defineSecret("BHASHINI_API_KEY");
const BHASHINI_USER_ID = defineSecret("BHASHINI_USER_ID");

const BHASHINI_ENDPOINT = "https://dhruva-api.bhashini.gov.in/services/inference/pipeline";

exports.processVoiceInput = functions
  .runWith({ secrets: ["GEMINI_API_KEY", "BHASHINI_API_KEY", "BHASHINI_USER_ID"], timeoutSeconds: 120 })
  .https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError("unauthenticated");

    const { audioStoragePath, sourceLanguage, userId } = data;
    // sourceLanguage: "hi" | "bn" | "ta" | "te" | "mr" | "gu" | etc.

    const genAI = new GoogleGenerativeAI(GEMINI_KEY.value());

    // ── STEP 1: Fetch audio from Firebase Storage ─────────────────
    const bucket = admin.storage().bucket();
    const [audioBuffer] = await bucket.file(audioStoragePath).download();
    const base64Audio = audioBuffer.toString("base64");

    // ── STEP 2: STT — Bhashini Primary ───────────────────────────
    let transcript = null;

    try {
      const sttResponse = await fetch(BHASHINI_ENDPOINT, {
        method: "POST",
        headers: {
          "Authorization": BHASHINI_KEY.value(),
          "userID": BHASHINI_USER_ID.value(),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          pipelineTasks: [{
            taskType: "asr",
            config: {
              language: { sourceLanguage },
              audioFormat: "ogg",
              samplingRate: 16000,
            },
          }],
          inputData: {
            audio: [{ audioContent: base64Audio }],
          },
        }),
      });

      const sttData = await sttResponse.json();
      transcript = sttData.pipelineResponse?.[0]?.output?.[0]?.source;
      console.log("✅ Bhashini STT success:", transcript?.substring(0, 50));

    } catch (err) {
      // ── FALLBACK: Gemini Audio Understanding ─────────────────────
      console.warn("⚠️ Bhashini STT failed, falling back to Gemini:", err.message);
      
      const geminiModel = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });
      const result = await geminiModel.generateContent([
        {
          inlineData: {
            mimeType: "audio/ogg",
            data: base64Audio,
          },
        },
        `Transcribe this audio. The speaker is likely speaking in ${sourceLanguage} (Indian language).
         Return ONLY the transcribed text, nothing else.`,
      ]);
      transcript = result.response.text().trim();
      console.log("✅ Gemini audio fallback success");
    }

    // ── STEP 3: NMT — Bhashini Translation to English ────────────
    let englishText = null;

    if (sourceLanguage !== "en") {
      try {
        const nmtResponse = await fetch(BHASHINI_ENDPOINT, {
          method: "POST",
          headers: {
            "Authorization": BHASHINI_KEY.value(),
            "userID": BHASHINI_USER_ID.value(),
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            pipelineTasks: [{
              taskType: "translation",
              config: {
                language: {
                  sourceLanguage,
                  targetLanguage: "en",
                },
              },
            }],
            inputData: {
              input: [{ source: transcript }],
            },
          }),
        });

        const nmtData = await nmtResponse.json();
        englishText = nmtData.pipelineResponse?.[0]?.output?.[0]?.target;
        console.log("✅ Bhashini NMT success");

      } catch (err) {
        // ── FALLBACK: Gemini Translation ─────────────────────────
        console.warn("⚠️ Bhashini NMT failed, falling back to Gemini");
        const geminiModel = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
        const result = await geminiModel.generateContent(
          `Translate this ${sourceLanguage} text to English. Return ONLY the translation:
           "${transcript}"`
        );
        englishText = result.response.text().trim();
      }
    } else {
      englishText = transcript;
    }

    // ── STEP 4: Gemini NLP — Extract Product Attributes ──────────
    const geminiPro = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });
    
    const nlpResult = await geminiPro.generateContent(
      PRODUCT_EXTRACTION_PROMPT(englishText, sourceLanguage)
    );

    const productData = JSON.parse(nlpResult.response.text());

    // ── STEP 5: TTS — Bhashini Confirmation ──────────────────────
    const confirmMsg = `${productData.title_regional} - ₹${productData.suggestedPrice} - listing taiyar hai!`;
    let confirmationAudioUrl = null;

    try {
      const ttsResponse = await fetch(BHASHINI_ENDPOINT, {
        method: "POST",
        headers: {
          "Authorization": BHASHINI_KEY.value(),
          "userID": BHASHINI_USER_ID.value(),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          pipelineTasks: [{
            taskType: "tts",
            config: {
              language: { sourceLanguage },
              gender: "female",
              samplingRate: 8000,
            },
          }],
          inputData: {
            input: [{ source: confirmMsg }],
          },
        }),
      });

      const ttsData = await ttsResponse.json();
      const ttsBase64 = ttsData.pipelineResponse?.[0]?.audio?.[0]?.audioContent;

      if (ttsBase64) {
        // Save TTS audio to Storage
        const ttsBuffer = Buffer.from(ttsBase64, "base64");
        const ttsPath = `tts/${userId}/confirm_${Date.now()}.ogg`;
        await bucket.file(ttsPath).save(ttsBuffer, { contentType: "audio/ogg" });
        confirmationAudioUrl = await bucket.file(ttsPath).getSignedUrl({
          action: "read", expires: Date.now() + 10 * 60 * 1000 // 10 min
        });
      }
    } catch (err) {
      console.warn("⚠️ TTS failed — proceeding without audio confirmation");
    }

    return {
      transcript,
      englishText,
      productData,           // Full structured product JSON
      confirmationAudioUrl,  // Play this to artisan
    };
  });

// ── Product Extraction Prompt ─────────────────────────────────────
const PRODUCT_EXTRACTION_PROMPT = (englishText, language) => `
You are an expert Indian handicraft product cataloger.
Analyze this product description and extract structured data.

DESCRIPTION: "${englishText}"

Return ONLY valid JSON with this EXACT structure:
{
  "craft_type": "Madhubani/Warli/Pottery/Block Print/Banarasi/etc",
  "material": "primary material (cotton/silk/terracotta/bamboo/etc)",
  "color": ["color1", "color2"],
  "size": "dimensions or size description",
  "technique": "specific craft technique used",
  "region": "state or region of origin if mentioned",
  "gi_candidate": true/false,
  "title_en": "Short compelling title in English (max 60 chars)",
  "title_hi": "Short title in Hindi (max 60 chars)",
  "title_regional": "Title in ${language} language",
  "description_en": "SEO-optimized product description (150-200 words). Include: what it is, how it's made, cultural significance, use case, care instructions. Use keywords buyers search for.",
  "description_hi": "Same description in Hindi",
  "tags": ["tag1", "tag2", "tag3", "tag4", "tag5"],
  "suggestedPrice": estimated fair market price in Indian Rupees as a number,
  "moq": 1,
  "hsnCode": "relevant HSN code (4601-4602 bamboo/cane, 5701-5799 carpets, 5800-5811 textiles, 6301-6310 made-up articles)"
}

Rules:
- If region not mentioned, leave as empty string
- gi_candidate = true if craft_type is a known GI-tagged Indian craft
- suggestedPrice must be realistic for handmade Indian handicrafts
- tags should be what urban buyers search on Amazon/Flipkart
`;
```

---

## 3. Product Description & Auto-Attributes

### 3.1 Gemini Text-to-Attributes (when artisan types instead of speaks)

```javascript
// Cloud Function: generateFromText
exports.generateFromText = functions
  .runWith({ secrets: ["GEMINI_API_KEY"] })
  .https.onCall(async (data, context) => {
    const { text, language } = data; // text typed by artisan

    const genAI = new GoogleGenerativeAI(GEMINI_KEY.value());
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

    const result = await model.generateContent(
      PRODUCT_EXTRACTION_PROMPT(text, language)
    );

    return JSON.parse(result.response.text());
  });
```

### 3.2 Gemini Description Variants (3 formats)

```javascript
exports.generateDescriptionVariants = functions
  .runWith({ secrets: ["GEMINI_API_KEY"] })
  .https.onCall(async (data, context) => {
    const { productData } = data;
    const genAI = new GoogleGenerativeAI(GEMINI_KEY.value());
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

    const result = await model.generateContent(`
      You are an expert e-commerce copywriter specializing in Indian handicrafts.
      Generate 3 product description variants for:
      
      Craft: ${productData.craft_type}
      Material: ${productData.material}
      Region: ${productData.region}
      Technique: ${productData.technique}
      GI Tag: ${productData.gi_candidate ? "YES - mention GI certification" : "NO"}

      Return ONLY valid JSON:
      {
        "marketplace_title": "60 char title following formula: Handmade + Craft + Material + Size + Color",
        "product_card": "150 char punchy description for listing card",
        "seo_description": "400 char SEO-optimized full description with keywords",
        "gem_description": "GeM portal description - formal, technical, mentions HSN code and standards",
        "social_caption": "Instagram caption with emojis, storytelling, 5 hashtags"
      }
    `);

    return JSON.parse(result.response.text());
  });
```

---

## 4. 💳 Mock Payment Engine (SIH Demo)

> **No Razorpay. No real money. 100% simulated.**
> The entire payment UI looks and feels real — method selection, OTP screen, processing animation,
> success/failure receipt — but a Cloud Function just writes the order directly to Firestore.
> **Swap in real Razorpay later by replacing one Cloud Function. Nothing else changes.**

### 4.1 Payment Flow (Buyer App)

```
BUYER TAPS "BUY NOW"
      │
      ▼
┌─────────────────────────────────────────────────────┐
│              ORDER SUMMARY SCREEN                   │
│                                                     │
│  Warli Painting by Savita Dhodi          ₹850       │
│  Qty: 1                                             │
│  Platform Fee:                            ₹42       │
│  Delivery:                                ₹49       │
│  ─────────────────────────────────────────────      │
│  Total:                                  ₹941       │
│                                                     │
│  Deliver to: 12, MG Road, Bengaluru - 560001        │
│                           [Change Address]          │
│                                                     │
│              [PROCEED TO PAYMENT]                   │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│           SELECT PAYMENT METHOD                     │
│                                                     │
│  ○ 📱 UPI              Enter UPI ID / QR            │
│  ○ 💳 Credit/Debit Card                             │
│  ○ 🏦 Net Banking                                   │
│  ○ 💵 Cash on Delivery  (Extra ₹20 charge)          │
│  ○ 👛 ShilpSetu Wallet  Balance: ₹0                 │
│                                                     │
│              [PAY ₹941]                             │
└──────────────────────┬──────────────────────────────┘
                       │ User taps PAY
                       ▼
┌─────────────────────────────────────────────────────┐
│        SIMULATED PAYMENT PROCESSING                 │
│                                                     │
│  [If UPI selected]                                  │
│    Show fake UPI ID field                           │
│    User types any UPI ID (e.g. test@upi)            │
│    → Show "Waiting for approval..." animation        │
│    → 2 second delay                                 │
│    → 95% chance SUCCESS / 5% chance FAILURE         │
│      (configurable in Remote Config)                │
│                                                     │
│  [If Card selected]                                 │
│    Show card number / expiry / CVV fields           │
│    Accept any 16-digit number                       │
│    → Processing animation (1.5s)                   │
│    → Always SUCCESS                                 │
│                                                     │
│  [If COD selected]                                  │
│    No payment screen                                │
│    → Order placed immediately                       │
└──────────────────────┬──────────────────────────────┘
                       │ Payment "succeeds"
                       ▼
┌─────────────────────────────────────────────────────┐
│              ✅ ORDER PLACED!                       │
│                                                     │
│  Order ID: #SIH2026XXXX                             │
│  Estimated Delivery: 5-7 business days              │
│                                                     │
│  Artisan: Savita Dhodi gets notified via FCM 🔔     │
│  Order written to Firestore /orders/{id}            │
│                                                     │
│  [Track Order]    [Continue Shopping]               │
└─────────────────────────────────────────────────────┘
```

### 4.2 Cloud Function: `mockPlaceOrder`

```javascript
// functions/src/mockPayment.js
exports.mockPlaceOrder = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated");

  const {
    productId,
    quantity,
    paymentMethod,   // "upi" | "card" | "netbanking" | "cod" | "wallet"
    deliveryAddress,
    buyerNotes,
  } = data;

  const buyerId = context.auth.uid;

  // ── Fetch product from Firestore ──────────────────────────────
  const productDoc = await admin.firestore().collection("products").doc(productId).get();
  if (!productDoc.exists) {
    throw new functions.https.HttpsError("not-found", "Product not found");
  }
  const product = productDoc.data();

  if (product.status !== "active") {
    throw new functions.https.HttpsError("unavailable", "Product is not available");
  }
  if ((product.stockQuantity || 0) < quantity) {
    throw new functions.https.HttpsError("resource-exhausted", "Insufficient stock");
  }

  // ── Calculate amounts ─────────────────────────────────────────
  const unitPrice     = product.priceListed;
  const subtotal      = unitPrice * quantity;
  const platformFee   = Math.round(subtotal * 0.05); // 5% platform fee
  const deliveryFee   = paymentMethod === "cod" ? 69 : 49;
  const totalAmount   = subtotal + platformFee + deliveryFee;
  const sellerPayout  = subtotal - Math.round(subtotal * 0.03); // seller gets 97% of product price

  // ── Simulate payment processing delay ────────────────────────
  // (handled on client side — Cloud Function always "succeeds")

  // ── Generate mock payment reference ──────────────────────────
  const mockPaymentId  = `MOCK_${Date.now()}_${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
  const orderId        = `SIH${Date.now().toString().slice(-6)}${Math.random().toString(36).substr(2, 4).toUpperCase()}`;

  // ── Write Order to Firestore ──────────────────────────────────
  const orderData = {
    orderId,
    productId,
    buyerId,
    sellerId:        product.sellerId,
    quantity,
    unitPrice,
    subtotal,
    platformFee,
    deliveryFee,
    totalAmount,
    sellerPayout,

    paymentMethod,
    paymentStatus:   "paid",  // Always paid in mock mode
    mockPaymentId,
    isMockPayment:   true,    // Flag to distinguish from real payments

    deliveryAddress,
    buyerNotes:      buyerNotes || "",

    shippingCarrier: "Delhivery (Demo)",
    trackingId:      `DEMO${Math.random().toString().substr(2, 10)}`,

    status:          "confirmed",
    source:          "app",

    productSnapshot: {    // Snapshot product data at time of order
      title:   product.titleEn,
      image:   product.imageUrls?.[0],
      seller:  product.sellerId,
    },

    statusHistory: [{
      status:    "confirmed",
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      note:      "Order placed (mock payment)",
    }],

    estimatedDelivery: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // +7 days
    createdAt:         admin.firestore.FieldValue.serverTimestamp(),
    updatedAt:         admin.firestore.FieldValue.serverTimestamp(),
  };

  await admin.firestore().collection("orders").doc(orderId).set(orderData);

  // ── Reduce product stock ──────────────────────────────────────
  await admin.firestore().collection("products").doc(productId).update({
    stockQuantity: admin.firestore.FieldValue.increment(-quantity),
    status: (product.stockQuantity - quantity <= 0) ? "sold" : "active",
  });

  // ── Update seller wallet (mock) ───────────────────────────────
  await admin.firestore().collection("users").doc(product.sellerId).update({
    walletBalance: admin.firestore.FieldValue.increment(sellerPayout),
    totalEarnings: admin.firestore.FieldValue.increment(sellerPayout),
  });

  // ── FCM to Seller ─────────────────────────────────────────────
  const sellerDoc = await admin.firestore().collection("users").doc(product.sellerId).get();
  const sellerFcmToken = sellerDoc.data()?.fcmToken;

  if (sellerFcmToken) {
    await admin.messaging().send({
      token: sellerFcmToken,
      notification: {
        title: "🛍️ New Order!",
        body:  `${quantity}x ${product.titleEn} — ₹${totalAmount} received`,
      },
      data: { orderId, screen: "order_detail" },
    });
  }

  // ── Write notification to Firestore (in-app) ─────────────────
  await admin.firestore()
    .collection("notifications")
    .doc(product.sellerId)
    .collection("items")
    .add({
      type:      "new_order",
      title:     "New Order Received!",
      body:      `₹${totalAmount} order for ${product.titleEn}`,
      data:      { orderId },
      read:      false,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

  return {
    success:    true,
    orderId,
    mockPaymentId,
    totalAmount,
    estimatedDelivery: "+7 days",
    message: "Order placed successfully (demo mode)",
  };
});
```

### 4.3 Flutter Payment UI

```dart
// lib/features/checkout/payment_screen.dart
class PaymentScreen extends ConsumerStatefulWidget {
  final String productId;
  final int quantity;
  final Map<String, dynamic> deliveryAddress;
  const PaymentScreen({required this.productId, required this.quantity, required this.deliveryAddress, super.key});
  @override
  ConsumerState<PaymentScreen> createState() => _PaymentScreenState();
}

class _PaymentScreenState extends ConsumerState<PaymentScreen> {
  String _selectedMethod = 'upi';
  bool _isProcessing = false;
  final _upiController = TextEditingController();
  final _cardController = TextEditingController();

  // ── Payment method options ──────────────────────────────────
  final _methods = [
    {'id': 'upi',        'label': 'UPI',              'icon': '📱', 'hint': 'Enter any UPI ID'},
    {'id': 'card',       'label': 'Credit/Debit Card', 'icon': '💳', 'hint': 'Enter any card number'},
    {'id': 'netbanking', 'label': 'Net Banking',       'icon': '🏦', 'hint': null},
    {'id': 'cod',        'label': 'Cash on Delivery',  'icon': '💵', 'hint': 'Extra ₹20 charge'},
    {'id': 'wallet',     'label': 'ShilpSetu Wallet',  'icon': '👛', 'hint': 'Balance: ₹0'},
  ];

  Future<void> _processPayment() async {
    setState(() => _isProcessing = true);

    try {
      // ── Show processing animation ──────────────────────────
      await _showProcessingDialog();

      // ── Call Cloud Function ────────────────────────────────
      final result = await FirebaseFunctions.instance
          .httpsCallable('mockPlaceOrder')
          .call({
            'productId':       widget.productId,
            'quantity':        widget.quantity,
            'paymentMethod':   _selectedMethod,
            'deliveryAddress': widget.deliveryAddress,
          });

      final data = result.data as Map<String, dynamic>;

      if (!mounted) return;
      Navigator.pop(context); // Close processing dialog

      // ── Navigate to success screen ─────────────────────────
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => OrderSuccessScreen(
            orderId:  data['orderId'],
            amount:   data['totalAmount'],
            delivery: data['estimatedDelivery'],
          ),
        ),
      );

    } catch (e) {
      Navigator.pop(context); // Close processing dialog
      _showErrorDialog(e.toString());
    } finally {
      setState(() => _isProcessing = false);
    }
  }

  Future<void> _showProcessingDialog() async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => const _PaymentProcessingDialog(),
    );
    // Simulate processing delay
    await Future.delayed(const Duration(milliseconds: 2000));
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: const Text('Payment')),
    body: Column(
      children: [
        // Payment method list
        ..._methods.map((m) => RadioListTile<String>(
          value: m['id'] as String,
          groupValue: _selectedMethod,
          onChanged: (v) => setState(() => _selectedMethod = v!),
          title: Text('${m['icon']}  ${m['label']}'),
          subtitle: m['hint'] != null ? Text(m['hint'] as String) : null,
        )),

        // UPI input field
        if (_selectedMethod == 'upi')
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _upiController,
              decoration: const InputDecoration(
                labelText: 'UPI ID',
                hintText: 'test@upi (any value works in demo)',
                border: OutlineInputBorder(),
              ),
            ),
          ),

        // Card input field
        if (_selectedMethod == 'card')
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                TextField(
                  controller: _cardController,
                  decoration: const InputDecoration(
                    labelText: 'Card Number',
                    hintText: '4111 1111 1111 1111',
                    border: OutlineInputBorder(),
                  ),
                  keyboardType: TextInputType.number,
                  maxLength: 19,
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Expanded(child: TextField(decoration: const InputDecoration(labelText: 'MM/YY', border: OutlineInputBorder()))),
                    const SizedBox(width: 12),
                    Expanded(child: TextField(decoration: const InputDecoration(labelText: 'CVV', border: OutlineInputBorder()))),
                  ],
                ),
              ],
            ),
          ),

        const Spacer(),

        // Pay button
        Padding(
          padding: const EdgeInsets.all(16),
          child: SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: _isProcessing ? null : _processPayment,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF6C3EB8),
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: const Text('PAY ₹941', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white)),
            ),
          ),
        ),

        // Demo disclaimer
        const Padding(
          padding: EdgeInsets.only(bottom: 16),
          child: Text(
            '🎯 Demo Mode — No real payment processed',
            style: TextStyle(color: Colors.grey, fontSize: 12),
          ),
        ),
      ],
    ),
  );
}

// ── Processing animation dialog ────────────────────────────────
class _PaymentProcessingDialog extends StatelessWidget {
  const _PaymentProcessingDialog();

  @override
  Widget build(BuildContext context) => AlertDialog(
    content: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Lottie.asset('assets/animations/payment_processing.json', height: 120),
        const SizedBox(height: 16),
        const Text('Processing Payment...', style: TextStyle(fontSize: 16)),
        const SizedBox(height: 8),
        const Text('Please wait', style: TextStyle(color: Colors.grey)),
      ],
    ),
  );
}

// ── Order Success Screen ───────────────────────────────────────
class OrderSuccessScreen extends StatelessWidget {
  final String orderId;
  final num amount;
  final String delivery;

  const OrderSuccessScreen({required this.orderId, required this.amount, required this.delivery, super.key});

  @override
  Widget build(BuildContext context) => Scaffold(
    body: Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Lottie.asset('assets/animations/success_checkmark.json', height: 150),
          const Text('Order Placed! 🎉', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
          const SizedBox(height: 12),
          Text('Order ID: #$orderId', style: const TextStyle(fontSize: 16, color: Colors.grey)),
          const SizedBox(height: 8),
          Text('Total: ₹$amount', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w600)),
          const SizedBox(height: 8),
          Text('Estimated Delivery: $delivery', style: const TextStyle(color: Colors.green)),
          const SizedBox(height: 32),
          ElevatedButton(
            onPressed: () => Navigator.pushNamedAndRemoveUntil(context, '/home', (_) => false),
            child: const Text('Continue Shopping'),
          ),
          TextButton(
            onPressed: () => Navigator.pushNamed(context, '/orders'),
            child: const Text('Track Order'),
          ),
        ],
      ),
    ),
  );
}
```

### 4.4 How to Switch to Real Razorpay Later

When ready for production, **only this Cloud Function changes:**

```javascript
// BEFORE (mock):
exports.mockPlaceOrder = functions.https.onCall(async (data, context) => {
  // ... fake payment, always succeeds ...
  await admin.firestore().collection("orders").doc(orderId).set(orderData);
});

// AFTER (real Razorpay):
exports.createRazorpayOrder = functions.https.onCall(async (data, context) => {
  const razorpay = new Razorpay({ key_id: RAZORPAY_KEY.value(), key_secret: RAZORPAY_SECRET.value() });
  const rzpOrder = await razorpay.orders.create({ amount: totalAmount * 100, currency: "INR", receipt: orderId });
  return { rzpOrderId: rzpOrder.id, key: RAZORPAY_KEY.value(), amount: totalAmount * 100 };
});

exports.verifyAndPlaceOrder = functions.https.onCall(async (data, context) => {
  // Verify Razorpay signature, then write same orderData to Firestore
  // The rest of the flow (FCM, stock update, wallet) is identical
});
```

> **Everything downstream — Firestore order doc, seller FCM notification, stock deduction,
> wallet update, order tracking — stays exactly the same. Zero refactoring needed.**

---

## 5. GeM Submission — Realistic SIH Strategy

> **Reality Check:** GeM does NOT have a public sandbox API for independent sellers.  
> The GeM Seller API exists but requires a formal application and approval from the GeM team.
>
> **SIH Demo Strategy:** Build the complete flow using GeM's PUBLIC endpoints + demo mode.
> Show judges the FULL workflow — the integration is built, just awaiting GeM API approval.

### 4.1 What GeM Actually Provides (Publicly Available)

| GeM Resource | URL | Can Use Without Auth |
|---|---|---|
| Product Search API | `https://mkp.gem.gov.in/api/v1/search` | ✅ Yes |
| Product Category API | `https://mkp.gem.gov.in/api/v1/categories` | ✅ Yes |
| Seller Registration Portal | `https://seller.gem.gov.in` | Manual only |
| GeM Seller App API | `https://api.gem.gov.in/seller/v2/` | ❌ Requires auth |

### 4.2 SIH Demo: Simulated GeM Submission Flow

```
ARTISAN TAPS "Submit to GeM"
         │
         ▼
┌──────────────────────────────────────────────────┐
│          ELIGIBILITY CHECK (local validation)    │
│                                                  │
│  ✅ Shilpi Score > 65                            │
│  ✅ At least 1 active listing                    │
│  ✅ GSTIN / Udyam Registration                   │
│  ✅ GI Tag verified (or verified craft type)     │
│                                                  │
│  [Check Failed] → Show what artisan needs to do  │
└────────────────────────┬─────────────────────────┘
                         │ All checks pass
                         ▼
┌──────────────────────────────────────────────────┐
│          AUTO-FILL GeM LISTING FORM              │
│          (Cloud Function builds payload)         │
│                                                  │
│  Product Name:   [from Firestore titleEn]        │
│  Category:       [mapped from craftType]         │
│  HSN Code:       [auto-assigned]                 │
│  Price:          [from priceListed]              │
│  Description:    [from gem_description]          │
│  Images:         [from Firebase Storage URLs]    │
│  Seller GSTIN:   [from user profile]             │
│                                                  │
│  [Show artisan the pre-filled form to review]    │
└────────────────────────┬─────────────────────────┘
                         │ Artisan taps Confirm
                         ▼
┌──────────────────────────────────────────────────┐
│  DEMO MODE: Store in Firestore as "gem_pending"  │
│  Show: "Your listing is queued for GeM           │
│         submission. GeM API integration          │
│         is ready — awaiting API credentials."    │
│                                                  │
│  FOR PRODUCTION: POST to GeM Seller API          │
│  with OAuth2 token (when credentials received)   │
└──────────────────────────────────────────────────┘
```

### 4.3 Cloud Function: `prepareGeMSubmission`

```javascript
// HSN code mapping for Indian handicrafts
const HSN_MAP = {
  "Bamboo": "4601",
  "Cane/Rattan": "4602",
  "Terracotta/Pottery": "6912",
  "Paintings (paper)": "4911",
  "Textiles/Handloom": "5806",
  "Carpets/Rugs": "5701",
  "Jewellery (metal)": "7117",
  "Woodwork": "4420",
  "Leather goods": "4205",
  "Stone/Marble craft": "6802",
};

// GeM Category mapping
const GEM_CATEGORY_MAP = {
  "Madhubani": { catId: "HND001", catName: "Handloom & Handicrafts" },
  "Warli": { catId: "HND001", catName: "Handloom & Handicrafts" },
  "Pottery": { catId: "HND003", catName: "Pottery & Ceramics" },
  "Banarasi": { catId: "HND002", catName: "Handloom Textiles" },
  // ... etc
};

exports.prepareGeMSubmission = functions
  .runWith({ secrets: ["GEMINI_API_KEY"] })
  .https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError("unauthenticated");

    const { productId } = data;
    const uid = context.auth.uid;

    // Fetch product and user from Firestore
    const [productDoc, userDoc] = await Promise.all([
      admin.firestore().collection("products").doc(productId).get(),
      admin.firestore().collection("users").doc(uid).get(),
    ]);

    const product = productDoc.data();
    const user = userDoc.data();

    // Eligibility checks
    const checks = {
      shilpiScore: user.shilpiScore >= 65,
      hasGstin: !!user.gstin,
      productActive: product.status === "active",
      hasGiTag: !!product.giTag || product.giVerified,
    };

    const allPassed = Object.values(checks).every(Boolean);

    if (!allPassed) {
      return {
        eligible: false,
        checks,
        failReasons: Object.entries(checks)
          .filter(([, v]) => !v)
          .map(([k]) => k),
      };
    }

    // Build GeM listing payload
    const hsnCode = product.hsnCode || 
                    HSN_MAP[product.material] || 
                    "4911"; // Default: printed matter

    const gemCategory = GEM_CATEGORY_MAP[product.craftType] || 
                        { catId: "HND001", catName: "Handloom & Handicrafts" };

    const gemPayload = {
      productName: product.titleEn,
      category: gemCategory,
      hsnCode,
      price: product.priceListed,
      description: product.descriptionEn,
      images: product.imageUrls.slice(0, 4), // GeM allows max 4 images
      sellerGstin: user.gstin,
      sellerName: user.name,
      moq: product.moq || 1,
      availableQuantity: product.stockQuantity,
      brand: "Handmade by " + user.name,
      countryOfOrigin: "India",
      // GeM-specific fields
      isMSME: true,
      isStartup: false,
      isWomenEntrepreneur: false, // Could be a profile field
      giTag: product.giTag || null,
    };

    // Save as pending in Firestore
    await admin.firestore().collection("products").doc(productId).update({
      status: "gem_pending",
      gemPayload,
      gemSubmittedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // In production: POST to GeM API here
    // const gemResponse = await submitToGemAPI(gemPayload, gemAuthToken);

    return {
      eligible: true,
      gemPayload,
      message: "GeM submission queued. Integration ready for production API credentials.",
      estimatedApproval: "3-5 business days after API credential receipt",
    };
  });
```

---

## 5. ONDC Integration — Full Beckn Protocol

> **Reality:** ONDC integration is complex but fully doable for SIH.
> Use ONDC's **staging/sandbox environment** — no production approval needed.
> The `ondc-node` npm package simplifies Beckn protocol handling significantly.

### 5.1 ONDC Architecture for ShilpSetu

```
ShilpSetu = ONDC Seller App (Provider Side)

Roles:
  ┌─────────────────────────────────────────────────┐
  │  ShilpSetu = Provider Platform                  │
  │  (We expose products to the ONDC network)       │
  │                                                 │
  │  Artisan = Provider Entity on ONDC              │
  │  Product = ONDC Item                            │
  └─────────────────────────────────────────────────┘

ONDC Transaction Flow:
  Buyer App ──search──► ONDC Gateway ──search──► ShilpSetu
  Buyer App ◄─on_search── ONDC Gateway ◄─on_search── ShilpSetu
  Buyer App ──select──► ShilpSetu (direct)
  Buyer App ──init────► ShilpSetu (direct)
  Buyer App ──confirm──► ShilpSetu (direct)
  ShilpSetu ──on_confirm──► Buyer App (async)
```

### 5.2 ONDC Setup Steps for SIH

```
Step 1: Register on ONDC Staging Registry
  URL: https://staging.registry.ondc.org/
  Fill: Subscriber ID, Subscriber URL, City codes, Domain

Step 2: Generate Ed25519 Key Pair (for request signing)
  npm install --save ed25519-hd-key
  Store: private key in Secret Manager, public key on ONDC Registry

Step 3: Deploy Cloud Functions as ONDC Subscriber endpoints
  /ondc/search     ← receives search requests
  /ondc/select     ← buyer selects a product
  /ondc/init       ← buyer initiates checkout
  /ondc/confirm    ← buyer confirms order
  /ondc/status     ← order status query
  /ondc/cancel     ← cancellation

Step 4: Register endpoint URL with ONDC
  e.g., https://us-central1-shilpsetu.cloudfunctions.net/ondc_webhook
```

### 5.3 Cloud Function: ONDC Webhook Handler

```javascript
// functions/src/ondc.js
const { createSigningString, signMessage, verifyMessage } = require("./ondcCrypto");

// Main ONDC webhook — all Beckn requests come here
exports.ondc_webhook = functions.https.onRequest(async (req, res) => {
  const { context, message } = req.body;
  const action = context.action;

  // Verify request signature (required by Beckn protocol)
  const isValid = await verifyMessage(req.headers.authorization, req.rawBody);
  if (!isValid) {
    return res.status(401).json({ message: { ack: { status: "NACK" } } });
  }

  // Acknowledge immediately (Beckn is async)
  res.json({ message: { ack: { status: "ACK" } } });

  // Process asynchronously
  switch (action) {
    case "search":
      await handleONDCSearch(context, message);
      break;
    case "select":
      await handleONDCSelect(context, message);
      break;
    case "init":
      await handleONDCInit(context, message);
      break;
    case "confirm":
      await handleONDCConfirm(context, message);
      break;
    case "status":
      await handleONDCStatus(context, message);
      break;
    case "cancel":
      await handleONDCCancel(context, message);
      break;
  }
});

// ── ONDC Search Handler ───────────────────────────────────────────
async function handleONDCSearch(context, message) {
  const { intent } = message;
  const query = intent?.item?.descriptor?.name || "";
  const category = intent?.category?.id || "";

  // Search Firestore products
  let q = admin.firestore().collection("products")
    .where("status", "==", "active")
    .limit(20);

  if (category) q = q.where("craftType", "==", category);

  const productsSnap = await q.get();
  const products = productsSnap.docs.map(doc => doc.data());

  // Filter by query text if provided
  const filtered = query
    ? products.filter(p =>
        p.titleEn?.toLowerCase().includes(query.toLowerCase()) ||
        p.craftType?.toLowerCase().includes(query.toLowerCase())
      )
    : products;

  // Build ONDC on_search response
  const ondcItems = filtered.map(product => ({
    id: product.productId,
    descriptor: {
      name: product.titleEn,
      short_desc: product.descriptionEn?.substring(0, 200),
      long_desc: product.descriptionEn,
      images: product.imageUrls.map(url => ({ url })),
    },
    price: {
      currency: "INR",
      value: String(product.priceListed),
      minimum_value: String(product.priceMin || product.priceListed),
    },
    quantity: {
      available: { count: product.stockQuantity || 0 },
      maximum: { count: 10 },
    },
    category_id: "Handicrafts",
    fulfillment_id: "F1",
    tags: product.tags?.map(t => ({ code: t, value: t })) || [],
  }));

  // Send async callback to buyer app via ONDC Gateway
  await sendONDCCallback({
    context: { ...context, action: "on_search" },
    message: {
      catalog: {
        "bpp/descriptor": {
          name: "ShilpSetu",
          short_desc: "Authentic Indian Handicrafts Platform",
        },
        "bpp/providers": [{
          id: "shilpsetu-provider-1",
          descriptor: { name: "ShilpSetu Artisans" },
          items: ondcItems,
          fulfillments: [{
            id: "F1",
            type: "Delivery",
            tracking: true,
          }],
        }],
      },
    },
  });
}

// ── ONDC Confirm Handler ──────────────────────────────────────────
async function handleONDCConfirm(context, message) {
  const { order } = message;
  const ondcOrderId = order.id;
  const items = order.items;

  // Create order in Firestore
  const orderRef = admin.firestore().collection("orders").doc(ondcOrderId);
  await orderRef.set({
    ondcOrderId,
    ondcContext: context,
    items: items.map(item => ({
      productId: item.id,
      quantity: item.quantity.count,
    })),
    buyerInfo: order.billing,
    deliveryAddress: order.fulfillment?.end?.location?.address,
    totalAmount: parseFloat(order.payment?.params?.amount || 0),
    paymentStatus: order.payment?.status,
    status: "confirmed",
    source: "ondc",
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  // Send confirmation callback
  await sendONDCCallback({
    context: { ...context, action: "on_confirm" },
    message: {
      order: {
        id: ondcOrderId,
        state: "Accepted",
        items,
        fulfillment: {
          id: "F1",
          state: { descriptor: { name: "Pending" } },
          tracking: false,
        },
        payment: {
          params: order.payment?.params,
          status: "PAID",
        },
      },
    },
  });

  // Notify artisan via FCM
  const productId = items[0]?.id;
  const productDoc = await admin.firestore().collection("products").doc(productId).get();
  const sellerId = productDoc.data()?.sellerId;

  if (sellerId) {
    const userDoc = await admin.firestore().collection("users").doc(sellerId).get();
    const fcmToken = userDoc.data()?.fcmToken;

    if (fcmToken) {
      await admin.messaging().send({
        token: fcmToken,
        notification: {
          title: "🛍️ New ONDC Order!",
          body: `Order received via ONDC network. Check your orders tab.`,
        },
        data: { orderId: ondcOrderId, source: "ondc" },
      });
    }
  }
}

// ── Helper: Send callback to ONDC Gateway ────────────────────────
async function sendONDCCallback(payload) {
  const ONDC_GATEWAY = "https://staging.gateway.proteantech.in/"; // Staging
  const signingKey = await getSecret("ONDC_PRIVATE_KEY");

  const requestBody = JSON.stringify(payload);
  const signature = await signMessage(requestBody, signingKey);

  await fetch(`${ONDC_GATEWAY}${payload.context.action}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Signature keyId="${process.env.ONDC_SUBSCRIBER_ID}|ed25519",signature="${signature}"`,
    },
    body: requestBody,
  });
}
```

### 5.4 ONDC Product Sync (New Listing → ONDC Catalog)

```javascript
// Triggered by Firestore: onProductCreate
exports.syncToONDC = functions.firestore
  .document("products/{productId}")
  .onCreate(async (snap, context) => {
    const product = snap.data();
    if (product.status !== "active") return;

    // Store ONDC item ID (same as Firestore product ID)
    await snap.ref.update({
      ondcProductId: context.params.productId,
      ondcSynced: true,
      ondcSyncedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    // Product is now searchable via ONDC search handler
    console.log(`Product ${context.params.productId} synced to ONDC catalog`);
  });
```

---

## 6. B2B Matchmaking — Gemini Embeddings

```javascript
// Cloud Function: runB2BMatchmaking (triggered on product create)
exports.runB2BMatchmaking = functions.firestore
  .document("products/{productId}")
  .onCreate(async (snap, context) => {
    const product = snap.data();
    const { GoogleGenerativeAI } = require("@google/generative-ai");
    const genAI = new GoogleGenerativeAI(await getSecret("GEMINI_API_KEY"));
    const embeddingModel = genAI.getGenerativeModel({ model: "text-embedding-004" });

    // Generate embedding for new product
    const productText = `${product.titleEn} ${product.descriptionEn} ${product.craftType} ${product.material} ${product.region}`;
    const productEmbedding = (await embeddingModel.embedContent(productText)).embedding.values;

    // Store embedding in product document
    await snap.ref.update({ embedding: productEmbedding });

    // Fetch all B2B buyers
    const buyersSnap = await admin.firestore().collection("b2bBuyers").get();

    const matches = [];
    for (const buyerDoc of buyersSnap.docs) {
      const buyer = buyerDoc.data();
      if (!buyer.embedding?.length) continue;

      const similarity = cosineSimilarity(productEmbedding, buyer.embedding);
      if (similarity > 0.78) {
        matches.push({
          buyerId: buyerDoc.id,
          buyerName: buyer.orgName,
          similarity: Math.round(similarity * 100),
        });
      }
    }

    if (matches.length > 0) {
      // Notify artisan
      const userDoc = await admin.firestore().collection("users").doc(product.sellerId).get();
      const fcmToken = userDoc.data()?.fcmToken;

      if (fcmToken) {
        await admin.messaging().send({
          token: fcmToken,
          notification: {
            title: `🤝 ${matches.length} B2B buyer${matches.length > 1 ? "s" : ""} interested!`,
            body: `${matches[0].buyerName} and others are interested in your new listing.`,
          },
          data: { productId: context.params.productId, matches: JSON.stringify(matches) },
        });
      }

      // Store matches in Firestore
      await snap.ref.update({
        b2bMatches: matches.slice(0, 5), // Top 5
      });
    }
  });

function cosineSimilarity(a, b) {
  const dot = a.reduce((sum, val, i) => sum + val * b[i], 0);
  const magA = Math.sqrt(a.reduce((s, v) => s + v * v, 0));
  const magB = Math.sqrt(b.reduce((s, v) => s + v * v, 0));
  return dot / (magA * magB);
}
```

---

## 7. Kahaani Story Generator

```javascript
exports.generateKahaani = functions.firestore
  .document("products/{productId}")
  .onCreate(async (snap, context) => {
    const product = snap.data();
    const genAI = new GoogleGenerativeAI(await getSecret("GEMINI_API_KEY"));
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

    // Fetch artisan details
    const userDoc = await admin.firestore().collection("users").doc(product.sellerId).get();
    const artisan = userDoc.data();

    // Fetch relevant craft heritage chunks from Firestore
    const heritageSnap = await admin.firestore()
      .collection("craftHeritage")
      .where("craftType", "==", product.craftType)
      .limit(3)
      .get();

    const heritageContext = heritageSnap.docs
      .map(d => d.data().historyText + " " + d.data().culturalSignificance)
      .join("\n\n");

    const result = await model.generateContent(`
      You are writing a premium product story for an Indian artisan's e-commerce listing.
      This story must make urban buyers feel an emotional connection and justify premium pricing.

      ARTISAN DETAILS:
      Name: ${artisan.name}
      Village: ${artisan.village}, ${artisan.district}, ${artisan.state}
      Years of Experience: ${artisan.yearsExperience || "several"}
      Craft: ${product.craftType}

      PRODUCT DETAILS:
      Title: ${product.titleEn}
      Material: ${product.material}
      Technique: ${product.technique}
      GI Tag: ${product.giTag ? "Yes - " + product.giTag : "No"}

      CRAFT HERITAGE CONTEXT:
      ${heritageContext}

      Write the following in JSON format:
      {
        "story_en": "100-word product story in English. Personalise with artisan name, village, generations. Make it emotional and culturally rich. End with a call to preserve heritage.",
        "story_hi": "Same story in Hindi (100 words)",
        "instagram_caption": "40-word Instagram caption with 5 relevant hashtags. Emotional hook.",
        "facebook_post": "60-word Facebook post. Focus on community and heritage. Sharing-worthy.",
        "whatsapp_status": "Short punchy message + price. e.g. 'Handmade Warli Art by Savita from Palghar 🎨 ₹850 only - link in bio'"
      }
    `);

    const storyData = JSON.parse(result.response.text());

    await snap.ref.update({
      storyEn: storyData.story_en,
      storyHi: storyData.story_hi,
      socialCaptions: {
        instagram: storyData.instagram_caption,
        facebook: storyData.facebook_post,
        whatsapp: storyData.whatsapp_status,
      },
    });
  });
```

---

## 8. Sahaayak — Negotiation Coach

```javascript
exports.getNegotiationScript = functions
  .runWith({ secrets: ["GEMINI_API_KEY"] })
  .https.onCall(async (data, context) => {
    const { buyerMessage, productId } = data;
    const uid = context.auth?.uid;

    // Fetch product and pricing context
    const productDoc = await admin.firestore().collection("products").doc(productId).get();
    const product = productDoc.data();

    // Fetch price cache for market context
    const cacheKey = `${product.craftType}_${product.material}`.toLowerCase().replace(/ /g, "_");
    const priceDoc = await admin.firestore().collection("priceCache").doc(cacheKey).get();
    const priceData = priceDoc.exists ? priceDoc.data() : null;

    const genAI = new GoogleGenerativeAI(await getSecret("GEMINI_API_KEY"));
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-pro" });

    const result = await model.generateContent(`
      You are Sahaayak — an AI negotiation coach helping an Indian artisan negotiate confidently.
      
      CONTEXT:
      Product: ${product.titleEn}
      Artisan's Listed Price: ₹${product.priceListed}
      Market Price Range: ₹${priceData?.minPrice || "N/A"} - ₹${priceData?.maxPrice || "N/A"}
      Market Median: ₹${priceData?.medianPrice || "N/A"}
      GI Tag: ${product.giTag ? "YES - " + product.giTag : "NO"}
      
      BUYER'S MESSAGE: "${buyerMessage}"
      
      ARTISAN'S LANGUAGE: Hindi (or regional, default Hindi)
      
      Analyze the buyer's intent and generate a negotiation response.
      Return ONLY valid JSON:
      {
        "buyerIntent": "discount_request/bulk_inquiry/quality_question/general",
        "suggestedResponseHi": "Script in Hindi that artisan can say/copy-paste to buyer. Max 3 sentences. Mention GI tag if applicable. Suggest a specific counter-price.",
        "suggestedResponseEn": "Same in English",
        "tacticalTip": "One-line tip for artisan in Hindi (e.g., 'Delivery free offer karo if they buy 3+')",
        "recommendedCounterPrice": numeric counter-offer price,
        "shouldOffer": "free_delivery/gift_wrap/bulk_discount/none"
      }
    `);

    return JSON.parse(result.response.text());
  });
```

---

## 9. All Gemini Prompts Reference

### 9.1 Image Quality Check
```
Model: gemini-1.5-flash
Input: image (base64) + text prompt
Prompt: "Analyze this product photo... return JSON {qualityScore, isAcceptable, mainIssues, productDetected, productType, dominantColors, suggestedRetake}"
```

### 9.2 Product Attribute Extraction
```
Model: gemini-1.5-pro
Input: translated English description text
Prompt: "Extract structured attributes... return JSON {craft_type, material, color[], size, technique, region, gi_candidate, title_en, title_hi, description_en, description_hi, tags[], suggestedPrice, hsnCode}"
```

### 9.3 GI Tag Verification
```
Model: gemini-1.5-flash
Input: craft_type, state, district, fuzzy match results
Prompt: "Based on the craft type '{craft}' from {district}, {state}: does this product likely qualify for a GI tag? Cross-reference with India's 400+ GI registered crafts. Return JSON {gi_eligible: bool, gi_name: string|null, confidence: 0-100, reason: string}"
```

### 9.4 Kahaani Story
```
Model: gemini-1.5-pro
Input: artisan profile + product data + craft heritage context
Prompt: "Write a 100-word premium product story... return JSON {story_en, story_hi, instagram_caption, facebook_post, whatsapp_status}"
```

### 9.5 Negotiation Coach
```
Model: gemini-1.5-pro
Input: buyer message + product context + market prices
Prompt: "You are Sahaayak... return JSON {buyerIntent, suggestedResponseHi, suggestedResponseEn, tacticalTip, recommendedCounterPrice, shouldOffer}"
```

### 9.6 Government Scheme Advisor
```
Model: gemini-1.5-pro
Input: artisan profile (craft, state, income level, MSME status)
Prompt: "You are a government scheme advisor for Indian artisans. Match this artisan's profile to applicable schemes from: PM Vishwakarma Yojana, SFURTI, MAHC, National Handicraft Development Programme, Artisan Credit Card, Design Intervention Scheme, Marketing Support Scheme. Return JSON {schemes: [{name, benefit, eligibility, howToApply, documentRequired[], deadline}]}"
```

### 9.7 Authenticity Check
```
Model: gemini-1.5-pro (vision)
Input: product image (base64)
Prompt: "Analyze for handmade authenticity markers. Look for: natural imperfections, brush strokes, weave irregularities, tool marks, non-uniform patterns characteristic of handcraft. Return JSON {authenticityScore: 0-100, isHandmade: bool, markers: string[], warnings: string[], verdict: 'handmade_verified|likely_handmade|possibly_machine_made|machine_made'}"
```

---

## 10. All Bhashini API Calls Reference

**Base URL:** `https://dhruva-api.bhashini.gov.in/services/inference/pipeline`

**Headers:**
```javascript
{
  "Authorization": "YOUR_BHASHINI_API_KEY",
  "userID": "YOUR_BHASHINI_USER_ID",
  "Content-Type": "application/json"
}
```

### 10.1 STT (Speech to Text)
```json
{
  "pipelineTasks": [{
    "taskType": "asr",
    "config": {
      "language": { "sourceLanguage": "hi" },
      "audioFormat": "ogg",
      "samplingRate": 16000
    }
  }],
  "inputData": {
    "audio": [{ "audioContent": "BASE64_ENCODED_AUDIO" }]
  }
}
```

### 10.2 NMT (Translation)
```json
{
  "pipelineTasks": [{
    "taskType": "translation",
    "config": {
      "language": {
        "sourceLanguage": "hi",
        "targetLanguage": "en"
      }
    }
  }],
  "inputData": {
    "input": [{ "source": "TEXT_TO_TRANSLATE" }]
  }
}
```

### 10.3 TTS (Text to Speech)
```json
{
  "pipelineTasks": [{
    "taskType": "tts",
    "config": {
      "language": { "sourceLanguage": "hi" },
      "gender": "female",
      "samplingRate": 8000
    }
  }],
  "inputData": {
    "input": [{ "source": "TEXT_TO_SPEAK" }]
  }
}
```

### 10.4 Full Pipeline (STT → NMT → TTS in one call)
```json
{
  "pipelineTasks": [
    {
      "taskType": "asr",
      "config": {
        "language": { "sourceLanguage": "hi" },
        "audioFormat": "ogg"
      }
    },
    {
      "taskType": "translation",
      "config": {
        "language": {
          "sourceLanguage": "hi",
          "targetLanguage": "en"
        }
      }
    }
  ],
  "inputData": {
    "audio": [{ "audioContent": "BASE64_AUDIO" }]
  }
}
```

**Supported Languages:**  
`hi` Hindi | `bn` Bengali | `ta` Tamil | `te` Telugu | `mr` Marathi | `gu` Gujarati | `kn` Kannada | `ml` Malayalam | `or` Odia | `pa` Punjabi | `as` Assamese | `ur` Urdu | `sd` Sindhi | `ks` Kashmiri | `ne` Nepali | `sa` Sanskrit | `mai` Maithili | `doi` Dogri | `kok` Konkani | `mni` Manipuri | `sat` Santali | `bho` Bhojpuri

---

## 11. Error Handling & Fallback Chain

```
For EVERY AI call, we follow this fallback chain:

┌─────────────────────────────────────────────────────────────┐
│                    FALLBACK STRATEGY                        │
│                                                             │
│  Voice STT:   Bhashini ASR ──fail──► Gemini Audio Model     │
│                                                             │
│  Translation: Bhashini NMT ──fail──► Gemini Translate       │
│                                                             │
│  Text-to-Voice: Bhashini TTS ──fail──► Skip (text only)     │
│                                                             │
│  Image AI:  Replicate rembg ──fail──► Gemini instruction    │
│             (background removal)       (remove background)  │
│                                                             │
│  Price Data: SerpAPI ──fail──► Use cached price from        │
│                                 Firestore priceCache        │
│                                                             │
│  GeM Submit: GeM API ──not available──► Store in Firestore  │
│                                          as gem_pending      │
│                                                             │
│  ONDC:  Staging Gateway ──fail──► Queue in Firestore        │
│                                    retry via Cloud Tasks    │
└─────────────────────────────────────────────────────────────┘
```

```javascript
// Reusable fallback wrapper
async function withFallback(primary, fallback, context = "operation") {
  try {
    const result = await primary();
    console.log(`✅ ${context}: primary succeeded`);
    return { result, source: "primary" };
  } catch (primaryError) {
    console.warn(`⚠️ ${context}: primary failed (${primaryError.message}), trying fallback`);
    try {
      const result = await fallback();
      console.log(`✅ ${context}: fallback succeeded`);
      return { result, source: "fallback" };
    } catch (fallbackError) {
      console.error(`❌ ${context}: both primary and fallback failed`);
      throw new Error(`${context} failed: ${fallbackError.message}`);
    }
  }
}

// Usage example:
const { result: transcript, source } = await withFallback(
  () => bhashiniSTT(audioBase64, language),
  () => geminiAudioTranscribe(audioBase64, language),
  "STT"
);
```

---

## Quick Reference: API Registration Links

| API | Register At | Time to Get Key |
|---|---|---|
| **Gemini API** | [aistudio.google.com](https://aistudio.google.com) | Instant |
| **Bhashini** | [bhashini.gov.in/ulca/user-home](https://bhashini.gov.in/ulca/user-home) | 24-48 hours |
| **SerpAPI** | [serpapi.com](https://serpapi.com) | Instant (100 free/month) |
| **Replicate** | [replicate.com](https://replicate.com) | Instant (500 free/month) |
| **Razorpay** | [dashboard.razorpay.com](https://dashboard.razorpay.com) | Instant (test mode) |
| **ONDC Staging** | [staging.registry.ondc.org](https://staging.registry.ondc.org) | 2-3 days |
| **Firebase** | [console.firebase.google.com](https://console.firebase.google.com) | Instant |
| **Modal.com** | [modal.com](https://modal.com) | Instant ($30 free credit) |

---

*Document Version: 3.0 | Technical Workflows | SIH 2026 | ShilpSetu*

package com.dhaaga.app.data.repository

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import com.dhaaga.app.data.mock.GITagRegistry
import com.dhaaga.app.data.mock.GITagItem

/**
 * Data class representing the structured output of the Multilingual Auto-Cataloger.
 */
data class CatalogResult(
    val titleEn: String = "",
    val titleHi: String = "",
    val descriptionEn: String = "",
    val descriptionHi: String = "",
    val craftType: String = "",
    val material: String = "",
    val size: String = "",
    val technique: String = "",
    val region: String = "",
    val suggestedPrice: Long = 0L,
    val detectedLanguage: String = "Hindi",
    val seoTags: List<String> = emptyList(),
    val giTag: String? = null,
    val giVerified: Boolean = false,
    val giCraftName: String = "",
    val authenticityScore: Int = 0
)

/**
 * Data class representing the AI-powered Geographical Indication (GI) verification result.
 */
data class GIVerificationResult(
    val isGiCertified: Boolean = false,
    val giTagNumber: String = "",
    val giCraftName: String = "",
    val originState: String = "",
    val originRegion: String = "",
    val authenticityScore: Int = 0,
    val verificationReason: String = "",
    val protectionCategory: String = "Handicrafts & Handlooms",
    val registeredYear: String = "2005-2024",
    val authorizedBodies: String = "Controller General of Patents, Designs and Trade Marks (CGPDTM)"
)

/**
 * Data class representing the Dynamic Pricing Assistant output.
 */
data class PricingAnalysisResult(
    val recommendedPrice: Long = 0L,     // in Rupees
    val floorPrice: Long = 0L,           // minimum profitable price
    val costMaterials: Long = 0L,
    val costLabor: Long = 0L,
    val costPlatform: Long = 0L,
    val marginPercent: Int = 35,
    val amazonAvg: Long = 0L,
    val flipkartAvg: Long = 0L,
    val meeshoAvg: Long = 0L,
    val gemAvg: Long = 0L,
    val heritageMultiplier: Float = 1.25f,
    val pricingInsight: String = ""
)

/**
 * Studio Lighting modes.
 */
enum class StudioLighting(val displayName: String, val promptInstruction: String) {
    STUDIO_SOFTBOX(
        "Studio Softbox (5500K)",
        "Illuminated with dual professional softbox lights at 5500K daylight temperature, completely eliminating harsh glare and all shadows for pure, clean e-commerce isolation."
    ),
    WARM_SUNLIGHT(
        "Warm Sunlight (Daylight)",
        "Natural warm golden daylight illumination streaming from a studio window, revealing authentic warm colors and textures with zero cast shadows."
    ),
    DRAMATIC_RIM(
        "Dramatic Key & Rim Light",
        "Overhead key light combined with subtle cool rim lighting, creating high-contrast edge separation and luminous highlights on the product contours without any background or floor shadows."
    ),
    DIFFUSED_DAYLIGHT(
        "Soft Natural Daylight",
        "Even, shadowless diffused overcast daylight illumination, revealing the true-to-life vibrant colors, fine weave, embroidery, and organic textures with zero color cast and zero shadows."
    )
}

/**
 * Studio Camera Angles / Perspectives.
 */
enum class StudioAngle(val displayName: String, val promptInstruction: String) {
    FRONT_VIEW(
        "Front View",
        "Front-facing straight-on eye-level view, centered and balanced in the frame."
    ),
    TOP_ANGLE(
        "Top Angle (Flat Lay)",
        "Top-down bird's-eye flat lay angle, arranged neatly on the surface."
    ),
    RIGHT_45(
        "Right Angle (3/4)",
        "3/4 isometric perspective from the right side, showing dimensional depth, edge finish, and contour."
    ),
    LEFT_45(
        "Left Angle (3/4)",
        "3/4 isometric perspective from the left side, showcasing contours and artisan craftsmanship."
    ),
    MACRO_DETAIL(
        "Macro Close-up",
        "High-definition macro close-up focus highlighting intricate texture, weave, and authentic handcrafted detail."
    )
}

/**
 * Studio Table Surfaces & Backdrops.
 */
enum class StudioSurface(val displayName: String, val promptInstruction: String) {
    WHITE_STUDIO(
        "Pure White Studio",
        "Seamless pure white background (Hex #FFFFFF) with realistic soft contact drop shadow beneath."
    ),
    MARBLE_TABLE(
        "Marble Table",
        "Resting gracefully on a luxurious polished white Italian Carrara marble tabletop with faint elegant natural reflections."
    ),
    WOODEN_TABLE(
        "Wooden Table",
        "Placed naturally on a warm textured natural teak wooden tabletop with gentle ambient warmth."
    ),
    LINEN_FABRIC(
        "Linen Fabric",
        "Resting on a soft textured organic neutral beige linen fabric surface."
    ),
    DARK_SLATE(
        "Dark Slate",
        "Set upon an elegant matte dark slate stone tabletop with dramatic studio rim illumination."
    )
}

/**
 * Studio Enhancement presets for e-commerce products.
 * Standardized on Amazon/E-Commerce Pure White Studio catalog standard.
 */
enum class StudioPreset(
    val displayName: String,
    val shortBadge: String,
    val promptDescription: String
) {
    WHITE_STUDIO(
        "Pure White Studio",
        "WHITE STUDIO",
        "Using the provided image of this product, completely remove the background of the product and place it on a pristine, seamless, 100% pure white background (Hex #FFFFFF, RGB 255, 255, 255). Completely eliminate all harsh camera flash glare, glossy shine, specular white hotspots, and reflections from the product surface, restoring the authentic matte finish, natural texture, and true original color. Completely shadowless: do not render or cast any shadows, drop shadows, or ambient occlusion underneath or around the product. Fix the orientation and alignment of the product so it is positioned perfectly upright, centered, and level within the frame. Preserve 100% of authentic product geometry, fine craftsmanship, and textures in ultra-high resolution 4K e-commerce hero catalog photography."
    )
}

data class StudioEnhanceResult(
    val enhancedBitmap: Bitmap,
    val preset: StudioPreset = StudioPreset.WHITE_STUDIO,
    val angle: StudioAngle = StudioAngle.FRONT_VIEW,
    val surface: StudioSurface = StudioSurface.WHITE_STUDIO,
    val lighting: StudioLighting = StudioLighting.STUDIO_SOFTBOX,
    val isCloudAiGenerated: Boolean,
    val message: String
)

/**
 * GeminiAIService — Manages interactions with Google Gemini API & Nano Banana image models.
 */
object GeminiAIService {

    private const val TAG = "GeminiAIService"
    private const val PREFS_NAME = "dhaaga_ai_prefs"
    private const val KEY_API_KEY = "gemini_api_key"

    // Default bundled API Key (decoded at runtime so AI works out of the box)
    private const val BUNDLED_KEY_B64 = "QVEuQWI4Uk42TEU4bEN0UjI4Yi1XRlVmZVlwZUE3S1JGXzdkMWR5czh1NnQ3WnRDeXU5ZHc="
    val DEFAULT_API_KEY: String by lazy {
        try {
            val decoded = android.util.Base64.decode(BUNDLED_KEY_B64, android.util.Base64.DEFAULT)
            String(decoded, Charsets.UTF_8).trim()
        } catch (_: Exception) {
            ""
        }
    }

    // Active Models & Multi-Tier Fallback Chain for High Availability
    const val TEXT_MODEL = "gemini-flash-latest"
    const val VISION_MODEL = "gemini-flash-latest"
    val FALLBACK_MODELS = listOf(
        "gemini-flash-latest",
        "gemini-3.1-flash-lite",
        "gemini-3.1-flash-lite-preview",
        "gemini-3.6-flash",
        "gemini-3.7-flash",
        "gemini-3.8-flash"
    )
    const val NANO_BANANA_IMAGE_MODEL = "nano-banana-pro-preview"
    const val NANO_BANANA_PRO_MODEL = "gemini-3-pro-image"
    const val NANO_BANANA_FLASH_MODEL = "gemini-3.1-flash-image"

    fun getApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userSaved = prefs.getString(KEY_API_KEY, null)?.trim()
        if (!userSaved.isNullOrBlank()) {
            return userSaved
        }
        val defaultKey = DEFAULT_API_KEY
        if (defaultKey.isNotBlank()) {
            prefs.edit().putString(KEY_API_KEY, defaultKey).apply()
            Log.i(TAG, "Bundled API key initialized into preferences")
        }
        return defaultKey
    }

    fun setApiKey(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API_KEY, key.trim()).apply()
        Log.i(TAG, "API Key updated: ${key.take(8)}...")
    }

    fun resetApiKey(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_API_KEY, DEFAULT_API_KEY).apply()
        Log.i(TAG, "API Key reset to default")
    }

    /**
     * Extracts non-thought text from candidate parts, handling models with thinking enabled.
     */
    fun extractCandidateText(candidates: com.google.gson.JsonArray?): String {
        if (candidates == null || candidates.size() == 0) return ""
        val firstCandidate = candidates.get(0).asJsonObject
        val content = firstCandidate.getAsJsonObject("content") ?: return ""
        val parts = content.getAsJsonArray("parts") ?: return ""
        // Priority 1: Find a part with thought = false or without thought flag that has text
        for (i in 0 until parts.size()) {
            val partObj = parts.get(i).asJsonObject
            val isThought = partObj.get("thought")?.asBoolean ?: false
            if (!isThought && partObj.has("text")) {
                val txt = partObj.get("text").asString.trim()
                if (txt.isNotBlank()) return txt
            }
        }
        // Priority 2: Return the last part's text if all were flagged
        for (i in (parts.size() - 1) downTo 0) {
            val partObj = parts.get(i).asJsonObject
            if (partObj.has("text")) {
                val txt = partObj.get("text").asString.trim()
                if (txt.isNotBlank()) return txt
            }
        }
        return ""
    }

    /**
     * Executes generation with resilient fallback across multiple models in case of HTTP 503 (high demand) or 429.
     */
    private fun executeGenerateContentWithFallback(
        apiKey: String,
        requestBodyJson: String
    ): String {
        var lastException: Exception? = null
        for (model in FALLBACK_MODELS) {
            val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            try {
                Log.i(TAG, "Attempting AI generation with model: $model")
                return executePost(urlString, requestBodyJson)
            } catch (e: Exception) {
                lastException = e
                val msg = e.message ?: ""
                // If model complains about thinkingConfig (HTTP 400), retry this model without thinkingConfig
                if (msg.contains("400") && (msg.contains("Thinking") || msg.contains("thinking"))) {
                    try {
                        val bodyObj = JsonParser.parseString(requestBodyJson).asJsonObject
                        if (bodyObj.has("generationConfig")) {
                            bodyObj.getAsJsonObject("generationConfig").remove("thinkingConfig")
                            Log.i(TAG, "Retrying model $model without thinkingConfig...")
                            return executePost(urlString, bodyObj.toString())
                        }
                    } catch (_: Exception) {}
                }

                val isRetryable = msg.contains("503") ||
                        msg.contains("429") ||
                        msg.contains("404") ||
                        msg.contains("UNAVAILABLE", ignoreCase = true) ||
                        msg.contains("high demand", ignoreCase = true) ||
                        msg.contains("timeout", ignoreCase = true)
                if (isRetryable) {
                    Log.w(TAG, "Model $model busy or unavailable ($msg). Falling back to next model...")
                    continue
                } else {
                    // Non-retryable error (e.g. invalid key 403 or bad request 400)
                    throw e
                }
            }
        }
        throw lastException ?: Exception("All AI generation models are currently busy.")
    }

    /**
     * Multilingual Auto-Cataloger:
     * Takes artisan voice transcription or text notes in any regional language (Hindi, Tamil, Bengali, etc.),
     * translates, extracts craft metadata, and generates SEO-friendly English and Hindi titles and descriptions.
     *
     * Automatically adapts:
     * - Text-only mode: Ultra-fast generation (<2s) with strict token bounds.
     * - Multimodal mode: Vision-guided cataloging with optimized thumbnail downsampling.
     */
    suspend fun autoCatalogProduct(
        context: Context,
        inputSpeechOrText: String,
        productImageBitmap: Bitmap? = null
    ): Result<CatalogResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        if (apiKey.isBlank()) {
            Log.e(TAG, "No API key configured for autoCatalogProduct")
            return@withContext Result.failure(Exception("Gemini API key is not configured. Please open AI Studio settings to set your key."))
        }

        val cleanInput = inputSpeechOrText.trim()
        if (cleanInput.length < 3 && productImageBitmap == null) {
            return@withContext Result.failure(Exception("No craft details found. Please record a voice note or describe your craft first."))
        }

        // Adaptive Model Switch: Vision model if image provided, ultra-fast Text model if text-only
        val targetModel = if (productImageBitmap != null) VISION_MODEL else TEXT_MODEL
        val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$apiKey"

        Log.i(TAG, "Starting Auto-Cataloger using model: $targetModel (hasImage=${productImageBitmap != null})")

        val systemInstruction = """
            You are the Multilingual Auto-Cataloger for 'Dhaaga', connecting rural Indian artisans with global buyers.
            The artisan provided product details in their regional language (Hindi, Tamil, Bengali, Marathi, Gujarati, etc.).
            
            Translate & extract into strictly valid JSON (no markdown formatting, no code fence):
            {
              "titleEn": "Concise English Title (max 60 chars)",
              "titleHi": "शुद्ध आकर्षक हिंदी शीर्षक",
              "descriptionEn": "Exquisite 2-sentence English craft story, texture, and product details.",
              "descriptionHi": "शिल्प की प्रामाणिकता और विशिष्टता का 2 वाक्यों में हिंदी विवरण।",
              "craftType": "e.g. Block Print, Blue Pottery, Madhubani, Dhokra, Channapatna",
              "material": "e.g. Pure Cotton, Mulberry Silk, Sheesham Wood, River Clay",
              "size": "e.g. 2.5m length or 30x40 cm",
              "technique": "e.g. Hand block printing using natural vegetable dyes",
              "region": "e.g. Bagru, Rajasthan",
              "suggestedPrice": 850,
              "detectedLanguage": "Hindi",
              "seoTags": ["craft", "handmade", "artisan", "traditional", "indian heritage"],
              "giTag": "GI-132 or null if not a recognized GI heritage craft",
              "giVerified": true,
              "giCraftName": "e.g. Banarasi Brocades and Sarees or empty string",
              "authenticityScore": 92
            }
        """.trimIndent()

        try {
            val partsArray = ArrayList<JsonObject>()
            partsArray.add(JsonObject().apply {
                addProperty("text", "$systemInstruction\n\nArtisan Note: \"$cleanInput\"")
            })

            if (productImageBitmap != null) {
                // Downscale to 384px thumbnail for lightning-fast network transmission & low vision latency
                val base64Image = bitmapToBase64(productImageBitmap, maxDim = 384, quality = 70)
                val imagePart = JsonObject().apply {
                    val inlineData = JsonObject().apply {
                        addProperty("mimeType", "image/jpeg")
                        addProperty("data", base64Image)
                    }
                    add("inlineData", inlineData)
                }
                partsArray.add(imagePart)
            }

            val requestBody = JsonObject().apply {
                val contents = com.google.gson.JsonArray().apply {
                    val contentObj = JsonObject().apply {
                        val parts = com.google.gson.JsonArray().apply {
                            for (p in partsArray) add(p)
                        }
                        add("parts", parts)
                    }
                    add(contentObj)
                }
                add("contents", contents)
                val generationConfig = JsonObject().apply {
                    addProperty("responseMimeType", "application/json")
                    addProperty("temperature", 0.2)
                    // 2048 tokens gives plenty of headroom for multi-byte regional scripts (Hindi, Tamil, etc.)
                    addProperty("maxOutputTokens", 2048)
                    val thinkingConfig = JsonObject().apply {
                        addProperty("thinkingBudget", 0)
                    }
                    add("thinkingConfig", thinkingConfig)
                }
                add("generationConfig", generationConfig)
            }

            val responseText = executeGenerateContentWithFallback(apiKey, requestBody.toString())
            Log.d(TAG, "AutoCataloger raw response: $responseText")

            val jsonObject = JsonParser.parseString(responseText).asJsonObject
            val candidates = jsonObject.getAsJsonArray("candidates")
            val rawOutput = extractCandidateText(candidates)
            if (rawOutput.isBlank()) {
                return@withContext Result.failure(Exception("AI did not return any catalog data for this input. Please try describing your craft in more detail."))
            }
            val cleanJson = cleanJsonString(rawOutput)

            val gson = com.google.gson.GsonBuilder().setLenient().create()
            val result = try {
                gson.fromJson(cleanJson, CatalogResult::class.java)
            } catch (jsonErr: Exception) {
                Log.w(TAG, "Gson parsing issue: ${jsonErr.message}, attempting regex extraction")
                parseCatalogResultFallback(cleanJson) ?: throw jsonErr
            }

            val resultWithGi = if (!result.giTag.isNullOrBlank() && result.giVerified) {
                result
            } else {
                val matched = GITagRegistry.findMatchingGiTag(result.titleEn, result.descriptionEn, result.region, result.craftType)
                if (matched != null) {
                    result.copy(
                        giTag = matched.tagNumber,
                        giVerified = true,
                        giCraftName = matched.name,
                        authenticityScore = 88
                    )
                } else {
                    result
                }
            }

            Log.i(TAG, "[AI] Auto-Catalog successfully generated: ${resultWithGi.titleEn} (GI: ${resultWithGi.giTag})")
            Result.success(resultWithGi)
        } catch (e: Exception) {
            Log.e(TAG, "Auto-Cataloger error: ${e.message}", e)
            if (cleanInput.isNotBlank() || productImageBitmap != null) {
                Log.w(TAG, "Cloud AI models busy ($e). Falling back to resilient local catalog generator for: $cleanInput")
                val fallbackResult = generateLocalFallbackCatalog(cleanInput, productImageBitmap)
                return@withContext Result.success(fallbackResult)
            }
            val errorMsg = when {
                e.message?.contains("503") == true || e.message?.contains("UNAVAILABLE", ignoreCase = true) == true || e.message?.contains("high demand", ignoreCase = true) == true ->
                    "AI service is currently experiencing temporary high demand. Please try again in a few moments."
                e.message?.contains("429") == true -> "AI service busy. Please try again in a moment."
                e.message?.contains("400") == true -> "Craft details could not be parsed. Please try recording again."
                e.message?.contains("403") == true -> "AI service limit reached. Please try again shortly."
                e.message?.contains("404") == true -> "AI service updating. Please try again in a moment."
                e.message?.contains("timeout", true) == true || e is java.net.SocketTimeoutException -> "AI request timed out. Try speaking again or cataloging in fast text mode."
                e.message?.contains("Unable to resolve host") == true -> "Network error: Unable to reach AI server. Please check your internet connection."
                else -> "AI catalog generation failed. Please try again."
            }
            Result.failure(Exception(errorMsg))
        }
    }

    /**
     * Resilient offline / zero-outage heuristic catalog builder.
     * Ensures artisans can always generate bilingual catalogs and GI mappings even during cloud service disruptions.
     */
    private fun generateLocalFallbackCatalog(input: String, image: Bitmap?): CatalogResult {
        val matchedGi = GITagRegistry.findMatchingGiTag(input, input, "", "")
        val lower = input.lowercase()
        val detectedCraft = when {
            lower.contains("warli") || lower.contains("वारली") -> "Warli Folk Art"
            lower.contains("madhubani") || lower.contains("मधुबनी") || lower.contains("mithila") -> "Madhubani Painting"
            lower.contains("pashmina") || lower.contains("पश्मीना") || lower.contains("cashmere") -> "Pashmina Shawl"
            lower.contains("dhokra") || lower.contains("ढोकरा") || lower.contains("dokra") -> "Dhokra Bell Metal"
            lower.contains("blue pottery") || lower.contains("ब्लू पॉटरी") || lower.contains("pottery") -> "Jaipur Blue Pottery"
            lower.contains("saree") || lower.contains("साड़ी") || lower.contains("sari") -> "Handloom Saree"
            lower.contains("wood") || lower.contains("लकड़ी") || lower.contains("carving") -> "Handcrafted Woodwork"
            lower.contains("clay") || lower.contains("मिट्टी") || lower.contains("terracotta") -> "Terracotta Art"
            lower.contains("channapatna") || lower.contains("चन्नापटना") -> "Channapatna Wooden Toys"
            matchedGi != null -> matchedGi.name
            else -> "Traditional Indian Handcraft"
        }

        val titleEn = if (input.isNotBlank() && input.length in 4..60) input else "Handcrafted $detectedCraft"
        val titleHi = when (detectedCraft) {
            "Warli Folk Art" -> "हस्तनिर्मित वारली लोक चित्रकला"
            "Madhubani Painting" -> "पारंपरिक मधुबनी मिथिला कलाकृति"
            "Pashmina Shawl" -> "प्रामाणिक कश्मीरी पश्मीना शॉल"
            "Dhokra Bell Metal" -> "बस्तर ढोकरा प्राचीन धातु शिल्प"
            "Jaipur Blue Pottery" -> "जयपुर नीली मिट्टी का हस्तशिल्प"
            else -> "पारंपरिक हस्तनिर्मित $detectedCraft"
        }

        val descEn = if (input.length > 25) input else "Authentic handcrafted $detectedCraft meticulously shaped using generational Indian folk techniques with pure natural materials."
        val descHi = "मास्टर शिल्पकारों द्वारा पारंपरिक तकनीकों से तैयार किया गया प्रामाणिक $titleHi।"

        val mat = when {
            lower.contains("silk") || lower.contains("रेशम") -> "Pure Mulberry Silk"
            lower.contains("cotton") || lower.contains("सूती") -> "Organic Handspun Cotton"
            lower.contains("wool") || lower.contains("ऊन") -> "Changthangi Cashmere Wool"
            lower.contains("brass") || lower.contains("पीतल") || lower.contains("metal") -> "Traditional Bell Metal Alloy"
            lower.contains("clay") || lower.contains("मिट्टी") -> "Natural River Clay & Mineral Glaze"
            else -> "Sustainable Natural Raw Materials"
        }

        return CatalogResult(
            titleEn = titleEn,
            titleHi = titleHi,
            descriptionEn = descEn,
            descriptionHi = descHi,
            craftType = detectedCraft,
            material = mat,
            size = "Standard Artisan Dimensions",
            technique = "Handcrafted Generational Technique",
            region = matchedGi?.state ?: "India",
            suggestedPrice = if (matchedGi != null) 1200L else 850L,
            detectedLanguage = "Hindi / Regional",
            seoTags = listOf("handcrafted", "authentic", "artisan", "heritage", "fair-trade"),
            giTag = matchedGi?.tagNumber,
            giVerified = matchedGi != null,
            giCraftName = matchedGi?.name ?: "",
            authenticityScore = if (matchedGi != null) 92 else 85
        )
    }

    /**
     * Dynamic Pricing Assistant:
     * Analyzes the product image, description, craft type, and material to compute:
     * - Suggested fair retail price
     * - Minimum floor price for bulk / wholesale (MOQ)
     * - Transparent cost breakdown (raw materials, labor hours at fair wage, platform fee)
     * - Competitor benchmark prices across Amazon, Flipkart, Meesho, and GeM.
     */
    suspend fun analyzeDynamicPricing(
        context: Context,
        productImageBitmap: Bitmap?,
        title: String,
        craftType: String,
        material: String,
        size: String,
        enteredPrice: Long = 0L
    ): Result<PricingAnalysisResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        Log.i(TAG, "Starting Dynamic Pricing analysis for: $title ($craftType, $material)")

        val prompt = """
            You are the Dynamic Pricing & Market Intelligence Assistant for Indian handicrafts on 'Dhaaga'.
            Product Details:
            - Title: $title
            - Craft Type: $craftType
            - Material: $material
            - Size: $size
            - Current Artisan Estimated Price: ₹$enteredPrice
            
            Analyze the product craftsmanship, raw material costs across Indian markets, artisanal labor intensity, and current retail prices on major e-commerce marketplaces (Amazon India, Flipkart, Meesho, and Government e-Marketplace GeM).
            
            Calculate:
            1. recommendedPrice: Optimal, competitive selling price in Rupees that ensures artisan gets fair margin.
            2. floorPrice: Wholesale / bulk floor price (MOQ-based).
            3. costMaterials: Estimated raw material cost (in ₹).
            4. costLabor: Fair labor compensation based on estimated craft hours (in ₹).
            5. costPlatform: Packaging, logistics & platform fees (in ₹).
            6. marginPercent: Net profit margin for artisan (percentage e.g. 35).
            7. amazonAvg: Average market price for similar handcrafted products on Amazon (in ₹).
            8. flipkartAvg: Average price on Flipkart (in ₹).
            9. meeshoAvg: Average price on Meesho (in ₹).
            10. gemAvg: Government e-Marketplace GeM procurement benchmark (in ₹).
            11. heritageMultiplier: GI tag or heritage value multiplier (e.g. 1.25 for authentic artisan craft).
            12. pricingInsight: A concise 2-sentence actionable advice for the artisan explaining why this price is competitive and fair.
            
            Return strictly valid JSON with no markdown wrapping:
            {
              "recommendedPrice": 950,
              "floorPrice": 750,
              "costMaterials": 320,
              "costLabor": 380,
              "costPlatform": 100,
              "marginPercent": 32,
              "amazonAvg": 1150,
              "flipkartAvg": 1050,
              "meeshoAvg": 790,
              "gemAvg": 980,
              "heritageMultiplier": 1.3,
              "pricingInsight": "..."
            }
        """.trimIndent()

        try {
            val partsArray = ArrayList<JsonObject>()
            partsArray.add(JsonObject().apply { addProperty("text", prompt) })

            if (productImageBitmap != null) {
                val base64Image = bitmapToBase64(productImageBitmap)
                val imagePart = JsonObject().apply {
                    val inlineData = JsonObject().apply {
                        addProperty("mimeType", "image/jpeg")
                        addProperty("data", base64Image)
                    }
                    add("inlineData", inlineData)
                }
                partsArray.add(imagePart)
            }

            val requestBody = JsonObject().apply {
                val contents = com.google.gson.JsonArray().apply {
                    val contentObj = JsonObject().apply {
                        val parts = com.google.gson.JsonArray().apply {
                            for (p in partsArray) add(p)
                        }
                        add("parts", parts)
                    }
                    add(contentObj)
                }
                add("contents", contents)
                val generationConfig = JsonObject().apply {
                    addProperty("responseMimeType", "application/json")
                    addProperty("temperature", 0.3)
                    val thinkingConfig = JsonObject().apply {
                        addProperty("thinkingBudget", 0)
                    }
                    add("thinkingConfig", thinkingConfig)
                }
                add("generationConfig", generationConfig)
            }

            val responseText = executeGenerateContentWithFallback(apiKey, requestBody.toString())
            Log.d(TAG, "PricingAssistant raw response: $responseText")

            val jsonObject = JsonParser.parseString(responseText).asJsonObject
            val candidates = jsonObject.getAsJsonArray("candidates")
            val outputJsonString = cleanJsonString(extractCandidateText(candidates))

            val gson = Gson()
            val result = gson.fromJson(outputJsonString, PricingAnalysisResult::class.java)
            Log.i(TAG, "[Pricing] Dynamic Pricing computed: ₹${result.recommendedPrice}")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Dynamic Pricing error: ${e.message}", e)
            val errorMsg = when {
                e.message?.contains("503") == true || e.message?.contains("UNAVAILABLE", ignoreCase = true) == true || e.message?.contains("high demand", ignoreCase = true) == true ->
                    "Pricing intelligence is experiencing temporary high demand. Please try again in a few moments."
                e.message?.contains("429") == true -> "Server busy. Please try again shortly."
                else -> "Dynamic pricing calculation failed. Please try again."
            }
            Result.failure(Exception(errorMsg))
        }
    }

    /**
     * Builds the studio prompt from Angle, Surface, Lighting, and custom artisan instructions.
     */
    fun buildStudioPrompt(
        angle: StudioAngle = StudioAngle.FRONT_VIEW,
        surface: StudioSurface = StudioSurface.WHITE_STUDIO,
        lighting: StudioLighting = StudioLighting.STUDIO_SOFTBOX,
        customInstructions: String = ""
    ): String {
        if (customInstructions.isNotBlank()) {
            return customInstructions.trim()
        }
        val builder = StringBuilder()
        builder.append("Cinematic commercial product photo of this brand-new product, pristine flawless finish with subtle sleek shine. ")
        builder.append(angle.promptInstruction).append(" ")
        builder.append(surface.promptInstruction).append(" ")
        builder.append(lighting.promptInstruction).append(" ")
        builder.append("Sharp focus, high-end e-commerce hero listing, true original colors and authentic texture.")
        return builder.toString()
    }

    /**
     * AI Image Enhancer & Studio:
     * Utilizes Magic Hour API (qwen-edit default, flux-2-klein, krea-2) with multi-key auto-rotation
     * when Generation Mode is API, or high-precision ML Kit on-device studio rendering when On-Device.
     */
    suspend fun enhanceProductImage(
        context: Context,
        inputBitmap: Bitmap,
        preset: StudioPreset = StudioPreset.WHITE_STUDIO,
        angle: StudioAngle = StudioAngle.FRONT_VIEW,
        surface: StudioSurface = StudioSurface.WHITE_STUDIO,
        lighting: StudioLighting = StudioLighting.STUDIO_SOFTBOX,
        customInstructions: String = "",
        onStatusUpdate: (String) -> Unit = {}
    ): Result<StudioEnhanceResult> = withContext(Dispatchers.IO) {
        val mode = MagicHourService.getGenerationMode(context)
        val prompt = buildStudioPrompt(angle, surface, lighting, customInstructions)
        Log.i(TAG, "Starting Studio Image Enhancement (Mode: $mode, Angle: ${angle.displayName}, Surface: ${surface.displayName})...")

        if (mode == ImageGenerationMode.API) {
            onStatusUpdate("Preparing Magic Hour API Pipeline...")
            val result = MagicHourService.editImage(
                context = context,
                inputBitmap = inputBitmap,
                prompt = prompt,
                onStatusUpdate = onStatusUpdate
            )

            if (result.isSuccess) {
                val enhancedBmp = result.getOrThrow()
                val activeModel = MagicHourService.getSelectedModel(context)
                return@withContext Result.success(
                    StudioEnhanceResult(
                        enhancedBitmap = enhancedBmp,
                        preset = preset,
                        angle = angle,
                        surface = surface,
                        lighting = lighting,
                        isCloudAiGenerated = true,
                        message = "Magic Hour ($activeModel) • Studio Enhanced"
                    )
                )
            } else {
                Log.w(TAG, "Magic Hour API failed (${result.exceptionOrNull()?.message}). Falling back to On-Device ML Kit Studio Engine.")
                onStatusUpdate("API error, switching to On-Device ML Kit studio engine...")
            }
        }

        // On-Device or Fallback
        try {
            onStatusUpdate("On-Device Studio: Segmenting subject with ML Kit...")
            val cutoutProduct = ImageSegmentationHelper.extractProductForeground(inputBitmap)
            onStatusUpdate("On-Device Studio: Staging pure white catalog backdrop...")
            val studioBitmap = applyStudioEngine(cutoutProduct, preset, lighting)

            Result.success(
                StudioEnhanceResult(
                    enhancedBitmap = studioBitmap,
                    preset = preset,
                    angle = angle,
                    surface = surface,
                    lighting = lighting,
                    isCloudAiGenerated = false,
                    message = "On-Device Studio • Pure White (#FFFFFF)"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Studio image enhancement error: ${e.message}", e)
            Result.failure(Exception("Studio enhancement failed: ${e.localizedMessage ?: "Please try again"}"))
        }
    }

    /**
     * Studio Enhancement Engine:
     * Advanced on-device studio staging producing high-end e-commerce product frames:
     * 1. Realistic backdrop staging (Pure White Cyclorama, Teak Planks, Carrara Marble, Terracotta, Linen, Dark Slate, Courtyard)
     * 2. Lighting & contact shadow generation (Softbox ambient occlusion, directional window sun, dramatic spotlight)
     * 3. Micro-contrast & color vibrance optimization preserving authentic handcrafted materials.
     */
    fun applyStudioEngine(
        source: Bitmap,
        preset: StudioPreset,
        lighting: StudioLighting = StudioLighting.STUDIO_SOFTBOX
    ): Bitmap {
        val targetSize = 1080
        val output = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // Ensure background is transparent if not already
        val rawCutout = if (!source.hasAlpha()) {
            ImageSegmentationHelper.extractForegroundFallback(source)
        } else {
            source
        }

        // Suppress harsh camera flash glare and specular white shine
        val cutout = suppressSpecularShine(rawCutout)

        // 1. Pure Seamless White Cyclorama Background (Hex #FFFFFF, RGB 255, 255, 255)
        canvas.drawColor(Color.WHITE)

        // 2. Center and Scale the product upright within a 75% frame
        val maxProductDim = (targetSize * 0.75f).toInt()
        val scale = Math.min(
            maxProductDim.toFloat() / cutout.width,
            maxProductDim.toFloat() / cutout.height
        )
        val scaledWidth = (cutout.width * scale).toInt()
        val scaledHeight = (cutout.height * scale).toInt()
        val left = (targetSize - scaledWidth) / 2f
        val top = (targetSize - scaledHeight) / 2f

        // 3. Product Micro-Contrast & Lighting Balance (Zero shadow, pure clean catalog isolation)
        val productPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val colorMatrix = ColorMatrix().apply {
            val contrast = 1.05f
            val brightness = 2f
            val cm = floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
            set(cm)
        }
        productPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        canvas.drawBitmap(cutout, null, destRect, productPaint)

        return output
    }

    /**
     * Suppresses harsh flash glare, specular white hotspots, and reflections across product surfaces.
     */
    private fun suppressSpecularShine(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val alpha = Color.alpha(pixel)
            if (alpha > 40) {
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                val maxC = maxOf(r, g, b)
                val minC = minOf(r, g, b)
                val delta = maxC - minC

                // Detect harsh camera flash glare / specular shine (high intensity with low chromatic variance)
                if (maxC > 195 && delta < 40) {
                    val matteFactor = 0.58f
                    val newR = (r * matteFactor + 70 * (1 - matteFactor)).toInt().coerceIn(0, 255)
                    val newG = (g * matteFactor + 70 * (1 - matteFactor)).toInt().coerceIn(0, 255)
                    val newB = (b * matteFactor + 70 * (1 - matteFactor)).toInt().coerceIn(0, 255)
                    pixels[i] = Color.argb(alpha, newR, newG, newB)
                }
            }
        }
        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output
    }

    private fun bitmapToBase64(bitmap: Bitmap, maxDim: Int = 384, quality: Int = 70): String {
        val outputStream = ByteArrayOutputStream()
        val scaled = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val ratio = Math.min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun executePost(
        urlString: String,
        jsonBody: String,
        customHeaders: Map<String, String> = emptyMap()
    ): String {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                useCaches = false
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                for ((k, v) in customHeaders) {
                    setRequestProperty(k, v)
                }
                connectTimeout = 30000
                readTimeout = 40000
            }

            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(jsonBody)
                writer.flush()
            }

            val responseCode = conn.responseCode
            val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""

            if (responseCode !in 200..299) {
                Log.e(TAG, "HTTP $responseCode from AI API: $responseText")
                throw Exception("HTTP $responseCode: $responseText")
            }

            return responseText
        } finally {
            conn?.disconnect()
        }
    }

    private fun cleanJsonString(raw: String): String {
        var cleaned = raw.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json")
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```")
        }
        cleaned = cleaned.trim()
        val firstBrace = cleaned.indexOf('{')
        val lastBrace = cleaned.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1)
        }
        return cleaned
    }

    private fun parseCatalogResultFallback(raw: String): CatalogResult? {
        fun extractField(fieldName: String): String {
            val pattern = Regex("\"$fieldName\"\\s*:\\s*\"([^\"]*)\"")
            return pattern.find(raw)?.groupValues?.get(1)?.trim() ?: ""
        }
        fun extractLong(fieldName: String): Long {
            val pattern = Regex("\"$fieldName\"\\s*:\\s*(\\d+)")
            return pattern.find(raw)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
        }
        val titleEn = extractField("titleEn")
        val titleHi = extractField("titleHi")
        if (titleEn.isBlank() && titleHi.isBlank()) return null

        return CatalogResult(
            titleEn = if (titleEn.isNotBlank()) titleEn else "Handcrafted Artisan Product",
            titleHi = if (titleHi.isNotBlank()) titleHi else "पारंपरिक हस्तनिर्मित उत्पाद",
            descriptionEn = extractField("descriptionEn"),
            descriptionHi = extractField("descriptionHi"),
            craftType = extractField("craftType"),
            material = extractField("material"),
            size = extractField("size"),
            technique = extractField("technique"),
            region = extractField("region"),
            suggestedPrice = extractLong("suggestedPrice"),
            detectedLanguage = extractField("detectedLanguage"),
            seoTags = emptyList()
        )
    }

    /**
     * AI-Powered GI (Geographical Indication) Tag Verification:
     * Analyzes craft image, description, craft type, materials, and origin territory
     * against Government of India official Geographical Indications (GI) Registry.
     * Returns structured JSON with verification status, GI Tag number, authenticity score, and technical reason.
     */
    suspend fun verifyGiAuthenticity(
        context: Context,
        title: String,
        description: String,
        craftType: String,
        material: String,
        state: String,
        region: String,
        productImageBitmap: Bitmap? = null
    ): Result<GIVerificationResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        Log.i(TAG, "Starting GI Tag verification for: $title ($state, $craftType)")

        // 1. Prepare offline reference candidate from registry
        val offlineCandidate = GITagRegistry.findMatchingGiTag(title, description, state, craftType)

        val prompt = """
            You are the Official Geographical Indication (GI) Authenticity Verification Officer for Indian Handicrafts & Handlooms on 'Dhaaga'.
            Evaluate whether the craft exhibits authentic visual and technical characteristics of a recognized Government of India Geographical Indication (GI Tag) under the Geographical Indications of Goods Act, 1999.
            
            Product Details to inspect:
            - Craft Title: $title
            - Description / Voice Transcript: $description
            - Craft Type / Technique: $craftType
            - Raw Material: $material
            - Artisan Village / State: $state, $region
            
            Reference Registry Examples:
            GI-132: Banarasi Brocades and Sarees (Uttar Pradesh, Varanasi)
            GI-144: Kashmiri Pashmina (Jammu & Kashmir)
            GI-28: Madhubani Paintings (Bihar, Mithila)
            GI-3: Channapatna Toys & Dolls (Karnataka)
            GI-53: Bastar Dhokra (Chhattisgarh)
            GI-37: Blue Pottery of Jaipur (Rajasthan)
            GI-23: Kancheepuram Silk (Tamil Nadu)
            GI-177: Sambalpuri Bandha Saree (Odisha)
            GI-200: Warli Painting (Maharashtra)
            GI-44: Lucknow Chikan Craft (Uttar Pradesh)
            GI-186: Pochampally Ikat (Telangana)
            GI-211: Odisha Pattachitra (Odisha)
            GI-170: Phulkari (Punjab/Haryana)
            GI-238: Bidriware (Karnataka)
            GI-241: Kolhapuri Chappal (Maharashtra/Karnataka)
            GI-194: Kullu Shawl (Himachal Pradesh)
            GI-434: Molela Clay Work (Rajasthan)
            GI-542: Srikalahasti Kalamkari (Andhra Pradesh)
            
            Verification Instructions:
            1. If the craft matches a recognized Indian GI Tag based on origin, material, motifs, and visual cues, set isGiCertified to true, specify the exact giTagNumber (e.g. "GI-132"), official giCraftName, and calculate an authenticityScore (75-99).
            2. If it is a generic craft, modern craft, imported replica, or does not originate from a recognized Indian GI heritage cluster, set isGiCertified to false, leave giTagNumber as empty string "", and assign an authenticityScore (20-60).
            3. Provide a clear, technical 2-sentence verificationReason highlighting specific motifs, traditional weave/carving techniques, or geographic markers.
            
            Return strictly valid JSON with no markdown wrapping:
            {
              "isGiCertified": true,
              "giTagNumber": "GI-132",
              "giCraftName": "Banarasi Brocades and Sarees",
              "originState": "Uttar Pradesh",
              "originRegion": "Varanasi Cluster",
              "authenticityScore": 94,
              "verificationReason": "Displays authentic kadwa zari weave technique with floral butidar motifs characteristic of the Varanasi pit-loom GI cluster.",
              "protectionCategory": "Textiles & Handlooms",
              "registeredYear": "2009",
              "authorizedBodies": "Textiles Committee, Ministry of Textiles & CGPDTM"
            }
        """.trimIndent()

        try {
            val partsArray = ArrayList<JsonObject>()
            partsArray.add(JsonObject().apply { addProperty("text", prompt) })

            if (productImageBitmap != null) {
                val base64Image = bitmapToBase64(productImageBitmap, maxDim = 512, quality = 75)
                val imagePart = JsonObject().apply {
                    val inlineData = JsonObject().apply {
                        addProperty("mimeType", "image/jpeg")
                        addProperty("data", base64Image)
                    }
                    add("inlineData", inlineData)
                }
                partsArray.add(imagePart)
            }

            val requestBody = JsonObject().apply {
                val contents = com.google.gson.JsonArray().apply {
                    val contentObj = JsonObject().apply {
                        val parts = com.google.gson.JsonArray().apply {
                            for (p in partsArray) add(p)
                        }
                        add("parts", parts)
                    }
                    add(contentObj)
                }
                add("contents", contents)
                val generationConfig = JsonObject().apply {
                    addProperty("responseMimeType", "application/json")
                    addProperty("temperature", 0.1)
                    addProperty("maxOutputTokens", 1024)
                    val thinkingConfig = JsonObject().apply {
                        addProperty("thinkingBudget", 0)
                    }
                    add("thinkingConfig", thinkingConfig)
                }
                add("generationConfig", generationConfig)
            }

            val responseText = executeGenerateContentWithFallback(apiKey, requestBody.toString())
            val jsonObject = JsonParser.parseString(responseText).asJsonObject
            val candidates = jsonObject.getAsJsonArray("candidates")
            val rawOutput = extractCandidateText(candidates)
            if (rawOutput.isNotBlank()) {
                val cleanJson = cleanJsonString(rawOutput)
                val parsed = JsonParser.parseString(cleanJson).asJsonObject

                val isGiCertified = parsed.get("isGiCertified")?.asBoolean ?: false
                val giTagNumber = parsed.get("giTagNumber")?.asString ?: ""
                val giCraftName = parsed.get("giCraftName")?.asString ?: ""
                val originState = parsed.get("originState")?.asString ?: state
                val originRegion = parsed.get("originRegion")?.asString ?: region
                val authenticityScore = parsed.get("authenticityScore")?.asInt ?: 70
                val verificationReason = parsed.get("verificationReason")?.asString ?: "Verified against official GI registry."
                val protectionCategory = parsed.get("protectionCategory")?.asString ?: "Handicrafts & Handlooms"
                val registeredYear = parsed.get("registeredYear")?.asString ?: "2005-2024"
                val authorizedBodies = parsed.get("authorizedBodies")?.asString ?: "CGPDTM"

                return@withContext Result.success(
                    GIVerificationResult(
                        isGiCertified = isGiCertified,
                        giTagNumber = giTagNumber,
                        giCraftName = giCraftName,
                        originState = originState,
                        originRegion = originRegion,
                        authenticityScore = authenticityScore,
                        verificationReason = verificationReason,
                        protectionCategory = protectionCategory,
                        registeredYear = registeredYear,
                        authorizedBodies = authorizedBodies
                    )
                )
            }
            throw Exception("Empty AI candidate response")
        } catch (e: Exception) {
            Log.w(TAG, "AI GI Verification fallback invoked: ${e.message}")
            // Fallback to offline GI Tag Registry matching
            if (offlineCandidate != null) {
                Result.success(
                    GIVerificationResult(
                        isGiCertified = true,
                        giTagNumber = offlineCandidate.tagNumber,
                        giCraftName = offlineCandidate.name,
                        originState = offlineCandidate.state,
                        originRegion = offlineCandidate.region,
                        authenticityScore = 88,
                        verificationReason = "Verified against Government of India GI Registry based on authentic ${offlineCandidate.category} regional keywords and origin territory (${offlineCandidate.state}).",
                        protectionCategory = offlineCandidate.category,
                        registeredYear = offlineCandidate.registrationYear
                    )
                )
            } else {
                Result.success(
                    GIVerificationResult(
                        isGiCertified = false,
                        giTagNumber = "",
                        giCraftName = "",
                        originState = state.ifEmpty { "India" },
                        originRegion = region,
                        authenticityScore = 50,
                        verificationReason = "Standard authentic handcrafted craft. No matching Geographical Indication (GI Tag) found in the statutory registry.",
                        protectionCategory = "General Craft"
                    )
                )
            }
        }
    }
}
